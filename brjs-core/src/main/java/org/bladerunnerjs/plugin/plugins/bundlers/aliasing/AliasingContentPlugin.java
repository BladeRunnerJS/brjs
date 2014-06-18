package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentOutputStream;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class AliasingContentPlugin extends AbstractContentPlugin {
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
		return Arrays.asList(NamespacedJsContentPlugin.class.getCanonicalName());
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		return getValidRequestPaths();
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		return getValidRequestPaths();
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentOutputStream os, String version) throws ContentProcessingException {
		try {
			if (contentPath.formName.equals("aliasing-request")) {
				boolean aliasRegistryLoaded = bundleSet.getSourceModules().contains(bundleSet.getBundlableNode().getSourceModule("br/AliasRegistry"));
				
				if(aliasRegistryLoaded) {
					try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding())) {
						String aliasData = AliasingSerializer.createJson(bundleSet);
						writer.write("require('br/AliasRegistry').setAliasData(" + aliasData + ");\n");
					}
				}
			}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (RequirePathException e) {
			// do nothing: if 'br/AliasRegistry' doesn't exist then we definitely need to configure it
		}
		catch(IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
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
