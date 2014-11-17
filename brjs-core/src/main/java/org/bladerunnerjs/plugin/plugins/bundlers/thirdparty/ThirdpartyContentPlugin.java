package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws ContentProcessingException
	{
		try {
			List<SourceModule> sourceModules = bundleSet.getSourceModules();
			if (contentPath.formName.equals("bundle-request"))
			{
				boolean hasUnencapsulatedSourceModule = hasUnencapsulatedSourceModule(sourceModules);
				List<Reader> readerList = new ArrayList<Reader>();
				for(SourceModule sourceFile : sourceModules) 
				{
					if(sourceFile instanceof ThirdpartySourceModule)
					{
						readerList.add(new StringReader("// " + sourceFile.getPrimaryRequirePath() + "\n"));
						readerList.add(sourceFile.getReader());
						readerList.add(new StringReader("\n\n"));
						readerList.add( new StringReader(getGlobalisedThirdpartyModuleContent(sourceFile, hasUnencapsulatedSourceModule)) );
					}
				}
				return new CharResponseContent( brjs, readerList );
			}
			else if(contentPath.formName.equals("single-module-request")) {
				boolean hasUnencapsulatedSourceModule = hasUnencapsulatedSourceModule(sourceModules);
				SourceModule jsModule = (SourceModule)bundleSet.getBundlableNode().getLinkedAsset(contentPath.properties.get("module"));
				return new CharResponseContent(brjs, 
					new StringReader("// " + jsModule.getPrimaryRequirePath() + "\n"),
					jsModule.getReader(),
					new StringReader("\n\n"),
					new StringReader(getGlobalisedThirdpartyModuleContent(jsModule, hasUnencapsulatedSourceModule))
				);
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch(RequirePathException  | IOException ex) {
			throw new ContentProcessingException(ex);
		}
	}

	private String getGlobalisedThirdpartyModuleContent(SourceModule sourceFile, boolean hasUnencapsulatedSourceModule)
	{
		if (sourceFile instanceof ThirdpartySourceModule && hasUnencapsulatedSourceModule) {
			ThirdpartySourceModule thirdpartyModule = (ThirdpartySourceModule) sourceFile;
			return "window." + thirdpartyModule.getGlobalisedName() + " = require('"+thirdpartyModule.getPrimaryRequirePath()+"');\n\n";
		}
		return "";
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		
		try {
			if (requestMode == RequestMode.Prod) {
				requestPaths.add(contentPathParser.createRequest("bundle-request"));
			} else {
				for(SourceModule sourceModule : bundleSet.getSourceModules()) {
					if(sourceModule instanceof ThirdpartySourceModule) {
						requestPaths.add(contentPathParser.createRequest("single-module-request", sourceModule.getPrimaryRequirePath()));
					}
				}				
			}
		}
		catch(MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}

	private boolean hasUnencapsulatedSourceModule(List<SourceModule> sourceModules)
	{
		for(SourceModule sourceFile : sourceModules) 
		{
			if (sourceFile.isGlobalisedModule()) {
				return true;
			}
		}
		return false;
	}
}
