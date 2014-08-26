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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.utility.AppMetadataUtility;
import org.bladerunnerjs.utility.AppRequestHandler;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.WebXmlCompiler;


public class AppBuilderUtilis
{

	public static void build(App app, File target) throws ModelOperationException {
		File targetContainer = target.getParentFile();
		
		AppRequestHandler appRequestHandler = new AppRequestHandler(app);
		
		if(!targetContainer.isDirectory()) throw new ModelOperationException("'" + targetContainer.getPath() + "' is not a directory.");
		
		try {
			Locale[] locales = app.appConf().getLocales();
			String version = app.root().getAppVersionGenerator().getProdVersion();
			UrlContentAccessor contentPluginUtility = new StaticContentAccessor(app);
			
			File appWebInf = app.file("WEB-INF");
			if(appWebInf.exists()) {
				File exportedWebInf = new File(target, "WEB-INF");
				FileUtils.copyDirectory(appWebInf, exportedWebInf);
				File exportedWebXml = new File(exportedWebInf, "web.xml");
				if (exportedWebXml.isFile()) {
					WebXmlCompiler.compile(exportedWebXml);					
					String webXmlContents = FileUtils.readFileToString(exportedWebXml);
					webXmlContents = webXmlContents.replace(AppMetadataUtility.APP_VERSION_TOKEN, version);
					FileUtils.writeStringToFile(exportedWebXml, webXmlContents, false);
				}
			}
			
			Map<String,List<String>> contentPluginProdRequestsMap = new HashMap<>();
			
			for(Aspect aspect : app.aspects()) {
				BundleSet bundleSet = aspect.getBundleSet();
				String aspectPrefix = (aspect.getName().equals("default")) ? "" : aspect.getName() + "/";
				File localeForwardingFile = new File(target, appRequestHandler.createRequest(LOCALE_FORWARDING_REQUEST, aspectPrefix) + "index.html");
				
				localeForwardingFile.getParentFile().mkdirs();
				
				try (OutputStream os = new FileOutputStream(localeForwardingFile);
					ResponseContent content = appRequestHandler.getLocaleForwardingPageContent(app.root(), bundleSet, contentPluginUtility, version); )
				{
					content.write(os);
				}
				
				for(Locale locale : locales) {
					String indexPageName = (aspect.file("index.jsp").exists()) ? "index.jsp" : "index.html";
					File localeIndexPageFile = new File(target, appRequestHandler.createRequest(INDEX_PAGE_REQUEST, aspectPrefix, locale.toString()) + indexPageName);
					
					localeIndexPageFile.getParentFile().mkdirs();
					try (OutputStream os = new FileOutputStream(localeIndexPageFile);
						ResponseContent content = appRequestHandler.getIndexPageContent(aspect, locale, version, contentPluginUtility, RequestMode.Prod); )
					{
						content.write(os);
					}
					
					
					for (TagHandlerPlugin tagPlugin : app.root().plugins().tagHandlerPlugins()) {
						for (String contentPluginPrefix : tagPlugin.getDependentContentPluginRequestPrefixes()) {
							contentPluginProdRequestsMap.put(contentPluginPrefix, new ArrayList<String>());							
						}
					}
					
					
					Map<String,Map<String,String>> tagsAndAttributes = appRequestHandler.getTagsAndAttributesFromIndexPage(aspect, locale, version, contentPluginUtility, RequestMode.Prod);
					for(String tag : tagsAndAttributes.keySet()) {
						TagHandlerPlugin tagPlugin = app.root().plugins().tagHandlerPlugin(tag);
						Map<String,String> tagAttributes = tagsAndAttributes.get(tag);
						List<String> generatedRequests = tagPlugin.getGeneratedRequests(RequestMode.Prod, tagAttributes, bundleSet, locale, version);
						for (String contentPluginPrefix : tagPlugin.getDependentContentPluginRequestPrefixes()) {
							contentPluginProdRequestsMap.get(contentPluginPrefix).addAll(generatedRequests);
						}
					}
				}
				
				for(ContentPlugin contentPlugin : app.root().plugins().contentPlugins()) {
					if(contentPlugin.getCompositeGroupName() == null) {
						String requestPrefix = contentPlugin.getRequestPrefix();
						for(String contentPath : contentPlugin.getValidProdContentPaths(bundleSet, locales)) {
							if (!contentPluginProdRequestsMap.containsKey(requestPrefix) || contentPluginProdRequestsMap.get(requestPrefix).contains(contentPath)) {
    							writeContentFile(bundleSet, contentPluginUtility, target, appRequestHandler, version, aspectPrefix, contentPlugin, contentPath);
    						}
						}
					} else {
						ContentPlugin plugin = (contentPlugin instanceof VirtualProxyContentPlugin) ? (ContentPlugin) ((VirtualProxyContentPlugin) contentPlugin).getUnderlyingPlugin() : contentPlugin;
						app.root().logger(AppBuilderUtilis.class).info("The content plugin '%s' is part of a composite content plugin so no files will be generated. " + 
								"If content bundles should be generated for this content plugin, you should set it's composite group name to null.", plugin.getClass().getSimpleName());
					}
				}
			}
		}
		catch(ConfigException | ContentProcessingException | MalformedRequestException | MalformedTokenException | IOException | ParseException | ResourceNotFoundException e) {
			throw new ModelOperationException(e);
		}
	}


	private static void writeContentFile(BundleSet bundleSet, UrlContentAccessor contentPluginUtility, File target, AppRequestHandler appRequestHandler, String version, String aspectPrefix, ContentPlugin contentPlugin, String contentPath) throws MalformedTokenException, MalformedRequestException, IOException, FileNotFoundException, ContentProcessingException
	{
		File bundleFile;
		if (contentPath.startsWith("/")) {
			bundleFile = new File(target, appRequestHandler.createRequest(UNVERSIONED_BUNDLE_REQUEST, aspectPrefix, contentPath));								
		} else {
			bundleFile = new File(target, appRequestHandler.createRequest(BUNDLE_REQUEST, aspectPrefix, version, contentPath));																
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

	public static File getTemporaryExportDir(App app) throws ModelOperationException
	{
		try
		{
			return FileUtility.createTemporaryDirectory(app.getName()+"_build");
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
}
