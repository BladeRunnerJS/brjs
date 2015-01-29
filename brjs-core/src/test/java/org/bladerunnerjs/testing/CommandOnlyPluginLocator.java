package org.bladerunnerjs.testing;

import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.PluginLocator;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.utility.PluginLoader;

public class CommandOnlyPluginLocator implements PluginLocator {
	private List<CommandPlugin> commandPlugins;
	
	@Override
	public void createPlugins(BRJS brjs) {
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
	}
	
	@Override
	public List<CommandPlugin> getCommandPlugins() {
		return commandPlugins;
	}
	
	@Override
	public List<ModelObserverPlugin> getModelObserverPlugins() {
		return Collections.emptyList();
	}
	
	@Override
	public List<MinifierPlugin> getMinifierPlugins() {
		return Collections.emptyList();
	}
	
	@Override
	public List<ContentPlugin> getContentPlugins() {
		return Collections.emptyList();
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return Collections.emptyList();
	}
	
	@Override
	public List<AssetPlugin> getAssetPlugins() {
		return Collections.emptyList();
	}
	
	@Override
	public List<AssetLocationPlugin> getAssetLocationPlugins() {
		return Collections.emptyList();
	}

	@Override
	public List<RequirePlugin> getRequirePlugins() {
		return Collections.emptyList();
	}
}
