package org.bladerunnerjs.api.plugin.base;

import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.plugin.AssetPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link AssetPlugin}.
 */
public abstract class AbstractAssetPlugin extends AbstractPlugin implements AssetPlugin {
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Collections.emptyList();
	}
}
