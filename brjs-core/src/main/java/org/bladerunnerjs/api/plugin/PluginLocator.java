package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.BRJS;


public interface PluginLocator
{
	void createPlugins(BRJS brjs);
	List<CommandPlugin> getCommandPlugins();
	List<ModelObserverPlugin> getModelObserverPlugins();
	List<ContentPlugin> getContentPlugins();
	List<TagHandlerPlugin> getTagHandlerPlugins();
	List<AssetPlugin> getAssetPlugins();
	List<AssetLocationPlugin> getAssetLocationPlugins();
	List<MinifierPlugin> getMinifierPlugins();
	List<RequirePlugin> getRequirePlugins();
}
