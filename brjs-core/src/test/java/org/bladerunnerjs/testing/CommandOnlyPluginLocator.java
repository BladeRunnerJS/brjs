package org.bladerunnerjs.testing;

import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
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
}
