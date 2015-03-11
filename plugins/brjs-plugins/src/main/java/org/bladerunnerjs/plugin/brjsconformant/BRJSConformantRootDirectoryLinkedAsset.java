package org.bladerunnerjs.plugin.brjsconformant;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.model.DirectoryLinkedAsset;


public class BRJSConformantRootDirectoryLinkedAsset implements DirectoryLinkedAsset
{

	private AssetContainer assetContainer;
	private MemoizedFile dir;
	private String primaryRequirePath;
	List<Asset> implicitDependencies = new ArrayList<>();

	public BRJSConformantRootDirectoryLinkedAsset(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
		this.dir = assetContainer.dir();		
		primaryRequirePath = calculateRequirePath(assetContainer);
	}
	
	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
		for (Asset asset : implicitDependencies) {
			if (asset instanceof SourceModule) {
				continue;
			}
			this.implicitDependencies.add(asset);
		}
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
		return new ArrayList<>(implicitDependencies);
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
	
	public static String calculateRequirePath(AssetContainer assetContainer) {
		return assetContainer.requirePrefix();
	}
}
