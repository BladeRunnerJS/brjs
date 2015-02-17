package org.bladerunnerjs.plugin.brjsconformant;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;


public class BRJSConformantRootDirectoryLinkedAsset implements LinkedAsset
{

	private AssetContainer assetContainer;
	private MemoizedFile dir;
	private String primaryRequirePath;
	private MemoizedValue<List<Asset>> dependentAssets;

	public BRJSConformantRootDirectoryLinkedAsset(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
		this.dir = assetContainer.dir();		
		primaryRequirePath = assetContainer.requirePrefix();
		dependentAssets = new MemoizedValue<>(getAssetPath()+ " dependent assets", assetContainer.root(), assetContainer.dir());
	}
	
	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
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
		return dependentAssets.value(() -> {
			List<Asset> dependentAssets = new ArrayList<>();
			for (Asset assetContainerAsset : assetContainer.assets()) {
				String thisRequirePath = getPrimaryRequirePath();
				String assetContainerAssetRequirePath = assetContainerAsset.getPrimaryRequirePath();
				String[] thisRequirePathChunks = thisRequirePath.split("/");
				String[] assetContainerAssetRequirePathChunks = assetContainerAssetRequirePath.split("/");
				if ( thisRequirePath.startsWith(assetContainerAssetRequirePath) && (thisRequirePathChunks.length+1)==assetContainerAssetRequirePathChunks.length ) {
					dependentAssets.add(assetContainerAsset);
				}
			}
			
			return dependentAssets;
		});
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
}
