package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.SuffixAssetFilter;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NodeJsAssetPlugin extends AbstractAssetPlugin {
	
	private List<SourceModule> emptySourceModules = new ArrayList<SourceModule>();
	private List<LinkedAsset> emptyLinkedAssets = new ArrayList<LinkedAsset>();
	private List<Asset> emptyAssets = new ArrayList<Asset>();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		try {
			if (assetLocation.jsStyle().equals(NodeJsContentPlugin.JS_STYLE))
			{
				return assetLocation.obtainMatchingAssets(new SuffixAssetFilter("js"), SourceModule.class, NodeJsSourceModule.class);
			}
			return emptySourceModules;
		} catch (AssetFileInstantationException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public List<SourceModule> getTestSourceModules(AssetLocation assetLocation) {
		return getSourceModules(assetLocation);
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
