package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.OrderedPlugin;
import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.api.plugin.PluginLocator;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;

public class PluginAccessor {
	
	private final CommandList commandList;
	private final List<ContentPlugin> contentPlugins;
	private final List<TagHandlerPlugin> tagHandlerPlugins;
	private final List<MinifierPlugin> minifierPlugins;
	private final List<ModelObserverPlugin> modelObserverPlugins;
	private final List<AssetPlugin> assetPlugins;
	private final List<AssetLocationPlugin> assetLocationPlugins;
	private final List<RequirePlugin> requirePlugins;
	
	public PluginAccessor(BRJS brjs, PluginLocator pluginLocator) {
		commandList = new CommandList(brjs, pluginLocator.getCommandPlugins());
		contentPlugins = sort(pluginLocator.getContentPlugins());
		tagHandlerPlugins = pluginLocator.getTagHandlerPlugins();
		minifierPlugins = pluginLocator.getMinifierPlugins();
		modelObserverPlugins = pluginLocator.getModelObserverPlugins();
		assetPlugins = sort(pluginLocator.getAssetPlugins());
		assetLocationPlugins = sort(pluginLocator.getAssetLocationPlugins());
		requirePlugins = pluginLocator.getRequirePlugins();
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
		plugins.addAll(requirePlugins());
		
		return plugins;
	}
	
	public List<CommandPlugin> commandPlugins() {
		return commandList.getPluginCommands();
	}
	
	public CommandPlugin commandPlugin(String commandName) {
		return commandList.lookupCommand(commandName);
	}
	
	public List<CommandPlugin> getCoreCommandPlugins() {
		return commandList.getCoreCommands();
	}
	
	public List<CommandPlugin> getNonCoreCommandPlugins() {
		return commandList.getPluginCommands();
	}
	
	public void addCommandPlugin(BRJS brjs, CommandPlugin commandPlugin) {
		commandList.addCommand(commandPlugin);
		commandPlugin.setBRJS(brjs);
	}
	
	public ContentPlugin contentPluginForLogicalPath(String logicalRequestpath)
	{
		String requestPrefix = logicalRequestpath.replaceFirst("^/", "").split("/")[0];
		
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
			if(contentPlugin.instanceOf(CompositeContentPlugin.class)) {
				CompositeContentPlugin compositeContentPlugin = (CompositeContentPlugin) contentPlugin.castTo(CompositeContentPlugin.class);
				
				if (groupName.equals(compositeContentPlugin.getCompositeGroupName())) {
					contentProviders.add(contentPlugin);
				}
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
	
	public void addModelObserverPlugin(ModelObserverPlugin modelObserver) {
		modelObserverPlugins.add(modelObserver);
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
	
	public List<RequirePlugin> requirePlugins() {
		return requirePlugins;
	}
	
	public RequirePlugin requirePlugin(String pluginName) {
		for (RequirePlugin requirePlugin : requirePlugins()) {
			if(requirePlugin.getPluginName().equals(pluginName)) {
				return requirePlugin;
			}
		}
		return null;
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
