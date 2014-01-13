package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.plugin.base.AbstractBundlerContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class XMLBundlerContentPlugin extends AbstractBundlerContentPlugin
{

	private ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("bundle.xml").as("bundle-request");
		contentPathParser = contentPathParserBuilder.build();
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getRequestPrefix() {
		return "xml";
	}
	
	@Override
	public String getMimeType()
	{
		return "application/xml";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}
}
