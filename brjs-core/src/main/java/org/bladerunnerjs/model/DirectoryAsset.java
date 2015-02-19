package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;


public class DirectoryAsset implements DirectoryLinkedAsset
{

	private AssetContainer assetContainer;
	private MemoizedFile dir;
	private String primaryRequirePath;
	private Set<Asset> implicitDependencies = new LinkedHashSet<>();

	public DirectoryAsset(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix) {
		this.assetContainer = assetContainer;
		this.dir = dir;		
		
		primaryRequirePath = getRequirePath(requirePrefix, dir);
	}
	
	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		this.implicitDependencies.addAll(implicitDependencies);
	}
	
	@Override
	public Reader getReader() throws IOException
	{
		return new StringReader("");
	}

	@Override
	public MemoizedFile file()
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
		return Arrays.asList(primaryRequirePath);
	}

	@Override
	public String getPrimaryRequirePath()
	{
		return primaryRequirePath;
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException
	{
		List<Asset> assets = new ArrayList<>();
		assets.addAll(implicitDependencies);
		return assets;
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}

	public static String getRequirePath(String requirePrefix, MemoizedFile dir) {
		return requirePrefix+"/"+dir.getName();
	}
	
}
