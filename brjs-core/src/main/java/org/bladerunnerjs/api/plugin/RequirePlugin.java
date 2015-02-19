package org.bladerunnerjs.api.plugin;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.BundlableNode;

public interface RequirePlugin extends Plugin {
	String getPluginName();
	Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException;
}
