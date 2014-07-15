package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.RelativePathUtility;


public class ThirdpartyResourceContentPlugin extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("thirdparty-resource/<module>/<file-path>").as("file-request")
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
		return "thirdparty-resource";
	}
	
	@Override
	public String getCompositeGroupName() {
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws ContentProcessingException
	{
		try {
			if(contentPath.formName.equals("file-request")) {
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
				
				return new CharResponseContent(brjs, new FileReader(file));
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (IOException ex) {
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
					requestPaths.addAll( getRequestPathsForThirdpartySourceModule((ThirdpartySourceModule) sourceModule) );
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
		return getValidDevContentPaths(bundleSet, locales);
	}
	
	private List<String> getRequestPathsForThirdpartySourceModule(ThirdpartySourceModule thirdpartySourceModule) throws MalformedTokenException {
		List<String> requestPaths = new ArrayList<>();
		for (File file : brjs.getFileInfo(thirdpartySourceModule.dir()).nestedFiles()) {
			String relativePath = RelativePathUtility.get(brjs, thirdpartySourceModule.dir(), file);
			requestPaths.add( contentPathParser.createRequest("file-request", thirdpartySourceModule.getPrimaryRequirePath(), relativePath) );
		}
		return requestPaths;
	}
}
