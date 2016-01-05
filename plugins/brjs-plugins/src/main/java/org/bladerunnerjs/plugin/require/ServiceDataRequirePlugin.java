package org.bladerunnerjs.plugin.require;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;


public class ServiceDataRequirePlugin extends AbstractRequirePlugin implements RequirePlugin
{
	
	private RequirePlugin defaultRequirePlugin;

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.defaultRequirePlugin = brjs.plugins().requirePlugin("default");
	}

	@Override
	public String getPluginName()
	{
		return "alias";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException
	{
		String requirePath = getPluginName()+"!"+requirePathSuffix;
		if (requirePath.equals(AliasDataSourceModule.PRIMARY_REQUIRE_PATH)) {
			return bundlableNode.asset(requirePath);
		}
		return defaultRequirePlugin.getAsset(bundlableNode, requirePath);
	}
	
}

