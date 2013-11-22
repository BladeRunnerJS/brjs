package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.servlet.ContentPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.BRJS;


public interface PluginLocator
{
	void createPlugins(BRJS brjs);
	List<BundlerPlugin> getBundlerPlugins();
	List<CommandPlugin> getCommandPlugins();
	List<ModelObserverPlugin> getModelObservers();
	List<MinifierPlugin> getMinifiers();
	List<ContentPlugin> getContentPlugins();
	List<TagHandlerPlugin> getTagHandlers();
}
