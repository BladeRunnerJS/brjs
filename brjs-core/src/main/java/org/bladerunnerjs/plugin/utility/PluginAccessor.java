package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.i18n.I18nAssetPlugin;
import org.bladerunnerjs.plugin.utility.command.CommandList;

public class PluginAccessor {
	private final PluginLocator pluginLocator;
	private final CommandList commandList;
	
	public PluginAccessor(BRJS brjs, PluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
		commandList = new CommandList(brjs, pluginLocator.getCommandPlugins());
	}
	
	public List<Plugin> allPlugins() {
		List<Plugin> plugins = new ArrayList<>();
		
		plugins.addAll(commands());
		plugins.addAll(contentProviders());
		plugins.addAll(tagHandlers());
		plugins.addAll(minifiers());
		plugins.addAll(modelObservers());
		plugins.addAll(assetProducers());
		plugins.addAll(assetLocationProducers());
		
		return plugins;
	}
	
	public CommandList commandList() {
		return commandList;
	}
	
	public List<CommandPlugin> commands() {
		return orderPlugins( commandList.getPluginCommands() );
	}
	
	public ContentPlugin contentProvider(BladerunnerUri requestUri) {
		return contentProviderForLogicalPath(requestUri.logicalPath);
	}
	
	public ContentPlugin contentProviderForLogicalPath(String logicalRequestpath)
	{
		String requestPrefix = (logicalRequestpath.indexOf('/') == -1) ? logicalRequestpath : logicalRequestpath.substring(0, logicalRequestpath.indexOf('/'));
		
		return contentProvider(requestPrefix);
	}
	
	public ContentPlugin contentProvider(String requestPrefix) {
		ContentPlugin contentProvider = null;
		
		for (ContentPlugin nextContentPlugin : contentProviders()) {
			if(nextContentPlugin.getRequestPrefix().equals(requestPrefix)) {
				contentProvider = nextContentPlugin;
				break;
			}
		}
		
		return contentProvider;
	}
	
	public List<ContentPlugin> contentProviders() {
		return orderPlugins( pluginLocator.getContentPlugins() );
	}

	public List<ContentPlugin> contentProviders(String groupName) {
		List<ContentPlugin> contentProviders = new LinkedList<>();
		
		for (ContentPlugin contentPlugin : contentProviders()) {
			if (groupName.equals(contentPlugin.getGroupName())) {
				contentProviders.add(contentPlugin);
			}
		}
		
		return contentProviders;
	}
	
	public List<TagHandlerPlugin> tagHandlers() {
		return orderPlugins( pluginLocator.getTagHandlerPlugins() );
	}
	
	public List<TagHandlerPlugin> tagHandlers(String groupName) {
		List<TagHandlerPlugin> tagHandlerPlugins = new LinkedList<>();
		
		for (TagHandlerPlugin tagHandlerPlugin : tagHandlers()) {
			if (groupName.equals(tagHandlerPlugin.getGroupName())) {
				tagHandlerPlugins.add(tagHandlerPlugin);
			}
		}
		
		return tagHandlerPlugins;
	}
	
	public List<MinifierPlugin> minifiers() {
		return orderPlugins( pluginLocator.getMinifierPlugins() );
	}
	
	public MinifierPlugin minifier(String minifierSetting) {
		List<String> validMinificationSettings = new LinkedList<String>();
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
		return orderPlugins( pluginLocator.getModelObserverPlugins() );
	}
	
	public List<AssetPlugin> assetProducers() {
		return orderPlugins( pluginLocator.getAssetPlugins() );
	}
	
	public List<AssetLocationPlugin> assetLocationProducers() {
		return orderPlugins( pluginLocator.getAssetLocationPlugins() );
	}
	
	public AssetPlugin assetProducer(Class<?> pluginClass ) {
		
		AssetPlugin result = null;
		List<AssetPlugin> assetProducers = assetProducers();
		for(AssetPlugin producer: assetProducers){
			Class<?> possiblePluginClass = producer.getPluginClass();
			if(possiblePluginClass.equals(pluginClass)){
				result =  producer;
				break;
			}
		}
		return result;
	}
	
	private <P extends Plugin> List<P> orderPlugins(List<P> plugins) {
		Collections.sort(plugins, new Comparator<Plugin>() {
			@Override
			public int compare(Plugin plugin1, Plugin plugin2) {
				// reverse sort so higher priority == top of list
				return Integer.compare( PluginPriorityCalculator.priority(plugin2), PluginPriorityCalculator.priority((plugin1)) );
			}
		});		
		return plugins;
	}
	
}
