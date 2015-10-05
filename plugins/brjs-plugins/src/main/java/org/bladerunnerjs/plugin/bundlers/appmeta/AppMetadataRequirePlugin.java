package org.bladerunnerjs.plugin.bundlers.appmeta;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;

public class AppMetadataRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	private RequirePlugin defaultRequirePlugin;

	@Override
	public void setBRJS(BRJS brjs) {
		this.defaultRequirePlugin = brjs.plugins().requirePlugin("default");
	}

	@Override
	public String getPluginName() {
		return "appmeta";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		String requirePath = getPluginName() + "!" + requirePathSuffix;
		if (requirePath.equals(AppMetadataSourceModule.APP_META_DATA)) {
			return bundlableNode.asset(requirePath);
		}
		return defaultRequirePlugin.getAsset(bundlableNode, requirePath);
	}
}
