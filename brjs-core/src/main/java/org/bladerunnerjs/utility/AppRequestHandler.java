package org.bladerunnerjs.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;

import com.google.common.base.Joiner;


public class AppRequestHandler
{

	private static final String BR_LOCALE_UTILITY_LIBNAME = "br-locale-utility";
	private static final String BR_LOCALE_UTILITY_FILENAME = "LocaleUtility.js";
	
	public static final String LOCALE_FORWARDING_REQUEST = "locale-forwarding-request";
	public static final String INDEX_PAGE_REQUEST = "index-page-request";
	public static final String UNVERSIONED_BUNDLE_REQUEST = "unversioned-bundle-request";
	public static final String BUNDLE_REQUEST = "bundle-request";
	public static final String WORKBENCH_LOCALE_FORWARDING_REQUEST = "workbench-locale-forwarding-request";
	public static final String WORKBENCH_INDEX_PAGE_REQUEST = "workbench-index-page-request";
	public static final String WORKBENCH_BUNDLE_REQUEST = "workbench-bundle-request";

	private final App app;
	private final MemoizedValue<ContentPathParser> contentPathParser;

	public AppRequestHandler(App app)
	{
		this.app = app;
		contentPathParser = new MemoizedValue<>("AppRequestHandler.contentPathParser", app.root(), app.dir());
	}

	public boolean canHandleLogicalRequest(String requestPath)
	{
		return getContentPathParser().canParseRequest(requestPath);
	}
	
	public ResponseContent handleLogicalRequest(String requestPath, UrlContentAccessor contentAccessor) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		ParsedContentPath parsedContentPath = getContentPathParser().parse(requestPath);
		Map<String, String> pathProperties = parsedContentPath.properties;
		String aspectName = getAspectName(requestPath, pathProperties);

		String devVersion = app.root().getAppVersionGenerator().getDevVersion();

		switch (parsedContentPath.formName)
		{
			case LOCALE_FORWARDING_REQUEST:
			case WORKBENCH_LOCALE_FORWARDING_REQUEST:
				return getLocaleForwardingPageContent(app.root(), contentAccessor, devVersion);

			case INDEX_PAGE_REQUEST:
				return getIndexPageContent(app.aspect(aspectName), new Locale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);

			case WORKBENCH_INDEX_PAGE_REQUEST:
				return getIndexPageContent(app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench(), new Locale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);
			
			case UNVERSIONED_BUNDLE_REQUEST:
				return app.aspect(aspectName).handleLogicalRequest("/"+pathProperties.get("content-path"), contentAccessor, devVersion);
				
			case BUNDLE_REQUEST:
				return app.aspect(aspectName).handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);

			case WORKBENCH_BUNDLE_REQUEST:
				return app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench().handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);
		}
		
		throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
	}

	public String createRequest(String requestFormName, String... args) throws MalformedTokenException
	{
		return getContentPathParser().createRequest(requestFormName, args);
	}
	
	public ResponseContent getIndexPageContent(BrowsableNode browsableNode, Locale locale, String version, UrlContentAccessor contentAccessor, RequestMode requestMode) throws ContentProcessingException, ResourceNotFoundException {
		
		File indexPage = (browsableNode.file("index.jsp").exists()) ? browsableNode.file("index.jsp") : browsableNode.file("index.html");
		try {
			if ( !Arrays.asList(app.appConf().getLocales()).contains(locale) ) {
				throw new ResourceNotFoundException("The locale '"+locale+"' is not a valid locale for this app.");
			}
			
			String pathRelativeToApp = RelativePathUtility.get(app.root(), app.dir(), indexPage);
			ByteArrayOutputStream indexPageContent = new ByteArrayOutputStream();
			contentAccessor.writeLocalUrlContentsToOutputStream(pathRelativeToApp, indexPageContent);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			String browserCharacterEncoding = browsableNode.root().bladerunnerConf().getBrowserCharacterEncoding();
			try (Writer writer =  new OutputStreamWriter(byteArrayOutputStream, browserCharacterEncoding)) {
				browsableNode.filterIndexPage(indexPageContent.toString(), locale, version, writer, requestMode);
			}

			return new CharResponseContent( browsableNode.root(), byteArrayOutputStream.toString() );
		}
		catch (IOException | ConfigException | ModelOperationException e) {
			throw new ContentProcessingException(e, "Error when trying to write the index page for " + RelativePathUtility.get(browsableNode.root(), browsableNode.root().dir(),indexPage));
		}
	}

	private String getAspectName(String requestPath, Map<String, String> contentPathProperties) throws MalformedRequestException
	{
		String aspectName = contentPathProperties.get("aspect");

		if (aspectName.equals("default/"))
		{
			throw new MalformedRequestException(requestPath, "The '/default' prefix should be omitted for the default aspect.");
		}
		else if (aspectName.isEmpty())
		{
			aspectName = "default";
		}
		else
		{
			aspectName = aspectName.substring(0, aspectName.length() - 1);
		}

		return aspectName;
	}

	public ResponseContent getLocaleForwardingPageContent(BRJS brjs, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		StringWriter localeForwardingPage = new StringWriter();
		
		SdkJsLib localeForwarderLib = app.root().sdkLib(BR_LOCALE_UTILITY_LIBNAME);
		try (Reader localeForwarderReader = new FileReader( localeForwarderLib.file(BR_LOCALE_UTILITY_FILENAME) ) ) {
			
			localeForwardingPage.write("<head>\n");
			localeForwardingPage.write("<noscript><meta http-equiv='refresh' content='0; url=" + app.appConf().getDefaultLocale() + "/'></noscript>\n");
			localeForwardingPage.write("<script type='text/javascript'>\n");
			IOUtils.write(AppMetadataUtility.getBundlePathJsData(app, version), localeForwardingPage);
			localeForwardingPage.write("\n");
			IOUtils.copy(localeForwarderReader, localeForwardingPage);
			localeForwardingPage.write("\n");			
			localeForwardingPage.write("function forwardToLocalePage() {\n");
			localeForwardingPage.write("	var localeCookie = LocaleUtility.getCookie(window.$BRJS_LOCALE_COOKIE_NAME);\n");
			localeForwardingPage.write("	var browserAcceptedLocales = LocaleUtility.getBrowserAcceptedLocales();\n");
			localeForwardingPage.write("	var appLocales = window.$BRJS_APP_LOCALES;\n");
			localeForwardingPage.write("	var activeLocale = LocaleUtility.getActiveLocale( localeCookie, browserAcceptedLocales, appLocales );\n");
			localeForwardingPage.write("	window.location = LocaleUtility.getLocalizedPageUrl( window.location.href, activeLocale );\n");
			localeForwardingPage.write("}\n");
			
			localeForwardingPage.write("\n</script>\n");
			localeForwardingPage.write("</head>\n");
			localeForwardingPage.write("<body onload='forwardToLocalePage()'></body>\n");
			
			return new CharResponseContent( brjs, localeForwardingPage.toString() );
		}
		catch (IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
	}

	private ContentPathParser getContentPathParser()
	{
		return contentPathParser.value(() -> {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				// NOTE: <aspect> definition ends with a / - so <aspect>workbench == myAspect-workbench
				.accepts("<aspect>").as(LOCALE_FORWARDING_REQUEST)
					.and("<aspect><locale>/").as(INDEX_PAGE_REQUEST)
					.and("<aspect>static/<content-path>").as(UNVERSIONED_BUNDLE_REQUEST)
					.and("<aspect>v/<version>/<content-path>").as(BUNDLE_REQUEST)
					.and("<aspect>workbench/<bladeset>/<blade>/").as(WORKBENCH_LOCALE_FORWARDING_REQUEST)
					.and("<aspect>workbench/<bladeset>/<blade>/<locale>/").as(WORKBENCH_INDEX_PAGE_REQUEST)
					.and("<aspect>workbench/<bladeset>/<blade>/v/<version>/<content-path>").as(WORKBENCH_BUNDLE_REQUEST)
				.where("aspect").hasForm("((" + getAspectNames() + ")/)?")
					.and("workbench").hasForm(ContentPathParserBuilder.NAME_TOKEN)
					.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
					.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
					.and("version").hasForm( app.root().getAppVersionGenerator().getVersionPattern() )
					.and("locale").hasForm(Locale.LANGUAGE_AND_COUNTRY_CODE_FORMAT)
					.and("content-path").hasForm(ContentPathParserBuilder.PATH_TOKEN);
			
			return contentPathParserBuilder.build();
		});
	}

	private String getAspectNames()
	{
		List<String> aspectNames = new ArrayList<>();

		for (Aspect aspect : app.aspects())
		{
			aspectNames.add(aspect.getName());
		}

		return Joiner.on("|").join(aspectNames);
	}
}
