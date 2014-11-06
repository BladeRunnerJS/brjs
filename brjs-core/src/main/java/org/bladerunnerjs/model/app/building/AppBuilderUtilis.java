package org.bladerunnerjs.model.app.building;

import static org.bladerunnerjs.utility.AppRequestHandler.BUNDLE_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.INDEX_PAGE_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.LOCALE_FORWARDING_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.UNVERSIONED_BUNDLE_REQUEST;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.utility.AppMetadataUtility;
import org.bladerunnerjs.utility.AppRequestHandler;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.WebXmlCompiler;


public class AppBuilderUtilis
{

	public static void build(App app, File targetDir) throws ModelOperationException {
		
		File targetContainer = targetDir.getParentFile();
		if(!targetContainer.isDirectory()) throw new ModelOperationException("'" + targetContainer.getPath() + "' is not a directory.");
		
		try {
			String version = app.root().getAppVersionGenerator().getProdVersion();
			BRJS brjs = app.root();
			AppRequestHandler appRequestHandler = new AppRequestHandler(app);
			UrlContentAccessor urlContentAccessor = new StaticContentAccessor(app);
			Locale[] locales = app.appConf().getLocales();
			
			filterAppWebXml(app, targetDir, version);
			
			for (Aspect aspect : app.aspects()) {
				BundleSet bundleSet = aspect.getBundleSet();
				String aspectRequestPrefix = (aspect.getName().equals("default")) ? "" : aspect.getName() + "/";				
								
				writeLocaleForwardingFileForAspect(bundleSet, targetDir, appRequestHandler, aspectRequestPrefix, urlContentAccessor, version);
				
				for (Locale locale : locales) {
					outputAspectIndexPage(aspect, locale, bundleSet, targetDir, appRequestHandler, aspectRequestPrefix, urlContentAccessor, version);
				}
				
				for (ContentPlugin contentPlugin : brjs.plugins().contentPlugins()) {
					outputContentPluginBundles(contentPlugin, bundleSet, locales, targetDir, version, appRequestHandler, aspectRequestPrefix, urlContentAccessor);
				}
			}
		}
		catch(Exception e) {
			throw new ModelOperationException(e);
		}
	}

	public static File getTemporaryExportDir(App app) throws ModelOperationException
	{
		try
		{
			return FileUtility.createTemporaryDirectory(AppBuilderUtilis.class, app.getName());
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
	
	private static void outputContentPluginBundles(ContentPlugin contentPlugin, BundleSet bundleSet, Locale[] locales, File target, String version, AppRequestHandler appRequestHandler, String aspectRequestPrefix, UrlContentAccessor urlContentAccessor) throws ContentProcessingException, MalformedTokenException, MalformedRequestException, IOException, FileNotFoundException
	{
		if (contentPlugin.getCompositeGroupName() == null) {
			for (String contentPath : contentPlugin.getProdContentPathsUsedFromBrowsableNode(bundleSet, locales)) {
				writeContentFile(bundleSet, urlContentAccessor, target, appRequestHandler, version, aspectRequestPrefix, contentPlugin, contentPath);
			}
		} else {
			ContentPlugin plugin = (contentPlugin instanceof VirtualProxyContentPlugin) ? (ContentPlugin) ((VirtualProxyContentPlugin) contentPlugin).getUnderlyingPlugin() : contentPlugin;
			bundleSet.getBundlableNode().root().logger(AppBuilderUtilis.class).info("The content plugin '%s' is part of a composite content plugin so no files will be generated. " + 
					"If content bundles should be generated for this content plugin, you should set it's composite group name to null.", plugin.getClass().getSimpleName());
		}
	}


	private static void outputAspectIndexPage(Aspect aspect, Locale locale, BundleSet bundleSet, File targetDir, AppRequestHandler appRequestHandler, String aspectRequestPrefix, UrlContentAccessor urlContentAccessor, String version) throws MalformedTokenException, IOException, FileNotFoundException, ContentProcessingException, ResourceNotFoundException
	{
		String indexPageName = (aspect.file("index.jsp").exists()) ? "index.jsp" : "index.html";
		File localeIndexPageFile = new File(targetDir, appRequestHandler.createRequest(INDEX_PAGE_REQUEST, aspectRequestPrefix, locale.toString()) + indexPageName);
		localeIndexPageFile.getParentFile().mkdirs();
		
		try (OutputStream os = new FileOutputStream(localeIndexPageFile);
			ResponseContent content = appRequestHandler.getIndexPageContent(aspect, locale, version, urlContentAccessor, RequestMode.Prod); )
		{
			content.write(os);
		}
	}

	private static void writeLocaleForwardingFileForAspect(BundleSet bundleSet, File target, AppRequestHandler appRequestHandler, String aspectRequestPrefix, UrlContentAccessor urlContentAccessor, String version) throws MalformedTokenException, IOException, FileNotFoundException, ContentProcessingException
	{
		File localeForwardingFile = new File(target, appRequestHandler.createRequest(LOCALE_FORWARDING_REQUEST, aspectRequestPrefix) + "index.html");
		localeForwardingFile.getParentFile().mkdirs();
		
		try (OutputStream os = new FileOutputStream(localeForwardingFile);
			ResponseContent content = appRequestHandler.getLocaleForwardingPageContent(bundleSet, urlContentAccessor, version); )
		{
			content.write(os);
		}
	}

	private static void filterAppWebXml(App app, File targetDir, String version) throws IOException, ParseException
	{
		File appWebInf = app.file("WEB-INF");
		if (appWebInf.exists()) {
			File exportedWebInf = new File(targetDir, "WEB-INF");
			FileUtils.copyDirectory(appWebInf, exportedWebInf);
			File exportedWebXml = new File(exportedWebInf, "web.xml");
			if (exportedWebXml.isFile()) {
				WebXmlCompiler.compile(exportedWebXml);					
				String webXmlContents = FileUtils.readFileToString(exportedWebXml);
				webXmlContents = webXmlContents.replace(AppMetadataUtility.APP_VERSION_TOKEN, version);
				FileUtils.writeStringToFile(exportedWebXml, webXmlContents, false);
			}
		}
	}

	private static void writeContentFile(BundleSet bundleSet, UrlContentAccessor contentPluginUtility, File target, AppRequestHandler appRequestHandler, String version, String aspectRequestPrefix, ContentPlugin contentPlugin, String contentPath) throws MalformedTokenException, MalformedRequestException, IOException, FileNotFoundException, ContentProcessingException
	{
		File bundleFile;
		if (contentPath.startsWith("/")) {
			bundleFile = new File(target, appRequestHandler.createRequest(UNVERSIONED_BUNDLE_REQUEST, aspectRequestPrefix, contentPath));								
		} else {
			bundleFile = new File(target, appRequestHandler.createRequest(BUNDLE_REQUEST, aspectRequestPrefix, version, contentPath));																
		}
		
		ParsedContentPath parsedContentPath = contentPlugin.getContentPathParser().parse(contentPath);
		bundleFile.getParentFile().mkdirs();
		bundleFile.createNewFile();
		try (FileOutputStream bundleFileOutputStream = new FileOutputStream(bundleFile);
			ResponseContent pluginContent = contentPlugin.handleRequest(parsedContentPath, bundleSet, contentPluginUtility, version); )
		{
			pluginContent.write( bundleFileOutputStream );
		}
	}
	
}
