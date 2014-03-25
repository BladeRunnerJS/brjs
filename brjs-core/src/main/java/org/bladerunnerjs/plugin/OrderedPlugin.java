package org.bladerunnerjs.plugin;

import java.util.List;

public interface OrderedPlugin extends Plugin {
	List<String> getPluginsThatMustAppearBeforeThisPlugin();
	List<String> getPluginsThatMustAppearAfterThisPlugin();
}
