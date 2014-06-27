package org.bladerunnerjs.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
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
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.ContentOutputStream;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
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
	
	public void handleLogicalRequest(String requestPath, ContentOutputStream os) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		ParsedContentPath parsedContentPath = getContentPathParser().parse(requestPath);
		Map<String, String> pathProperties = parsedContentPath.properties;
		String aspectName = getAspectName(requestPath, pathProperties);

		String devVersion = app.root().getAppVersionGenerator().getDevVersion();

		switch (parsedContentPath.formName)
		{
			case LOCALE_FORWARDING_REQUEST:
			case WORKBENCH_LOCALE_FORWARDING_REQUEST:
				writeLocaleForwardingPage(os, devVersion);
				break;

			case INDEX_PAGE_REQUEST:
				writeIndexPage(app.aspect(aspectName), new Locale(pathProperties.get("locale")), devVersion, os, RequestMode.Dev);
				break;

			case WORKBENCH_INDEX_PAGE_REQUEST:
				writeIndexPage(app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench(), new Locale(pathProperties.get("locale")), devVersion, os, RequestMode.Dev);
				break;
			
			case UNVERSIONED_BUNDLE_REQUEST:
				app.aspect(aspectName).handleLogicalRequest("/"+pathProperties.get("content-path"), os, devVersion);
				break;
				
			case BUNDLE_REQUEST:
				app.aspect(aspectName).handleLogicalRequest(pathProperties.get("content-path"), os, devVersion);
				break;

			case WORKBENCH_BUNDLE_REQUEST:
				app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench().handleLogicalRequest(pathProperties.get("content-path"), os, devVersion);
				break;
		}
	}

	public String createRequest(String requestFormName, String... args) throws MalformedTokenException
	{
		return getContentPathParser().createRequest(requestFormName, args);
	}
	
	public void writeIndexPage(BrowsableNode browsableNode, Locale locale, String version, ContentOutputStream os, RequestMode requestMode) throws ContentProcessingException, ResourceNotFoundException {
		
		File indexPage = (browsableNode.file("index.jsp").exists()) ? browsableNode.file("index.jsp") : browsableNode.file("index.html");
		try {
			if ( !Arrays.asList(app.appConf().getLocales()).contains(locale) ) {
				throw new ResourceNotFoundException("The locale '"+locale+"' is not a valid locale for this app.");
			}
			
			String pathRelativeToApp = RelativePathUtility.get(app.root(), app.dir(), indexPage);
			StringWriter indexPageContent = new StringWriter();
			os.writeLocalUrlContentsToWriter(pathRelativeToApp, indexPageContent);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			String browserCharacterEncoding = browsableNode.root().bladerunnerConf().getBrowserCharacterEncoding();
			try (Writer writer =  new OutputStreamWriter(byteArrayOutputStream, browserCharacterEncoding)) {
				browsableNode.filterIndexPage(indexPageContent.toString(), locale, version, writer, requestMode);
			}

			os.write(byteArrayOutputStream.toByteArray());
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

	public void writeLocaleForwardingPage(OutputStream os, String version) throws ContentProcessingException {
		SdkJsLib localeForwarderLib = app.root().sdkLib(BR_LOCALE_UTILITY_LIBNAME);
		try(Writer writer = new OutputStreamWriter(os, app.root().bladerunnerConf().getBrowserCharacterEncoding());
				Reader localeForwarderReader = new FileReader( localeForwarderLib.file(BR_LOCALE_UTILITY_FILENAME) ) ) {
			
			writer.write("<head>\n");
			writer.write("<noscript><meta http-equiv='refresh' content='0; url=" + app.appConf().getDefaultLocale() + "/'></noscript>\n");
			writer.write("<script type='text/javascript'>\n");
			IOUtils.write(ServedAppMetadataUtility.getBundlePathJsData(app, version), writer);
			writer.write("\n");
			IOUtils.copy(localeForwarderReader, writer);
			writer.write("\n");			
			writer.write("function forwardToLocalePage() {\n");
			writer.write("	var localeCookie = LocaleUtility.getCookie(window.$BRJS_LOCALE_COOKIE_NAME);\n");
			writer.write("	var browserAcceptedLocales = LocaleUtility.getBrowserAcceptedLocales();\n");
			writer.write("	var appLocales = window.$BRJS_APP_LOCALES;\n");
			writer.write("	var activeLocale = LocaleUtility.getActiveLocale( localeCookie, browserAcceptedLocales, appLocales );\n");
			writer.write("	window.location = LocaleUtility.getLocalizedPageUrl( window.location.href, activeLocale );\n");
			writer.write("}\n");
			
			writer.write("\n</script>\n");
			writer.write("</head>\n");
			writer.write("<body onload='forwardToLocalePage()'></body>\n");
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
