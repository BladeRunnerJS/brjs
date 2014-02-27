package org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SuffixAssetFilter;
import org.bladerunnerjs.model.TestSourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NamespacedJsAssetPlugin extends AbstractAssetPlugin {
	
	private List<SourceModule> emptySourceModules = new ArrayList<SourceModule>();
	private List<TestSourceModule> emptyTestSourceModules = new ArrayList<TestSourceModule>();
	private List<LinkedAsset> emptyLinkedAssets = new ArrayList<LinkedAsset>();
	private List<Asset> emptyAssets = new ArrayList<Asset>();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		try {
			if (assetLocation.getJsStyle().equals(NamespacedJsContentPlugin.JS_STYLE))
			{
				return assetLocation.obtainMatchingAssets(new SuffixAssetFilter("js"), SourceModule.class, NamespacedJsSourceModule.class);
			}
			return emptySourceModules;
		} catch (AssetFileInstantationException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<TestSourceModule> getTestSourceModules(AssetLocation assetLocation) {
		try {
			if (assetLocation.getJsStyle().equals(NamespacedJsContentPlugin.JS_STYLE))
			{
				return assetLocation.obtainMatchingAssets(new SuffixAssetFilter("js"), TestSourceModule.class, NamespacedJsTestSourceModule.class);
			}
			return emptyTestSourceModules;
		} catch (AssetFileInstantationException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
        return emptyLinkedAssets;
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		return emptyAssets;
	}
	
}
