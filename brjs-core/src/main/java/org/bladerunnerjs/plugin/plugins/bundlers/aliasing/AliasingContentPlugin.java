package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginUtility;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class AliasingContentPlugin extends AbstractContentPlugin {
	private final ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("aliasing/bundle.js").as("aliasing-request");
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
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
		return Arrays.asList(NamespacedJsContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		return getValidRequestPaths();
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		return getValidRequestPaths();
	}
	
	@Override
	public Reader writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentPluginUtility os, String version) throws ContentProcessingException {
		try {
			if (contentPath.formName.equals("aliasing-request")) {
				boolean aliasRegistryLoaded = bundleSet.getSourceModules().contains(bundleSet.getBundlableNode().getLinkedAsset("br/AliasRegistry"));
				
				if(aliasRegistryLoaded) {
					String aliasData = AliasingSerializer.createJson(bundleSet);
					return new StringReader("require('br/AliasRegistry').setAliasData(" + aliasData + ");\n");
				}
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (RequirePathException e) {
			// do nothing: if 'br/AliasRegistry' doesn't exist then we definitely need to configure it
		}
		return new StringReader("");
	}
	
	private List<String> getValidRequestPaths() throws ContentProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			requestPaths.add(contentPathParser.createRequest("aliasing-request"));
		} catch (MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}
}
