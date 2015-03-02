package org.bladerunnerjs.plugin.require;

import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.model.BundlableNode;

public class ServiceRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	// TODO: update to be a bundlableNodeSourceModules as has been done in AliasRequirePlugin
	private final Map<String, ServiceCommonJsSourceModule> sourceModules = new HashMap<>();
	private AssetLocation assetLocation;
	
	@Override
	public void setBRJS(BRJS brjs) {
		assetLocation = new NullAssetLocation(brjs);
	}

	@Override
	public String getPluginName() {
		return "service";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		return getSourceModule(requirePathSuffix);
	}
	
	private ServiceCommonJsSourceModule getSourceModule(String requirePath) {
		if(!sourceModules.containsKey(requirePath)) {
			sourceModules.put(requirePath, new ServiceCommonJsSourceModule(assetLocation, requirePath));
		}
		
		return sourceModules.get(requirePath);
	}
}
