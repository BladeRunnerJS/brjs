package org.bladerunnerjs.model.app.build;

import static org.bladerunnerjs.utility.AppRequestHandler.BUNDLE_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.INDEX_PAGE_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.LOCALE_FORWARDING_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.UNVERSIONED_BUNDLE_REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.StaticContentPluginOutput;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.utility.AppRequestHandler;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.ServedAppMetadataUtility;
import org.bladerunnerjs.utility.WebXmlCompiler;


public abstract class AbstractAppBuilder
{

	abstract void preBuild(App app, File target) throws ModelOperationException;
	abstract void postBuild(File exportDir, App app, File target) throws ModelOperationException;
	
	public void build(App app, File target) throws ModelOperationException {
		File targetContainer = target.getParentFile();
		
		AppRequestHandler appRequestHandler = new AppRequestHandler(app);
		
		File temporaryExportDir = getTemporaryExportDir(app);
		
		if(!targetContainer.isDirectory()) throw new ModelOperationException("'" + targetContainer.getPath() + "' is not a directory.");
		this.preBuild(app, target);
		
		try {
			Locale[] locales = app.appConf().getLocales();
			String version = app.root().getAppVersionGenerator().getProdVersion();
			
			File appWebInf = app.file("WEB-INF");
			if(appWebInf.exists()) {
				File exportedWebInf = new File(temporaryExportDir, "WEB-INF");
				FileUtils.copyDirectory(appWebInf, exportedWebInf);
				File exportedWebXml = new File(exportedWebInf, "web.xml");
				if (exportedWebXml.isFile()) {
					WebXmlCompiler.compile(exportedWebXml);					
					String webXmlContents = FileUtils.readFileToString(exportedWebXml);
					webXmlContents = webXmlContents.replace(ServedAppMetadataUtility.APP_VERSION_TOKEN, version);
					FileUtils.writeStringToFile(exportedWebXml, webXmlContents, false);
				}
			}
			
			for(Aspect aspect : app.aspects()) {
				BundleSet bundleSet = aspect.getBundleSet();
				String aspectPrefix = (aspect.getName().equals("default")) ? "" : aspect.getName() + "/";
				File localeForwardingFile = new File(temporaryExportDir, appRequestHandler.createRequest(LOCALE_FORWARDING_REQUEST, aspectPrefix) + "index.html");
				
				localeForwardingFile.getParentFile().mkdirs();
				
				OutputStream stream = new  FileOutputStream(localeForwardingFile);
				ContentPluginOutput output = new StaticContentPluginOutput(app, stream);
				try(OutputStream os = new FileOutputStream(localeForwardingFile)) {
					appRequestHandler.writeLocaleForwardingPage(output, version);
				}
				
				for(Locale locale : locales) {
					String indexPageName = (aspect.file("index.jsp").exists()) ? "index.jsp" : "index.html";
					File localeIndexPageFile = new File(temporaryExportDir, appRequestHandler.createRequest(INDEX_PAGE_REQUEST, aspectPrefix, locale.toString()) + indexPageName);
					
					localeIndexPageFile.getParentFile().mkdirs();
					try(OutputStream os = new FileOutputStream(localeIndexPageFile)) {
						appRequestHandler.writeIndexPage(aspect, locale, version, new StaticContentPluginOutput(app, os), RequestMode.Prod);
					}
				}
				
				for(ContentPlugin contentPlugin : app.root().plugins().contentPlugins()) {
					if(contentPlugin.getCompositeGroupName() == null) {
						for(String contentPath : contentPlugin.getValidProdContentPaths(bundleSet, locales)) {
							File bundleFile;
							if (contentPath.startsWith("/")) {
								bundleFile = new File(temporaryExportDir, appRequestHandler.createRequest(UNVERSIONED_BUNDLE_REQUEST, aspectPrefix, contentPath));								
							} else {
								bundleFile = new File(temporaryExportDir, appRequestHandler.createRequest(BUNDLE_REQUEST, aspectPrefix, version, contentPath));																
							}
							
							bundleFile.getParentFile().mkdirs();
							ContentPluginOutput os = new StaticContentPluginOutput(app, bundleFile);
							contentPlugin.writeContent(contentPlugin.getContentPathParser().parse(contentPath), bundleSet, os, version);
							Reader reader = os.getReader();
							if(reader != null){
								Writer writer = new FileWriter(bundleFile);
								IOUtils.copy(reader, writer);
								writer.flush();
							}
						
						}
					} else {
						ContentPlugin plugin = (contentPlugin instanceof VirtualProxyContentPlugin) ? (ContentPlugin) ((VirtualProxyContentPlugin) contentPlugin).getUnderlyingPlugin() : contentPlugin;
						app.root().logger(this.getClass()).info("The content plugin '%s' is part of a composite content plugin so no files will be generated. " + 
								"If content bundles should be generated for this content plugin, you should set it's composite group name to null.", plugin.getClass().getSimpleName());
					}
				}
			}
		}
		catch(ConfigException | ContentProcessingException | MalformedRequestException | MalformedTokenException | IOException | ParseException | ResourceNotFoundException e) {
			throw new ModelOperationException(e);
		}
		
		this.postBuild(temporaryExportDir, app, target);
		FileUtils.deleteQuietly(temporaryExportDir);
	}
	
	
	private static File getTemporaryExportDir(App app) throws ModelOperationException
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
