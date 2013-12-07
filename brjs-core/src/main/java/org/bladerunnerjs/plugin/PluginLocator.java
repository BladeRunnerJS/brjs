package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.plugin.command.CommandPlugin;
import org.bladerunnerjs.plugin.content.ContentPlugin;
import org.bladerunnerjs.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.plugin.taghandler.TagHandlerPlugin;


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
