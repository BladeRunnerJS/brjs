package org.bladerunnerjs.plugin.require;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.api.model.exception.OutOfScopeRequirePathException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;

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
		Asset asset = null;
		BRJS brjs = null;
		StringBuilder scopedLocations = new StringBuilder();
		for(AssetContainer assetContainer : bundlableNode.scopeAssetContainers()) {
			Asset locationAsset = assetContainer.asset(requirePathSuffix);

			if (scopedLocations.length() > 0) {
				scopedLocations.append(", ");
			}
			if (brjs == null) {
				brjs = assetContainer.root();
			}
			scopedLocations.append( brjs.dir().getRelativePath(assetContainer.dir()) );
			
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
			for (AssetContainer assetContainer : bundlableNode.app().getAllAssetContainers()) {
				Asset locationAsset = assetContainer.asset(requirePathSuffix);
				if (locationAsset != null) {
					throw new OutOfScopeRequirePathException(requirePathSuffix, bundlableNode.getClass().getSimpleName(), scopedLocations.toString(), locationAsset.getAssetPath());
				}
			}
			throw new UnresolvableRequirePathException(requirePathSuffix);
		}
		return asset;
	}
}
