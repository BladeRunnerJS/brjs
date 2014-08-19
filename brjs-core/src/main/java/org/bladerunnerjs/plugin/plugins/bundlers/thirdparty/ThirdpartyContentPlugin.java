package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
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
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsSourceModule;
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
				.and("thirdparty/globalise-modules.js").as("globalise-modules-request")
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
				List<Reader> readerList = new ArrayList<Reader>();
				for(SourceModule sourceFile : sourceModules) 
				{
					if(sourceFile instanceof ThirdpartySourceModule)
					{
						readerList.add(new StringReader("// " + sourceFile.getPrimaryRequirePath() + "\n"));
						readerList.add(sourceFile.getReader());
						readerList.add(new StringReader("\n\n"));
					}
				}
				return new CharResponseContent( brjs, readerList );
			}
			else if(contentPath.formName.equals("single-module-request")) {
				SourceModule jsModule = (SourceModule)bundleSet.getBundlableNode().getLinkedAsset(contentPath.properties.get("module"));
				return new CharResponseContent(brjs, 
					new StringReader("// " + jsModule.getPrimaryRequirePath() + "\n"),
					jsModule.getReader(),
					new StringReader("\n\n")
				);
			}
			else if(contentPath.formName.equals("globalise-modules-request")) {
				StringBuilder response = new StringBuilder();
				if (hasNamespacedJsSourceModule(sourceModules) ) {
					response.append("// thirdparty globalisation\n");
    				for(SourceModule sourceFile : sourceModules) 
    				{
    					if (sourceFile instanceof ThirdpartySourceModule && sourceFile.isEncapsulatedModule())
    					{
    						ThirdpartySourceModule thirdpartyModule = (ThirdpartySourceModule) sourceFile;
    						response.append(thirdpartyModule.getGlobalisedName() + " = require('"+thirdpartyModule.getPrimaryRequirePath()+"');\n");
    					}
    				}
    				response.append("\n\n");
				}
				return new CharResponseContent( brjs, response.toString() );
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
			if (hasNamespacedJsSourceModule(bundleSet.getSourceModules())) {
				requestPaths.add(contentPathParser.createRequest("globalise-modules-request"));
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
			if (hasNamespacedJsSourceModule(bundleSet.getSourceModules())) {
				requestPaths.add(contentPathParser.createRequest("globalise-modules-request"));
			}
		}
		catch (MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}

	private boolean hasNamespacedJsSourceModule(List<SourceModule> sourceModules)
	{
		for(SourceModule sourceFile : sourceModules) 
		{
			if (sourceFile instanceof NamespacedJsSourceModule) {
				return true;
			}
		}
		return false;
	}
}
