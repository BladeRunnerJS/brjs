package org.bladerunnerjs.plugin.plugins.require;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.plugin.RequirePlugin;
import org.bladerunnerjs.plugin.base.AbstractRequirePlugin;

public class ServiceRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}

	@Override
	public String getPluginName() {
		return "service";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		return ServiceCommonJsSourceModule.getSourceModule(bundlableNode.root(), requirePathSuffix);
	}
}
