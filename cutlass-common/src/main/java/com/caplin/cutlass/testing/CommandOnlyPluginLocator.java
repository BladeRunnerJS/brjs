package com.caplin.cutlass.testing;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.PluginAccessor;
import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLocator;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.BRJS;

public class CommandOnlyPluginLocator implements PluginLocator {
	private PluginLocator brjsPluginLocator = new PluginAccessor();
	
	@Override
	public List<CommandPlugin> createCommandPlugins(BRJS brjs) {
		return brjsPluginLocator.createCommandPlugins(brjs);
	}
	
	@Override
	public List<ModelObserverPlugin> createModelObservers(BRJS brjs) {
		return new ArrayList<>();
	}
	
	@Override
	public List<BundlerPlugin> createBundlerPlugins(BRJS brjs) {
		return new ArrayList<>();
	}
}
