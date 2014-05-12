package org.bladerunnerjs.plugin.base;

import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.plugin.ContentPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link ContentPlugin}.
 */
public abstract class AbstractContentPlugin extends AbstractPlugin implements ContentPlugin {
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Collections.emptyList();
	}
}
