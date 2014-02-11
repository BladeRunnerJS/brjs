package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class XMLContentPlugin extends AbstractContentPlugin
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
	public String getGroupName() {
		return "application/xml";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return new ArrayList<>();
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return new ArrayList<>();
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}
}
