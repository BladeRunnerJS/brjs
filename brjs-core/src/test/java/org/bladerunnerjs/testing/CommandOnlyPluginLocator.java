package org.bladerunnerjs.testing;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.BundlerPlugin;
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
		return new ArrayList<>();
	}
	
	@Override
	public List<BundlerPlugin> getBundlerPlugins() {
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
}
