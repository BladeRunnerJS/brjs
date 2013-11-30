package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
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
