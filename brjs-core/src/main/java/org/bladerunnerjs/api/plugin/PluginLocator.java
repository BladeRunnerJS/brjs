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
	List<LegacyAssetPlugin> getLegacyAssetPlugins();
	List<LegacyAssetLocationPlugin> getLegacyAssetLocationPlugins();
	List<AssetPlugin> assetPlugins();
	List<MinifierPlugin> getMinifierPlugins();
	List<RequirePlugin> getRequirePlugins();
}
