package org.bladerunnerjs.plugin.base;

import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link AssetPlugin}.
 */
public abstract class AbstractAssetLocationPlugin extends AbstractPlugin implements AssetLocationPlugin {
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Collections.emptyList();
	}
}
