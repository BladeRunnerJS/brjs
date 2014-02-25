package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FileAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SuffixAssetFilter;
import org.bladerunnerjs.model.TestSourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class CssAssetPlugin extends AbstractAssetPlugin {
	private final List<SourceModule> emptySourceModules = new ArrayList<>();
	private final List<TestSourceModule> emptyTestSourceModules = new ArrayList<>();
	private final List<LinkedAsset> emptyLinkedAssets = new ArrayList<>();
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		return emptySourceModules;
	}
	
	@Override
	public List<TestSourceModule> getTestSourceModules(AssetLocation assetLocation) {
		return emptyTestSourceModules;
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		return emptyLinkedAssets;
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		try {
			return assetLocation.obtainMatchingAssets(new SuffixAssetFilter("css"), Asset.class, FileAsset.class);
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
}
