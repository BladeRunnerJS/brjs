package org.bladerunnerjs.model.app.building;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
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
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.WebXmlCompiler;


public class AppBuilderUtilis
{

	public static void build(App app, File targetDir) throws ModelOperationException {
		
		File targetContainer = targetDir.getParentFile();
		if(!targetContainer.isDirectory()) throw new ModelOperationException("'" + targetContainer.getPath() + "' is not a directory.");
		
		try {
			String version = app.root().getAppVersionGenerator().getProdVersion();
			BRJS brjs = app.root();
			UrlContentAccessor urlContentAccessor = new StaticContentAccessor(app);
			Locale[] locales = app.appConf().getLocales();
			
			filterAppWebXml(app, targetDir, version);
			
			for (Aspect aspect : app.aspects()) {
				BundleSet bundleSet = aspect.getBundleSet();				
								
				writeLocaleForwardingFileForAspect(bundleSet, targetDir, aspect, urlContentAccessor, version);
				
				for (Locale locale : locales) {
					outputAspectIndexPage(aspect, locale, bundleSet, targetDir, urlContentAccessor, version);
				}
				
				for (ContentPlugin contentPlugin : brjs.plugins().contentPlugins()) {
					outputContentPluginBundles(contentPlugin, bundleSet, locales, targetDir, version, aspect, urlContentAccessor);
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
			return FileUtils.createTemporaryDirectory(AppBuilderUtilis.class, app.getName());
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
	
	private static void outputContentPluginBundles(ContentPlugin contentPlugin, BundleSet bundleSet, Locale[] locales, File target, String version, Aspect aspect, UrlContentAccessor urlContentAccessor) throws ContentProcessingException, MalformedTokenException, MalformedRequestException, IOException, FileNotFoundException
	{
		if (contentPlugin.getCompositeGroupName() == null) {
			for (String contentPath : contentPlugin.getUsedContentPaths(bundleSet, RequestMode.Prod, locales)) {
				writeContentFile(bundleSet, urlContentAccessor, target, version, aspect, contentPlugin, contentPath);
			}
		} else {
			ContentPlugin plugin = (contentPlugin instanceof VirtualProxyContentPlugin) ? (ContentPlugin) ((VirtualProxyContentPlugin) contentPlugin).getUnderlyingPlugin() : contentPlugin;
			bundleSet.getBundlableNode().root().logger(AppBuilderUtilis.class).info("The content plugin '%s' is part of a composite content plugin so no files will be generated. " + 
					"If content bundles should be generated for this content plugin, you should set it's composite group name to null.", plugin.getClass().getSimpleName());
		}
	}


	private static void outputAspectIndexPage(Aspect aspect, Locale locale, BundleSet bundleSet, File targetDir, UrlContentAccessor urlContentAccessor, String version) throws MalformedTokenException, IOException, FileNotFoundException, ContentProcessingException, ResourceNotFoundException
	{
		String indexPageName = (aspect.file("index.jsp").exists()) ? "index.jsp" : "index.html";
		File localeIndexPageFile = new File(targetDir, aspect.requestHandler().createIndexPageRequest(locale) + indexPageName);
		localeIndexPageFile.getParentFile().mkdirs();
		
		try (OutputStream os = new FileOutputStream(localeIndexPageFile);
			ResponseContent content = aspect.requestHandler().getIndexPageContent(locale, version, urlContentAccessor, RequestMode.Prod); )
		{
			content.write(os);
		}
	}

	private static void writeLocaleForwardingFileForAspect(BundleSet bundleSet, File target, Aspect aspect, UrlContentAccessor urlContentAccessor, String version) throws MalformedTokenException, IOException, FileNotFoundException, ContentProcessingException
	{
		File localeForwardingFile = new File(target, aspect.requestHandler().createLocaleForwardingRequest()+"index.html");
		localeForwardingFile.getParentFile().mkdirs();
		
		try (OutputStream os = new FileOutputStream(localeForwardingFile);
			ResponseContent content = aspect.requestHandler().getLocaleForwardingPageContent(bundleSet, urlContentAccessor, version); )
		{
			content.write(os);
		}
	}

	private static void filterAppWebXml(App app, File targetDir, String version) throws IOException, ParseException
	{
		File appWebInf = app.file("WEB-INF");
		if (appWebInf.exists()) {
			File exportedWebInf = new File(targetDir, "WEB-INF");
			FileUtils.copyDirectory(app, appWebInf, exportedWebInf);
			File exportedWebXml = new File(exportedWebInf, "web.xml");
			if (exportedWebXml.isFile()) {
				WebXmlCompiler.compile(app.root(), exportedWebXml);					
				String webXmlContents = org.apache.commons.io.FileUtils.readFileToString(exportedWebXml);
				webXmlContents = webXmlContents.replace(AppMetadataUtility.APP_VERSION_TOKEN, version);
				FileUtils.write(app, exportedWebXml, webXmlContents, false);
			}
		}
	}

	private static void writeContentFile(BundleSet bundleSet, UrlContentAccessor contentPluginUtility, File target, String version, Aspect aspect, ContentPlugin contentPlugin, String contentPath) throws MalformedTokenException, MalformedRequestException, IOException, FileNotFoundException, ContentProcessingException
	{
		File bundleFile = new File(target, aspect.requestHandler().createBundleRequest(contentPath, version));
		
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
