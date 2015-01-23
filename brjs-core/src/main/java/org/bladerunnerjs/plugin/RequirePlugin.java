package org.bladerunnerjs.plugin;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.RequirePathException;

public interface RequirePlugin extends Plugin {
	String getPluginName();
	Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException;
}
