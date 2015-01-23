package org.bladerunnerjs.plugin.plugins.require;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.plugin.RequirePlugin;
import org.bladerunnerjs.plugin.base.AbstractRequirePlugin;

public class DefaultRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}

	@Override
	public String getPluginName() {
		return "default";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		LinkedAsset asset = null;
		for(AssetContainer assetContainer : bundlableNode.scopeAssetContainers()) {
			LinkedAsset locationAsset = assetContainer.linkedAsset(requirePathSuffix);

			if(locationAsset != null) {
				if(asset == null) {
					asset = locationAsset;
				}
				else {
					throw new AmbiguousRequirePathException("'" + asset.getAssetPath() + "' and '" +
						locationAsset.getAssetPath() + "' source files both available via require path '" +
						requirePathSuffix + "'.");
				}
			}
		}
		
		if(asset == null) {
			throw new UnresolvableRequirePathException(requirePathSuffix);
		}
		
		return asset;
	}
}
