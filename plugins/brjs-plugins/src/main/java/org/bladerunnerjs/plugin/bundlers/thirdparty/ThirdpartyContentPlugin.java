package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class ThirdpartyContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin
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
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
	{
		try {
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			if (parsedContentPath.formName.equals("bundle-request"))
			{
				boolean hasUnencapsulatedSourceModule = hasUnencapsulatedSourceModule(bundleSet);
				List<Reader> readerList = new ArrayList<Reader>();
				for(ThirdpartySourceModule sourceFile : bundleSet.getSourceModules(ThirdpartySourceModule.class)) 
				{
					readerList.add(new StringReader("// " + sourceFile.getPrimaryRequirePath() + "\n"));
					readerList.add(sourceFile.getReader());
					readerList.add(new StringReader("\n\n"));
					readerList.add( new StringReader(getGlobalisedThirdpartyModuleContent(sourceFile, hasUnencapsulatedSourceModule)) );
				}
				return new CharResponseContent( brjs, readerList );
			}
			else if(parsedContentPath.formName.equals("single-module-request")) {
				boolean hasUnencapsulatedSourceModule = hasUnencapsulatedSourceModule(bundleSet);
				LinkedAsset jsModule = bundleSet.bundlableNode().getLinkedAsset(parsedContentPath.properties.get("module"));
				return new CharResponseContent(brjs, 
					new StringReader("// " + jsModule.getPrimaryRequirePath() + "\n"),
					jsModule.getReader(),
					new StringReader("\n\n"),
					new StringReader(getGlobalisedThirdpartyModuleContent(jsModule, hasUnencapsulatedSourceModule))
				);
			}
			else {
				throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
			}
		}
		catch(RequirePathException  | IOException ex) {
			throw new ContentProcessingException(ex);
		}
	}

	private String getGlobalisedThirdpartyModuleContent(LinkedAsset sourceFile, boolean hasUnencapsulatedSourceModule)
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
				for(ThirdpartySourceModule sourceModule : bundleSet.getSourceModules(ThirdpartySourceModule.class)) {
					requestPaths.add(contentPathParser.createRequest("single-module-request", sourceModule.getPrimaryRequirePath()));
				}				
			}
		}
		catch(MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}

	private boolean hasUnencapsulatedSourceModule(BundleSet bundleSet)
	{
		for(SourceModule sourceFile : bundleSet.getSourceModules()) 
		{
			if (sourceFile.isGlobalisedModule()) {
				return true;
			}
		}
		return false;
	}
}
