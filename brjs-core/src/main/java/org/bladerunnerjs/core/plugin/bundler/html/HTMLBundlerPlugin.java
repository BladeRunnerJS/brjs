package org.bladerunnerjs.core.plugin.bundler.html;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.ContentPathParserBuilder;


public class HTMLBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin
{

	private ContentPathParser requestParser;
	
	{
		ContentPathParserBuilder requestParserBuilder = new ContentPathParserBuilder();
		requestParserBuilder.accepts("bundle.html").as("bundle-request");
		requestParser = requestParserBuilder.build();
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getRequestPrefix() {
		return "html";
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
	public List<SourceModule> getSourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, FullyQualifiedLinkedAsset.class, "html");
	}

	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
}
