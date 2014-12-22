package org.bladerunnerjs.utility;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundleSet;
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
import org.bladerunnerjs.plugin.ContentPlugin;
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
	public static final String WORKBENCH_BLADESET_LOCALE_FORWARDING_REQUEST = "workbench-bladeset-locale-forwarding-request";
	public static final String WORKBENCH_BLADESET_INDEX_PAGE_REQUEST = "workbench-bladeset-index-page-request";
	public static final String WORKBENCH_BLADESET_BUNDLE_REQUEST = "workbench-bladeset-bundle-request";

	private final App app;
	private final MemoizedValue<ContentPathParser> contentPathParser;

	public AppRequestHandler(App app)
	{
		this.app = app;
		contentPathParser = new MemoizedValue<>(app.getName()+" - AppRequestHandler.contentPathParser", app.root(), app.dir());
	}

	public boolean canHandleLogicalRequest(String requestPath)
	{
		boolean canHandleRequest = getContentPathParser().canParseRequest(requestPath);
		if (canHandleRequest) {
			ParsedContentPath parsedRequest = parseRequest(requestPath);
			if (parsedRequest.formName.equals(UNVERSIONED_BUNDLE_REQUEST)) {
				/* since unversioned requests (/myApp/somePlugin/file.txt) could also be a request to a custom servlet
				 * if the request type is an unversioned bundle request we must first check that a content plugin can ultimately handle it
				 */
				String contentPath = parsedRequest.properties.get("content-path");
				ContentPlugin contentProvider = app.root().plugins().contentPluginForLogicalPath(contentPath);
				if (contentProvider == null) {
					return false;
				}
				return true;
			}
			return true;
		}
		return false;
	}

	private ParsedContentPath parseRequest(String requestPath)
	{
		try
		{
			return getContentPathParser().parse(requestPath);
		}
		catch (MalformedRequestException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public ResponseContent handleLogicalRequest(String requestPath, UrlContentAccessor contentAccessor) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, ModelOperationException {
		ParsedContentPath parsedContentPath = parseRequest(requestPath);
		Map<String, String> pathProperties = parsedContentPath.properties;
		String aspectName = getAspectName(requestPath, pathProperties);

		String devVersion = app.root().getAppVersionGenerator().getDevVersion();

		switch (parsedContentPath.formName)
		{
			case LOCALE_FORWARDING_REQUEST:
			case WORKBENCH_LOCALE_FORWARDING_REQUEST:
				return getLocaleForwardingPageContent(app.aspect(aspectName).getBundleSet(), contentAccessor, devVersion);
				
			case WORKBENCH_BLADESET_LOCALE_FORWARDING_REQUEST:
				return getLocaleForwardingPageContent(app.aspect(aspectName).getBundleSet(), contentAccessor, devVersion);	

			case INDEX_PAGE_REQUEST:
				return getIndexPageContent(app.aspect(aspectName), new Locale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);

			case WORKBENCH_INDEX_PAGE_REQUEST:
				return getIndexPageContent(app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench(), new Locale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);
			
			case WORKBENCH_BLADESET_INDEX_PAGE_REQUEST:
				return getIndexPageContent(app.bladeset(pathProperties.get("bladeset")).workbench(), new Locale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);
			
			case UNVERSIONED_BUNDLE_REQUEST:
				return app.aspect(aspectName).handleLogicalRequest("/"+pathProperties.get("content-path"), contentAccessor, devVersion);
				
			case BUNDLE_REQUEST:
				return app.aspect(aspectName).handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);

			case WORKBENCH_BUNDLE_REQUEST:
				return app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench().handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);
			
			case WORKBENCH_BLADESET_BUNDLE_REQUEST:
				return app.bladeset(pathProperties.get("bladeset")).workbench().handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);
				
		}
		
		throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
	}

	public String createRequest(String requestFormName, String... args) throws MalformedTokenException
	{
		return getContentPathParser().createRequest(requestFormName, args);
	}
	
	public MemoizedFile getIndexPage(BrowsableNode browsableNode) {
		return (browsableNode.file("index.jsp").exists()) ? browsableNode.file("index.jsp") : browsableNode.file("index.html");
	}
	
	public Map<String,Map<String,String>> getTagsAndAttributesFromIndexPage(BrowsableNode browsableNode, Locale locale, UrlContentAccessor contentAccessor, RequestMode requestMode) throws ContentProcessingException, ResourceNotFoundException {
		MemoizedFile indexPage = getIndexPage(browsableNode);
		try {
			if ( !Arrays.asList(app.appConf().getLocales()).contains(locale) ) {
				throw new ResourceNotFoundException("The locale '"+locale+"' is not a valid locale for this app.");
			}
			
			if (!indexPage.isFile()) {
				return new HashMap<>();
			}
			
			String pathRelativeToApp = app.dir().getRelativePath(indexPage);
			ByteArrayOutputStream indexPageContent = new ByteArrayOutputStream();
			contentAccessor.writeLocalUrlContentsToOutputStream(pathRelativeToApp, indexPageContent);
			
			return TagPluginUtility.getUsedTagsAndAttributes(indexPageContent.toString(), browsableNode.getBundleSet(), requestMode, locale);
		}
		catch (IOException | ConfigException | ModelOperationException | NoTagHandlerFoundException e) {
			throw new ContentProcessingException(e, "Error when trying to calculate used tags in the index page for " + browsableNode.root().dir().getRelativePath(indexPage));
		}
		
	}
	
	public ResponseContent getIndexPageContent(BrowsableNode browsableNode, Locale locale, String version, UrlContentAccessor contentAccessor, RequestMode requestMode) throws ContentProcessingException, ResourceNotFoundException {
		MemoizedFile indexPage = getIndexPage(browsableNode);
		try {
			if ( !Arrays.asList(app.appConf().getLocales()).contains(locale) ) {
				throw new ResourceNotFoundException("The locale '"+locale+"' is not a valid locale for this app.");
			}
			
			String pathRelativeToApp = app.dir().getRelativePath(indexPage);
			ByteArrayOutputStream indexPageContent = new ByteArrayOutputStream();
			contentAccessor.writeLocalUrlContentsToOutputStream(pathRelativeToApp, indexPageContent);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			try (Writer writer =  new OutputStreamWriter(byteArrayOutputStream, BladerunnerConf.OUTPUT_ENCODING)) {
				browsableNode.filterIndexPage(indexPageContent.toString(), locale, version, writer, requestMode);
			}

			return new CharResponseContent( browsableNode.root(), byteArrayOutputStream.toString() );
		}
		catch (IOException | ConfigException | ModelOperationException e) {
			throw new ContentProcessingException(e, "Error when trying to write the index page for " + browsableNode.root().dir().getRelativePath(indexPage));
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

	public ResponseContent getLocaleForwardingPageContent(BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		StringWriter localeForwardingPage = new StringWriter();
		
		SdkJsLib localeForwarderLib = app.root().sdkLib(BR_LOCALE_UTILITY_LIBNAME);
		try (Reader localeForwarderReader = new FileReader( localeForwarderLib.file(BR_LOCALE_UTILITY_FILENAME) ) ) {
			
			localeForwardingPage.write("<head>\n");
			localeForwardingPage.write("<noscript><meta http-equiv='refresh' content='0; url=" + app.appConf().getDefaultLocale() + "/'></noscript>\n");
			localeForwardingPage.write("<script type='text/javascript'>\n");
			
			ContentPlugin appVersionContentPlugin = app.root().plugins().contentPlugin("app-meta");
			ContentPathParser appVersionContentPathParser = appVersionContentPlugin.getContentPathParser();
			String appVersionContentPath = appVersionContentPathParser.createRequest("app-meta-request");
			ResponseContent responseContent = appVersionContentPlugin.handleRequest(appVersionContentPathParser.parse(appVersionContentPath), bundleSet, contentAccessor, appVersionContentPath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			responseContent.write(baos);
			localeForwardingPage.write( baos.toString() );
			
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
			
			return new CharResponseContent( app.root(), localeForwardingPage.toString() );
		}
		catch (IOException | ConfigException | MalformedTokenException | MalformedRequestException e) {
			throw new ContentProcessingException(e);
		}
	}

	private ContentPathParser getContentPathParser()
	{
		return contentPathParser.value(() -> {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				/* NOTE: 
				 * - <aspect> definition ends with a / - so <aspect>workbench == myAspect-workbench
				 * - ordering is important here, if two URLs share a similar format, the first type wins
				 */
				.accepts("<aspect>").as(LOCALE_FORWARDING_REQUEST)
					.and("<aspect><locale>/").as(INDEX_PAGE_REQUEST)
					.and("<aspect><bladeset>/<blade>/workbench/").as(WORKBENCH_LOCALE_FORWARDING_REQUEST)
					.and("<aspect><bladeset>/<blade>/workbench/<locale>/").as(WORKBENCH_INDEX_PAGE_REQUEST)
					.and("<aspect><bladeset>/<blade>/workbench/v/<version>/<content-path>").as(WORKBENCH_BUNDLE_REQUEST)
					.and("<aspect><bladeset>/workbench/").as(WORKBENCH_BLADESET_LOCALE_FORWARDING_REQUEST)
					.and("<aspect><bladeset>/workbench/<locale>/").as(WORKBENCH_BLADESET_INDEX_PAGE_REQUEST)
					.and("<aspect><bladeset>/workbench/v/<version>/<content-path>").as(WORKBENCH_BLADESET_BUNDLE_REQUEST)
					.and("<aspect>v/<version>/<content-path>").as(BUNDLE_REQUEST)
					.and("<aspect><content-path>").as(UNVERSIONED_BUNDLE_REQUEST)
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
