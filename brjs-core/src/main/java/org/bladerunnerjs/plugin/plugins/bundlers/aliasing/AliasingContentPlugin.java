package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
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
	public String getGroupName() {
		return "text/javascript";
	}
	
	@Override
	public String getRequestPrefix() {
		return "aliasing";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException {
		return getValidRequestPaths();
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException {
		return getValidRequestPaths();
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		try {
			if (contentPath.formName.equals("aliasing-request")) {
				boolean aliasRegistryLoaded = bundleSet.getSourceModules().contains(bundleSet.getBundlableNode().getSourceModule("br/AliasRegistry"));
				
				if(aliasRegistryLoaded) {
					try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
						String aliasData = AliasingSerializer.createJson(bundleSet);
						writer.write("require('br/AliasRegistry').setAliasData(" + aliasData + ");\n");
					}
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (RequirePathException e) {
			// do nothing: if 'br/AliasRegistry' doesn't exist then we definitely need to configure it
		}
		catch(IOException | ConfigException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	private List<String> getValidRequestPaths() throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		try {
			requestPaths.add(contentPathParser.createRequest("aliasing-request"));
		} catch (MalformedTokenException e) {
			throw new BundlerProcessingException(e);
		}
		
		return requestPaths;
	}
	
	@Override
	public int priority()
	{
		return 100;
	}
}
