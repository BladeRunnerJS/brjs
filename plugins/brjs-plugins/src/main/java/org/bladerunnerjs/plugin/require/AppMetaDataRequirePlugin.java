package org.bladerunnerjs.plugin.require;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.plugin.plugins.require.AppMetaDataSourceModule;

public class AppMetaDataRequirePlugin extends AbstractRequirePlugin implements RequirePlugin
{

	private BRJS brjs;
	private RequirePlugin defaultRequirePlugin;

	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		this.defaultRequirePlugin = brjs.plugins().requirePlugin("default");
	}

	@Override
	public String getPluginName() {
		return "app-meta";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		String requirePath = getPluginName() + "!" + requirePathSuffix;

		if (requirePath.equals(AppMetaDataSourceModule.PRIMARY_REQUIRE_PATH)) {
			return new AppMetaDataSourceModule(brjs, bundlableNode);
		}

		return defaultRequirePlugin.getAsset(bundlableNode, requirePath);
	}

}
