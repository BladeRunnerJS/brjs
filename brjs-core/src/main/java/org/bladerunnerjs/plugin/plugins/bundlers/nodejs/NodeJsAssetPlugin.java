package org.bladerunnerjs.plugin.plugins.bundlers.nodejs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.SuffixAssetFilter;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class NodeJsAssetPlugin extends AbstractAssetPlugin {
	private List<Asset> emptySourceModules = new ArrayList<>();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		try {
			if (assetLocation.jsStyle().equals(NodeJsContentPlugin.JS_STYLE))
			{
				return assetLocation.obtainMatchingAssets(new SuffixAssetFilter("js"), Asset.class, NodeJsSourceModule.class);
			}
			return emptySourceModules;
		}
		catch (AssetFileInstantationException ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
