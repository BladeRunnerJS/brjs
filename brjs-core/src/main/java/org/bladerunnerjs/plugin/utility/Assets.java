package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.AssetPlugin;

public class Assets {
	public Map<AssetPlugin, List<Asset>> pluginAssets = new HashMap<>();
	public List<LinkedAsset> linkedAssets = new ArrayList<>();
	public List<SourceModule> sourceModules = new ArrayList<>();
	
	public Assets(BRJS brjs) {
		for(AssetPlugin assetPlugin : brjs.plugins().assetProducers()) {
			pluginAssets.put(assetPlugin, new ArrayList<>());
		}
	}
}