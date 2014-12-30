package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.CompositeContentPlugin;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.thirdparty.ThirdpartyContentPlugin;
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		try {
			if (contentPath.formName.equals("aliasing-request")) {
				boolean aliasRegistryLoaded = bundleSet.getSourceModules().contains(bundleSet.getBundlableNode().getLinkedAsset("br/AliasRegistry"));
				
				if(aliasRegistryLoaded) {
					String aliasData = AliasingSerializer.createJson(bundleSet);
					return new CharResponseContent( brjs, "define('$alias-data', function(require, exports, module) {\n\tmodule.exports = " + aliasData + ";\n});\n" );
				}
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (RequirePathException e) {
			// do nothing: if 'br/AliasRegistry' doesn't exist then we definitely need to configure it
		}
		return new CharResponseContent(brjs, "");
	}
	
}
