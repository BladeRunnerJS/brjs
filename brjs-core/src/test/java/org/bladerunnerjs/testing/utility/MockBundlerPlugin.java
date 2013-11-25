package org.bladerunnerjs.testing.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class MockBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin
{

	@Override
	public String getTagName()
	{
		return "";
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getMimeType()
	{
		return "";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		return requestParserBuilder.build();
	}

	@Override
	public void writeContent(ParsedContentPath path, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
	}

	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		return Arrays.asList();
	}

	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		return Arrays.asList();
	}

	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<AssetFile> getResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

}
