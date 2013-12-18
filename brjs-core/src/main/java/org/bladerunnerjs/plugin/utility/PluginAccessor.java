package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.BundlerTagHandlerPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.BundlerContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.aliasing.AliasingBundlerContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty.BRJSThirdpartyBundlerContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerContentPlugin;
import org.bladerunnerjs.plugin.utility.command.CommandList;

public class PluginAccessor {
	private final PluginLocator pluginLocator;
	private final CommandList commandList;
	
	public PluginAccessor(BRJS brjs, PluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
		commandList = new CommandList(brjs, pluginLocator.getCommandPlugins());
	}
	
	public CommandList commandList() {
		return commandList;
	}
	
	public List<CommandPlugin> commands() {
		return commandList.getPluginCommands();
	}
	
	public BundlerContentPlugin bundlerContentProvider(BladerunnerUri requestUri) {
		String requestPrefix = (requestUri.logicalPath.indexOf('/') == -1) ? requestUri.logicalPath : requestUri.logicalPath.substring(0, requestUri.logicalPath.indexOf('/'));
		
		return bundlerContentProvider(requestPrefix);
	}
	
	public BundlerContentPlugin bundlerContentProvider(String requestPrefix) {
		BundlerContentPlugin contentPlugin = null;
		
		for (BundlerContentPlugin nextContentPlugin : bundlerContentProviders()) {
			if(nextContentPlugin.getRequestPrefix().equals(requestPrefix)) {
				contentPlugin = nextContentPlugin;
				break;
			}
		}
		
		return contentPlugin;
	}
	
	public List<BundlerContentPlugin> bundlerContentProviders() {
		List<BundlerContentPlugin> bundlerContentProviders = pluginLocator.getBundlerContentPlugins();
		
		orderContentPlugins(bundlerContentProviders);
		
		return bundlerContentProviders;
	}

	public List<BundlerContentPlugin> bundlerContentProviders(String mimeType) {
		List<BundlerContentPlugin> bundlerContentPlugins = new ArrayList<>();
		
		for (BundlerContentPlugin bundlerContentPlugin : bundlerContentProviders()) {
			if (bundlerContentPlugin.getMimeType().equals(mimeType)) {
				bundlerContentPlugins.add(bundlerContentPlugin);
			}
		}
		
		return bundlerContentPlugins;
	}
	
	public List<TagHandlerPlugin> tagHandlers() {
		List<TagHandlerPlugin> tagHandlers = new ArrayList<>();
		tagHandlers.addAll(pluginLocator.getTagHandlerPlugins());
		tagHandlers.addAll(pluginLocator.getBundlerTagHandlerPlugins());
		
		return tagHandlers;
	}
	
	public List<BundlerTagHandlerPlugin> bundlerTagHandlers() {
		return pluginLocator.getBundlerTagHandlerPlugins();
	}
	
	public List<BundlerTagHandlerPlugin> bundlerTagHandlers(String mimeType) {
		List<BundlerTagHandlerPlugin> bundlerTagHandlerPlugins = new ArrayList<>();
		
		for (BundlerTagHandlerPlugin bundlerTagHandlerPlugin : bundlerTagHandlers()) {
			if (bundlerTagHandlerPlugin.getMimeType().equals(mimeType)) {
				bundlerTagHandlerPlugins.add(bundlerTagHandlerPlugin);
			}
		}
		
		return bundlerTagHandlerPlugins;
	}
	
	public List<MinifierPlugin> minifiers() {
		return pluginLocator.getMinifierPlugins();
	}
	
	public MinifierPlugin minifier(String minifierSetting) {
		List<String> validMinificationSettings = new ArrayList<String>();
		MinifierPlugin pluginForMinifierSetting = null;
		
		for (MinifierPlugin minifierPlugin : minifiers()) {
			for (String setting : minifierPlugin.getSettingNames()) {
				validMinificationSettings.add(setting);
				
				if (setting.equals(minifierSetting)) {
					pluginForMinifierSetting = (pluginForMinifierSetting == null) ? minifierPlugin : pluginForMinifierSetting;
				}
			}
		}
		
		if (pluginForMinifierSetting != null) {
			return pluginForMinifierSetting;
		}
		
		throw new RuntimeException("No minifier plugin for minifier setting '" + minifierSetting + "'. Valid settings are: "
			+ StringUtils.join(validMinificationSettings, ", "));
	}
	
	public List<ModelObserverPlugin> modelObservers() {
		return pluginLocator.getModelObserverPlugins();
	}
	
	public List<AssetPlugin> assetProducers() {
		List<AssetPlugin> plugins = pluginLocator.getAssetPlugins();
		
		orderAssetPlugins(plugins);
		
		return plugins;
	}
	
	// TODO: it's becoming more and more obvious that we need a proper ordering mechanism for bundler content plug-ins, so external plug-in developers can have their plug-ins correctly ordered too
	private void orderContentPlugins(List<BundlerContentPlugin> bundlerContentProviders) {
		Collections.sort(bundlerContentProviders, new Comparator<BundlerContentPlugin>() {
			@Override
			public int compare(BundlerContentPlugin bundlerContentPlugin1, BundlerContentPlugin bundlerContentPlugin2) {
				return score(bundlerContentPlugin1) - score(bundlerContentPlugin2);
			}
			
			private int score(BundlerContentPlugin bundlerContentPlugin) {
				int score = 0;
				
				if(bundlerContentPlugin.instanceOf(BRJSThirdpartyBundlerContentPlugin.class)) {
					score = -1;
				}
				else if(bundlerContentPlugin.instanceOf(AliasingBundlerContentPlugin.class)) {
					score = 1;
				}
				else if(bundlerContentPlugin.instanceOf(NamespacedJsBundlerContentPlugin.class)) {
					score = 2;
				}
				
				return score;
			}
		});
	}
	
	private void orderAssetPlugins(List<AssetPlugin> plugins) {
		Collections.sort(plugins, new Comparator<AssetPlugin>() {
			@Override
			public int compare(AssetPlugin assetPlugin1, AssetPlugin assetPlugin2) {
				return score(assetPlugin1) - score(assetPlugin2);
			}
			
			private int score(AssetPlugin assetPlugin) {
				return (assetPlugin.instanceOf(BRJSConformantAssetPlugin.class)) ? 1 : 0;
			}
		});
	}
}
