package org.bladerunnerjs.model;

import java.util.List;


public interface ScopedNode
{
	/**
	 * Returns all AssetContainers that contain resources that can potentially be bundled for this BundleableNode
	 */
	List<AssetContainer> scopeAssetContainers();
}
