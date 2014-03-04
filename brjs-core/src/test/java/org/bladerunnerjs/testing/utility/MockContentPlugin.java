package org.bladerunnerjs.testing.utility;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class MockContentPlugin extends AbstractContentPlugin
{
	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getRequestPrefix() {
		return "mock";
	}

	@Override
	public String getGroupName() {
		return "";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		return contentPathParserBuilder.build();
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException
	{
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return Arrays.asList();
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException
	{
		return Arrays.asList();
	}
}
