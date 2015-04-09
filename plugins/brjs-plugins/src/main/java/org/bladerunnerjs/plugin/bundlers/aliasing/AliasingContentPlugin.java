package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
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
import org.bladerunnerjs.plugin.bundlers.thirdparty.ThirdpartyContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class AliasingContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin {
	private final ContentPathParser contentPathParser;
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("aliasing/bundle.js").as("aliasing-request");
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getCompositeGroupName() {
		return "text/javascript";
	}
	
	@Override
	public String getRequestPrefix() {
		return "aliasing";
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Arrays.asList(ThirdpartyContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			requestPaths.add(contentPathParser.createRequest("aliasing-request"));
		} catch (MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException {
		try {
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			
			if (parsedContentPath.formName.equals("aliasing-request")) {
				boolean aliasRegistryLoaded = bundleSet.getSourceModules().contains(bundleSet.getBundlableNode().getLinkedAsset("br/AliasRegistry"));
				
				if(aliasRegistryLoaded) {
					String aliasData = AliasingSerializer.createJson(bundleSet);
					return new CharResponseContent( brjs, "define('$alias-data', function(require, exports, module) {\n\tmodule.exports = " + aliasData + ";\n});\n" );
				}
			}
			else {
				throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
			}
		}
		catch (RequirePathException e) {
			// do nothing: if 'br/AliasRegistry' doesn't exist then we definitely need to configure it
		}
		
		return new CharResponseContent(brjs, "");
	}
}
