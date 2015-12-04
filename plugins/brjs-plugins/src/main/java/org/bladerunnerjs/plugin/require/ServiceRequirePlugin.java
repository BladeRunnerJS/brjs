package org.bladerunnerjs.plugin.require;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.plugin.plugins.require.ServiceDataSourceModule;

public class ServiceRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	// TODO: update to be a bundlableNodeSourceModules as has been done in AliasRequirePlugin
	private final Map<String, ServiceCommonJsSourceModule> sourceModules = new LinkedHashMap<>();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public String getPluginName() {
		return "service";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		return getSourceModule(bundlableNode, requirePathSuffix);
	}
	
	private Asset getSourceModule(BundlableNode bundlableNode, String requirePathSuffix) {
		String requirePath = getPluginName()+"!"+requirePathSuffix;
		if ( requirePath.equals(ServiceDataSourceModule.PRIMARY_REQUIRE_PATH) ) {
			return bundlableNode.asset(requirePath);
		}
		
		if(!sourceModules.containsKey(requirePathSuffix)) {
			sourceModules.put(requirePathSuffix, new ServiceCommonJsSourceModule(bundlableNode, requirePathSuffix));
		}
		
		return sourceModules.get(requirePathSuffix);
	}
}
