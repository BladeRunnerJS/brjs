package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.BRJS;


public interface PluginLocator
{
	void createPlugins(BRJS brjs);
	List<CommandPlugin> getCommandPlugins();
	List<ContentPlugin> getContentPlugins();
	List<BundlerPlugin> getBundlerPlugins();
	List<TagHandlerPlugin> getTagHandlerPlugins();
	List<MinifierPlugin> getMinifierPlugins();
	List<ModelObserverPlugin> getModelObserverPlugins();
}
