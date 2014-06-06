package org.bladerunnerjs.model;

import static org.bladerunnerjs.utility.AppRequestHandler.BUNDLE_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.INDEX_PAGE_REQUEST;
import static org.bladerunnerjs.utility.AppRequestHandler.LOCALE_FORWARDING_REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.utility.AppRequestHandler;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.PageAccessor;
import org.bladerunnerjs.utility.SimplePageAccessor;
import org.bladerunnerjs.utility.WebXmlCompiler;

public class AppBuilder {
	public static void build(App app, File targetDir, boolean warExport) throws ModelOperationException {
		AppRequestHandler appRequestHandler = new AppRequestHandler(app);
		File appExportDir = new File(targetDir, app.getName());
		
		if(!targetDir.isDirectory()) throw new ModelOperationException("'" + targetDir.getPath() + "' is not a directory.");
		if(appExportDir.exists()) throw new ModelOperationException("'" + appExportDir.getPath() + "' already exists.");
		
		appExportDir.mkdir();
		
		try {
			String[] locales = app.appConf().getLocales();
			String version = app.root().getAppVersionGenerator().getProdVersion();
			PageAccessor pageAcessor = new SimplePageAccessor();
			
			File appWebInf = app.file("WEB-INF");
			if(appWebInf.exists()) {
				File exportedWebInf = new File(appExportDir, "WEB-INF");
				FileUtils.copyDirectory(appWebInf, exportedWebInf);
				File exportedWebXml = new File(exportedWebInf, "web.xml");
				if (exportedWebXml.isFile()) {
					WebXmlCompiler.compile(exportedWebXml);					
				}
			}
			
			for(Aspect aspect : app.aspects()) {
				BundleSet bundleSet = aspect.getBundleSet();
				String aspectPrefix = (aspect.getName().equals("default")) ? "" : aspect.getName() + "/";
				File localeForwardingFile = new File(appExportDir, appRequestHandler.createRequest(LOCALE_FORWARDING_REQUEST, aspectPrefix) + "index.html");
				
				localeForwardingFile.getParentFile().mkdirs();
				try(OutputStream os = new FileOutputStream(localeForwardingFile)) {
					appRequestHandler.writeLocaleForwardingPage(os);
				}
				
				for(String locale : locales) {
					String indexPageName = (aspect.file("index.jsp").exists()) ? "index.jsp" : "index.html";
					File localeIndexPageFile = new File(appExportDir, appRequestHandler.createRequest(INDEX_PAGE_REQUEST, aspectPrefix, locale) + indexPageName);
					
					localeIndexPageFile.getParentFile().mkdirs();
					try(OutputStream os = new FileOutputStream(localeIndexPageFile)) {
						appRequestHandler.writeIndexPage(aspect, locale, version, pageAcessor, os, RequestMode.Prod);
					}
				}
				
				for(ContentPlugin contentPlugin : app.root().plugins().contentProviders()) {
					if(contentPlugin.getCompositeGroupName() == null) {
						for(String contentPath : contentPlugin.getValidProdContentPaths(bundleSet, locales)) {
							File bundleFile = new File(appExportDir, appRequestHandler.createRequest(BUNDLE_REQUEST, aspectPrefix, version, contentPath));
							
							bundleFile.getParentFile().mkdirs();
							try(OutputStream os = new FileOutputStream(bundleFile)) {
								contentPlugin.writeContent(contentPlugin.getContentPathParser().parse(contentPath), bundleSet, os);
							}
						}
					}
				}
			}
			
			if(warExport) {
				File warFile = new File(targetDir, app.getName() + ".war");
				FileUtility.zipFolder(appExportDir, warFile, true);
				FileUtils.deleteDirectory(appExportDir);
			}
		}
		catch(ConfigException | ContentProcessingException | MalformedRequestException | MalformedTokenException | IOException | ParseException e) {
			throw new ModelOperationException(e);
		}
	}
}
