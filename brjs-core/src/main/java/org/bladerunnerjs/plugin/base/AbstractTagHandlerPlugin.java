package org.bladerunnerjs.plugin.base;

import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.plugin.TagHandlerPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link TagHandlerPlugin}.
 */
public abstract class AbstractTagHandlerPlugin extends AbstractPlugin implements TagHandlerPlugin {
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Collections.emptyList();
	}
}
