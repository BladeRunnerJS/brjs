package org.bladerunnerjs.core.plugin.bundler.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.FullyQualifiedLinkedAssetFile;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class HTMLBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin
{

	private ContentPathParser requestParser;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder.accepts("bundle.html").as("bundle-request");
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public String getTagName()
	{
		return "html";
	}

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getMimeType()
	{
		return "text/html";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return requestParser;
	}

	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}

	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		throw new RuntimeException("Not implemented!");
	}
	
	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, FullyQualifiedLinkedAssetFile.class, "html");
	}

	@Override
	public List<AssetFile> getResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
}
