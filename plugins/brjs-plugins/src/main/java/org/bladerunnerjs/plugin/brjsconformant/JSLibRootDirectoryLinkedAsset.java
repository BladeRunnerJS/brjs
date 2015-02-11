package org.bladerunnerjs.plugin.brjsconformant;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.RootDirectoryLinkedAsset;


public class JSLibRootDirectoryLinkedAsset implements RootDirectoryLinkedAsset
{

	private AssetContainer assetContainer;
	private MemoizedFile dir;
	private MemoizedValue<List<Asset>> dependentAssets;
	private BRLibConf libManifest;

	public JSLibRootDirectoryLinkedAsset(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
		this.dir = assetContainer.dir();
		
		dependentAssets = new MemoizedValue<>(getAssetPath()+ " dependent assets", assetContainer.root(), assetContainer.dir());
		
		try {
			libManifest = new BRLibConf((JsLib) assetContainer);
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Reader getReader() throws IOException
	{
		return new StringReader("");
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
		return Arrays.asList( getPrimaryRequirePath() );
	}

	@Override
	public String getPrimaryRequirePath()
	{
		try {
			return (libManifest.manifestExists()) ? libManifest.getRequirePrefix() : ((JsLib) assetContainer).getName();
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
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
	public List<String> getAliasNames() throws ModelOperationException
	{
		return Collections.emptyList();
	}
	
	@Override
	public void setRequirePrefix(String requirePrefix) throws ConfigException {
		libManifest.setRequirePrefix(requirePrefix);
		libManifest.write();
	}

}
