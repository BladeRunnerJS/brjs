package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.OrderedPlugin;
import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.PluginLocator;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.utility.command.CommandList;

public class PluginAccessor {
	private final CommandList commandList;
	private final List<ContentPlugin> contentPlugins;
	private final List<TagHandlerPlugin> tagHandlerPlugins;
	private final List<MinifierPlugin> minifierPlugins;
	private final List<ModelObserverPlugin> modelObserverPlugins;
	private final List<AssetPlugin> assetPlugins;
	private final List<AssetLocationPlugin> assetLocationPlugins;
	
	public PluginAccessor(BRJS brjs, PluginLocator pluginLocator) {
		commandList = new CommandList(brjs, pluginLocator.getCommandPlugins());
		contentPlugins = sort(pluginLocator.getContentPlugins());
		tagHandlerPlugins = pluginLocator.getTagHandlerPlugins();
		minifierPlugins = pluginLocator.getMinifierPlugins();
		modelObserverPlugins = pluginLocator.getModelObserverPlugins();
		assetPlugins = sort(pluginLocator.getAssetPlugins());
		assetLocationPlugins = sort(pluginLocator.getAssetLocationPlugins());
	}
	
	public List<Plugin> allPlugins() {
		List<Plugin> plugins = new ArrayList<>();
		
		plugins.addAll(commandPlugins());
		plugins.addAll(contentPlugins());
		plugins.addAll(tagHandlerPlugins());
		plugins.addAll(minifierPlugins());
		plugins.addAll(modelObserverPlugins());
		plugins.addAll(assetPlugins());
		plugins.addAll(assetLocationPlugins());
		
		return plugins;
	}
	
	public CommandList commandList() {
		return commandList;
	}
	
	public List<CommandPlugin> commandPlugins() {
		return commandList.getPluginCommands();
	}
	
	public ContentPlugin contentPluginForLogicalPath(String logicalRequestpath)
	{
		String requestPrefix = logicalRequestpath.substring(0, logicalRequestpath.indexOf('/'));
		
		return contentPlugin(requestPrefix);
	}
	
	public ContentPlugin contentPlugin(String requestPrefix) {
		for (ContentPlugin contentPlugin : contentPlugins()) {
			if(contentPlugin.getRequestPrefix().equals(requestPrefix)) {
				return contentPlugin;
			}
		}
		return null;
	}
	
	public List<ContentPlugin> contentPlugins() {
		return contentPlugins;
	}
	
	public List<ContentPlugin> contentPlugins(String groupName) {
		List<ContentPlugin> contentProviders = new LinkedList<>();
		
		for (ContentPlugin contentPlugin : contentPlugins()) {
			if (groupName.equals(contentPlugin.getCompositeGroupName())) {
				contentProviders.add(contentPlugin);
			}
		}
		
		return contentProviders;
	}
	
	public List<TagHandlerPlugin> tagHandlerPlugins() {
		return tagHandlerPlugins;
	}
	
	public TagHandlerPlugin tagHandlerPlugin(String tagName) {
		for (TagHandlerPlugin tagHandler : tagHandlerPlugins()) {
			if(tagHandler.getTagName().equals(tagName)) {
				return tagHandler;
			}
		}
		return null;
	}
	
	public List<MinifierPlugin> minifierPlugins() {
		return minifierPlugins;
	}
	
	public MinifierPlugin minifierPlugin(String minifierSetting) {
		List<String> validMinificationSettings = new LinkedList<String>();
		MinifierPlugin pluginForMinifierSetting = null;
		
		for (MinifierPlugin minifierPlugin : minifierPlugins()) {
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
	
	public List<ModelObserverPlugin> modelObserverPlugins() {
		return modelObserverPlugins;
	}
	
	public List<AssetPlugin> assetPlugins() {
		return assetPlugins;
	}
	
	public List<AssetLocationPlugin> assetLocationPlugins() {
		return assetLocationPlugins;
	}
	
	public AssetPlugin assetPlugin(Class<?> pluginClass ) {
		AssetPlugin result = null;
		List<AssetPlugin> assetProducers = assetPlugins();
		for(AssetPlugin producer: assetProducers){
			Class<?> possiblePluginClass = producer.getPluginClass();
			if(possiblePluginClass.equals(pluginClass)){
				result =  producer;
				break;
			}
		}
		return result;
	}
	
	private <P extends OrderedPlugin> List<P> sort(List<P> plugins) {
		try {
			return PluginSorter.sort(plugins);
		}
		catch (PluginOrderingException | NonExistentPluginException e) {
			throw new RuntimeException(e);
		}
	}
}
