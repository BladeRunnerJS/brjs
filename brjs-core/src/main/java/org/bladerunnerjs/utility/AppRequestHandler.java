package org.bladerunnerjs.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.BrowsableNode;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;

import com.google.common.base.Joiner;


public class AppRequestHandler
{
	private static final String LOCALE_FORWARDING_REQUEST = "locale-forwarding-request";
	private static final String INDEX_PAGE_REQUEST = "index-page-request";
	private static final String UNVERSIONED_BUNDLE_REQUEST = "unversioned-bundle-request";
	private static final String BUNDLE_REQUEST = "bundle-request";
	private static final String WORKBENCH_LOCALE_FORWARDING_REQUEST = "workbench-locale-forwarding-request";
	private static final String WORKBENCH_INDEX_PAGE_REQUEST = "workbench-index-page-request";
	private static final String WORKBENCH_BUNDLE_REQUEST = "workbench-bundle-request";
	private static final String WORKBENCH_BLADESET_LOCALE_FORWARDING_REQUEST = "workbench-bladeset-locale-forwarding-request";
	private static final String WORKBENCH_BLADESET_INDEX_PAGE_REQUEST = "workbench-bladeset-index-page-request";
	private static final String WORKBENCH_BLADESET_BUNDLE_REQUEST = "workbench-bladeset-bundle-request";

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

		String devVersion = app.root().getAppVersionGenerator().getVersion();
		ResponseContent response = null;

		switch (parsedContentPath.formName)
		{
			case LOCALE_FORWARDING_REQUEST:
			case WORKBENCH_LOCALE_FORWARDING_REQUEST:
				response = getLocaleForwardingPageContent(app.aspect(aspectName), contentAccessor, devVersion);
				break;
				
			case WORKBENCH_BLADESET_LOCALE_FORWARDING_REQUEST:
				response = getLocaleForwardingPageContent(app.aspect(aspectName), contentAccessor, devVersion);
				break;

			case INDEX_PAGE_REQUEST:
				response = getIndexPageContent(app.aspect(aspectName), appLocale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);
				break;

			case WORKBENCH_INDEX_PAGE_REQUEST:
				response = getIndexPageContent(app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench(), appLocale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);
				break;
			
			case WORKBENCH_BLADESET_INDEX_PAGE_REQUEST:
				response = getIndexPageContent(app.bladeset(pathProperties.get("bladeset")).workbench(), appLocale(pathProperties.get("locale")), devVersion, contentAccessor, RequestMode.Dev);
				break;
			
			case UNVERSIONED_BUNDLE_REQUEST:
				response = app.aspect(aspectName).handleLogicalRequest("/"+pathProperties.get("content-path"), contentAccessor, devVersion);
				break;
				
			case BUNDLE_REQUEST:
				response = app.aspect(aspectName).handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);
				break;

			case WORKBENCH_BUNDLE_REQUEST:
				response = app.bladeset(pathProperties.get("bladeset")).blade(pathProperties.get("blade")).workbench().handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);
				break;
			
			case WORKBENCH_BLADESET_BUNDLE_REQUEST:
				response = app.bladeset(pathProperties.get("bladeset")).workbench().handleLogicalRequest(pathProperties.get("content-path"), contentAccessor, devVersion);
				break;
				
		}

		if (response == null) {
			throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
		}
		return new TokenReplacingResponseContentWrapper( response, new PropertyFileTokenFinder(app.file("app-properties")), true );
	}

	public String createRelativeBundleRequest(String contentPath, String version) throws MalformedTokenException
	{
		if (contentPath.startsWith("/")) {
			return getContentPathParser().createRequest(UNVERSIONED_BUNDLE_REQUEST, "", contentPath);
		}
		else {
			return getContentPathParser().createRequest(BUNDLE_REQUEST, "", version, contentPath);
		}
	}

	public String createBundleRequest(Aspect aspect, String contentPath, String version) throws MalformedTokenException
	{
		if (contentPath.startsWith("/")) {
			return createRequest(aspect, AppRequestHandler.UNVERSIONED_BUNDLE_REQUEST, contentPath);
		}
		else {
			return createRequest(aspect, AppRequestHandler.BUNDLE_REQUEST, version, contentPath);
		}
	}
	
	public String createLocaleForwardingRequest(Aspect aspect) throws MalformedTokenException
	{
		return createRequest(aspect, LOCALE_FORWARDING_REQUEST);
	}
	
	public String createIndexPageRequest(Aspect aspect, Locale locale) throws MalformedTokenException
	{
		if(!aspect.app().isMultiLocaleApp()) {
			return createRequest(aspect, INDEX_PAGE_REQUEST) + "index";
		}
		else {
			return createRequest(aspect, INDEX_PAGE_REQUEST, locale.toString());
		}
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
				return new LinkedHashMap<>();
			}
			
			String pathRelativeToApp = app.dir().getRelativePath(indexPage);
			ByteArrayOutputStream indexPageContent = new ByteArrayOutputStream();
			contentAccessor.handleRequest(pathRelativeToApp, indexPageContent);
			
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
			contentAccessor.handleRequest(pathRelativeToApp, indexPageContent);
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			
			try (Writer writer =  new OutputStreamWriter(byteArrayOutputStream, BladerunnerConf.OUTPUT_ENCODING)) {
				browsableNode.filterIndexPage(indexPageContent.toString(), locale, version, writer, requestMode);
			}

			return new CharResponseContent( browsableNode.root(), byteArrayOutputStream.toString() );
		}
		catch (IOException | ConfigException | ModelOperationException e) {
			throw new ContentProcessingException(e, "Error when trying to write the index page for '" + browsableNode.root().dir().getRelativePath(indexPage) + "'");
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

	public ResponseContent getLocaleForwardingPageContent(Aspect aspect, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		try {
			String redirectionPageContent;
			try (InputStream redirectionPageInputStream = getClass().getClassLoader().getResourceAsStream( "org/bladerunnerjs/locale-redirection-page.html" )) {
				List<String> redirectionPageLines = IOUtils.readLines(redirectionPageInputStream);
				redirectionPageContent = StringUtils.join(redirectionPageLines, "\n");
			}
			
			redirectionPageContent = redirectionPageContent.replace("@DEFAULT.LOCALE@", app.appConf().getDefaultLocale().toString() );
			redirectionPageContent = redirectionPageContent.replace("@JS.BUNDLE@", getLocaleForwardingPageJSBundleContent(aspect, contentAccessor, version) );
			
			return new CharResponseContent( app.root(), redirectionPageContent );
		}
		catch (IOException | ConfigException | MalformedTokenException | MalformedRequestException | ModelOperationException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	private String getLocaleForwardingPageJSBundleContent(Aspect aspect, UrlContentAccessor contentAccessor, String version) throws MalformedTokenException, MalformedRequestException, ContentProcessingException, ModelOperationException, IOException {
		ContentPlugin compositeJsContentPlugin = app.root().plugins().contentPlugin("js");
		ContentPathParser compositeJsContentPathParser = compositeJsContentPlugin.castTo(RoutableContentPlugin.class).getContentPathParser();
		String jsBundleContentPath = compositeJsContentPathParser.createRequest("dev-bundle-request", "combined");
		BundlableNode localeForwarderAspectWrapper = new LocaleForwarderAspectWrapper(aspect);
		ResponseContent brLocaleBundleResponse = compositeJsContentPlugin.handleRequest(jsBundleContentPath, localeForwarderAspectWrapper.getBundleSet(), contentAccessor, version);
		
		ByteArrayOutputStream brLocaleBundleContent = new ByteArrayOutputStream();
		brLocaleBundleResponse.write( brLocaleBundleContent );
		
		return brLocaleBundleContent.toString();
	}
	
	private String createRequest(Aspect aspect, String requestFormName, String... args) throws MalformedTokenException
	{
		String aspectRequestPrefix = (aspect.getName().equals(app.defaultAspect().getName())) ? "" : aspect.getName()+"/";
		List<String> requestArgs = new LinkedList<>(Arrays.asList(args));
		requestArgs.add(0, aspectRequestPrefix);
		return getContentPathParser().createRequest(requestFormName, requestArgs.toArray(new String[0]));
	}
	
	private ContentPathParser getContentPathParser()
	{
		return contentPathParser.value(() -> {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			
			/* NOTE:
			 * - <aspect> definition ends with a / - so <aspect>workbench == myAspect-workbench
			 * - ordering is important here, if two URLs share a similar format, the first type wins
			 */
			if(!app.isMultiLocaleApp()) {
				contentPathParserBuilder
					.accepts("<aspect>").as(INDEX_PAGE_REQUEST)
						.and("<aspect><bladeset>/<blade>/workbench/").as(WORKBENCH_INDEX_PAGE_REQUEST)
						.and("<aspect><bladeset>/<blade>/workbench/v/<version>/<content-path>").as(WORKBENCH_BUNDLE_REQUEST)
						.and("<aspect><bladeset>/workbench/").as(WORKBENCH_BLADESET_INDEX_PAGE_REQUEST)
						.and("<aspect><bladeset>/workbench/v/<version>/<content-path>").as(WORKBENCH_BLADESET_BUNDLE_REQUEST)
						.and("<aspect>v/<version>/<content-path>").as(BUNDLE_REQUEST)
						.and("<aspect><content-path>").as(UNVERSIONED_BUNDLE_REQUEST)
					.where("aspect").hasForm("((" + getAspectNames() + ")/)?")
						.and("workbench").hasForm(ContentPathParserBuilder.NAME_TOKEN)
						.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
						.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
						.and("version").hasForm( app.root().getAppVersionGenerator().getVersionPattern() )
						.and("content-path").hasForm(ContentPathParserBuilder.PATH_TOKEN);
			}
			else {
				contentPathParserBuilder
					.accepts("<aspect>").as(LOCALE_FORWARDING_REQUEST)
						.and("<aspect><locale>").as(INDEX_PAGE_REQUEST)
						.and("<aspect><bladeset>/<blade>/workbench/").as(WORKBENCH_LOCALE_FORWARDING_REQUEST)
						.and("<aspect><bladeset>/<blade>/workbench/<locale>").as(WORKBENCH_INDEX_PAGE_REQUEST)
						.and("<aspect><bladeset>/<blade>/workbench/v/<version>/<content-path>").as(WORKBENCH_BUNDLE_REQUEST)
						.and("<aspect><bladeset>/workbench/").as(WORKBENCH_BLADESET_LOCALE_FORWARDING_REQUEST)
						.and("<aspect><bladeset>/workbench/<locale>").as(WORKBENCH_BLADESET_INDEX_PAGE_REQUEST)
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
			}
			
			return contentPathParserBuilder.build();
		});
	}

	private Locale appLocale(String locale) {
		try {
			return (locale != null) ? new Locale(locale) : app.appConf().getLocales()[0];
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
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
