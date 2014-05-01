package org.bladerunnerjs.model;

import java.io.File;

public final class ResourcesAssetLocation extends AbstractResourcesAssetLocation {
	public ResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file) {
		super(root, assetContainer, file);
		registerInitializedNode();
	}
}
