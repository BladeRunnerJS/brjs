package com.caplin.cutlass.testing;

import java.util.ArrayList;
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
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.utility.PluginLoader;

public class CommandOnlyPluginLocator implements PluginLocator {
	private List<CommandPlugin> commandPlugins;
	private List<AssetLocationPlugin> assetLocationPlugins = new ArrayList<>();
	
	@Override
	public void createPlugins(BRJS brjs) {
		commandPlugins = PluginLoader.createPluginsOfType(brjs, CommandPlugin.class, VirtualProxyCommandPlugin.class);
		
		AssetLocationPlugin brjsConformantAssetPlugin = new VirtualProxyAssetLocationPlugin(new BRJSConformantAssetLocationPlugin());
		brjsConformantAssetPlugin.setBRJS(brjs);
		assetLocationPlugins.add(brjsConformantAssetPlugin);
	}
	
	@Override
	public List<CommandPlugin> getCommandPlugins() {
		return commandPlugins;
	}
	
	@Override
	public List<ModelObserverPlugin> getModelObserverPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<MinifierPlugin> getMinifierPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<ContentPlugin> getContentPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<TagHandlerPlugin> getTagHandlerPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<AssetPlugin> getAssetPlugins() {
		return new ArrayList<>();
	}
	
	@Override
	public List<AssetLocationPlugin> getAssetLocationPlugins() {
		return assetLocationPlugins;
	}
}
