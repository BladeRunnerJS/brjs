package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.utility.AdhocTimer;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

import com.Ostermiller.util.ConcatReader;


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
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentPluginOutput output, String version) throws ContentProcessingException
	{
		try {
			if (contentPath.formName.equals("bundle-request"))
			{
				List<Reader> readerList = new ArrayList<Reader>();
				for(SourceModule sourceFile : bundleSet.getSourceModules()) 
				{
					if(sourceFile instanceof ThirdpartySourceModule)
					{
						readerList.add(new StringReader("// " + sourceFile.getPrimaryRequirePath() + "\n"));
						readerList.add(sourceFile.getReader());
						readerList.add(new StringReader("\n\n"));
					}
				}
				Reader[] readers = new Reader[readerList.size()];
				readerList.toArray(readers);
				output.setReader(new ConcatReader(readers));
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
				
				output.setReader(new FileReader(file));
			}
			else if(contentPath.formName.equals("single-module-request")) {
				SourceModule jsModule = (SourceModule)bundleSet.getBundlableNode().getLinkedAsset(contentPath.properties.get("module"));
				output.setReader(new ConcatReader(new Reader[]{
					new StringReader("// " + jsModule.getPrimaryRequirePath() + "\n"),
					jsModule.getReader(),
					new StringReader("\n\n")
				}));
					
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch(RequirePathException  | IOException ex) {
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
