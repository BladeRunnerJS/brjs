package org.bladerunnerjs.api.plugin;

import java.util.List;

public interface OrderedPlugin extends Plugin {
	List<String> getPluginsThatMustAppearBeforeThisPlugin();
	List<String> getPluginsThatMustAppearAfterThisPlugin();
}
