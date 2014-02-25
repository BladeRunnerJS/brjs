package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.util.ArrayList;
import java.util.Arrays;
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

public class CssResourceAssetPlugin extends AbstractAssetPlugin {
	private final List<SourceModule> emptySourceModules = new ArrayList<>();
	private final List<TestSourceModule> emptyTestSourceModules = new ArrayList<>();
	private final List<LinkedAsset> emptyLinkedAssets = new ArrayList<>();
	private final List<String> resourceExtensions = Arrays.asList(new String[] {"jpg","jpeg","bmp","png","gif","svg","ico","cur","eot","ttf","woff"});
	
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
		List<Asset> assets = new ArrayList<>();
		
		try {
			for(String resourceExtension : resourceExtensions) {
				assets.addAll(assetLocation.obtainMatchingAssets(new SuffixAssetFilter(resourceExtension), Asset.class, FileAsset.class));
			}
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
}
