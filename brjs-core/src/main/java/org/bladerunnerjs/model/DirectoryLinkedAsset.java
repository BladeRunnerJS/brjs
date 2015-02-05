package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;


public class DirectoryLinkedAsset implements LinkedAsset
{

	private AssetContainer assetContainer;
	private MemoizedFile dir;
	private String primaryRequirePath;
	private List<String> requirePaths = new ArrayList<>();
	private List<Asset> dependentAssets;

	public DirectoryLinkedAsset(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, LinkedAsset parentAsset) {
		this.assetContainer = assetContainer;
		this.dir = dir;		
		
		primaryRequirePath = requirePrefix;
		requirePaths.add(primaryRequirePath);
		
		dependentAssets = parentAsset == null ? Collections.emptyList() : Arrays.asList(parentAsset);
	}
	
	@Override
	public Reader getReader() throws IOException
	{
		return new StringReader("");
	}

	@Override
	public AssetLocation assetLocation()
	{
		return null;
	}

	@Override
	public MemoizedFile dir()
	{
		return dir;
	}

	@Override
	public String getAssetName()
	{
		return dir.getName();
	}

	@Override
	public String getAssetPath()
	{
		return assetContainer.app().dir().getRelativePath(dir);
	}

	@Override
	public List<String> getRequirePaths()
	{
		return requirePaths;
	}

	@Override
	public String getPrimaryRequirePath()
	{
		return primaryRequirePath;
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException
	{
		return dependentAssets;
	}

	@Override
	public List<String> getAliasNames() throws ModelOperationException
	{
		return Collections.emptyList();
	}

}
