package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class ThirdpartyContentPlugin extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("thirdparty/bundle.js").as("bundle-request")
				.and("thirdparty/<module>/bundle.js").as("single-module-request")
				.and("thirdparty/<module>/<file-path>").as("file-request")
			.where("module").hasForm(ContentPathParserBuilder.PATH_TOKEN)
				.and("file-path").hasForm(ContentPathParserBuilder.PATH_TOKEN);
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;	
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "thirdparty";
	}
	
	@Override
	public String getCompositeGroupName() {
		return "text/javascript";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os, String version) throws ContentProcessingException
	{
		try {
			if (contentPath.formName.equals("bundle-request"))
			{
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding())) 
				{
					for(SourceModule sourceFile : bundleSet.getSourceModules()) {
						if(sourceFile instanceof ThirdpartySourceModule)
						{
							writer.write("// " + sourceFile.getPrimaryRequirePath() + "\n");
							try (Reader reader = sourceFile.getReader()) { IOUtils.copy(reader, writer); }
							writer.write("\n\n");
						}
					}
				}
			}
			else if(contentPath.formName.equals("file-request")) {
				String libName = contentPath.properties.get("module");
				App app = bundleSet.getBundlableNode().app();
				JsLib lib = app.jsLib(libName);
				if (!lib.dirExists())
				{
					throw new ContentProcessingException("Library '" + lib.getName() + "' doesn't exist.");
				}
				
				String filePath = contentPath.properties.get("file-path");
				
				filePath = StringUtils.substringBeforeLast(filePath, "?"); //strip off any query string that might be in the request
				
				File file = lib.file(filePath);
				if (!file.exists())
				{
					throw new ContentProcessingException("File '" + file.getAbsolutePath() + "' doesn't exist.");
				}
				
				IOUtils.copy(new FileInputStream(file), os);
			}
			else if(contentPath.formName.equals("single-module-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding())) 
				{
					SourceModule jsModule = (SourceModule)bundleSet.getBundlableNode().getLinkedAsset(contentPath.properties.get("module"));
					writer.write("// " + jsModule.getPrimaryRequirePath() + "\n");
					try (Reader reader = jsModule.getReader()) { IOUtils.copy(reader, writer); }
					writer.write("\n\n");
				}
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch(RequirePathException | ConfigException | IOException ex) {
			throw new ContentProcessingException(ex);
		}
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		
		try {
			for(SourceModule sourceModule : bundleSet.getSourceModules()) {
				if(sourceModule instanceof ThirdpartySourceModule) {
					requestPaths.add(contentPathParser.createRequest("single-module-request", sourceModule.getPrimaryRequirePath()));
				}
			}
		}
		catch(MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException 
	{
		List<String> requestPaths = new ArrayList<>();
		
		try {
			requestPaths.add(contentPathParser.createRequest("bundle-request"));
		}
		catch (MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}
}
