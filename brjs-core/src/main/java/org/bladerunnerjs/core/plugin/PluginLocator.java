package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.BRJS;



public interface PluginLocator
{

	public List<BundlerPlugin> createBundlerPlugins(BRJS brjs);

	public List<CommandPlugin> createCommandPlugins(BRJS brjs);

	public List<ModelObserverPlugin> createModelObservers(BRJS brjs);
}
