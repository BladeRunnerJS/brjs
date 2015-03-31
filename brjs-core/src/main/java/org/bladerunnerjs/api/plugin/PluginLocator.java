package org.bladerunnerjs.api.plugin;

import java.util.List;

import org.bladerunnerjs.api.BRJS;


public interface PluginLocator
{
	// if more plugin types are added defaults should be added to BladerunnerConf
	void createPlugins(BRJS brjs);
	List<CommandPlugin> getCommandPlugins();
	List<ModelObserverPlugin> getModelObserverPlugins();
	List<ContentPlugin> getContentPlugins();
	List<TagHandlerPlugin> getTagHandlerPlugins();
	List<AssetPlugin> assetPlugins();
	List<MinifierPlugin> getMinifierPlugins();
	List<RequirePlugin> getRequirePlugins();
}
