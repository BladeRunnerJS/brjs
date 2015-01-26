package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.model.BundlableNode;

public class VirtualProxyRequirePlugin extends VirtualProxyPlugin implements RequirePlugin {
	private final RequirePlugin requirePlugin;

	public VirtualProxyRequirePlugin(RequirePlugin requirePlugin) {
		super(requirePlugin);
		this.requirePlugin = requirePlugin;
	}

	public String getPluginName() {
		return requirePlugin.getPluginName();
	}

	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		initializePlugin();
		return requirePlugin.getAsset(bundlableNode, requirePathSuffix);
	}
}
