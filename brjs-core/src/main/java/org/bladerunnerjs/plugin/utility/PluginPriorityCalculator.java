package org.bladerunnerjs.plugin.utility;

import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.aliasing.AliasingContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.aliasing.AliasingTagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty.BRJSThirdpartyContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty.BRJSThirdpartyTagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.i18n.I18nContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.i18n.I18nTagHandlerPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsTagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;

public class PluginPriorityCalculator
{

	static int priority(Plugin plugin)
	{
		plugin = (plugin instanceof VirtualProxyPlugin) ? ((VirtualProxyPlugin) plugin).getUnderlyingPlugin() : plugin;
		
		if (plugin instanceof AssetLocationPlugin)
		{
			return priority( (AssetLocationPlugin) plugin);
		}
		if (plugin instanceof ContentPlugin)
		{
			return priority( (ContentPlugin) plugin);
		}
		if (plugin instanceof TagHandlerPlugin)
		{
			return priority( (TagHandlerPlugin) plugin);
		}
		
		return 0;
	}
	
	
	
	private static int priority(AssetLocationPlugin plugin)
	{
		if (plugin.getPluginClass() == BRJSConformantAssetLocationPlugin.class)
		{
			return -1;
		}
		return 0;
	}
	
	private static int priority(ContentPlugin plugin)
	{
		if (plugin.getPluginClass() == BRJSThirdpartyContentPlugin.class)
		{
			return 2;
		}
		if (plugin.getPluginClass() == I18nContentPlugin.class)
		{
			return 1;
		}
		if (plugin.getPluginClass() == NamespacedJsContentPlugin.class)
		{
			return -1;
		}
		if (plugin.getPluginClass() == AliasingContentPlugin.class)
		{
			return -2;
		}
		return 0;
	}
	
	private static int priority(TagHandlerPlugin plugin)
	{
		if (plugin.getPluginClass() == BRJSThirdpartyTagHandlerPlugin.class)
		{
			return 2;
		}
		if (plugin.getPluginClass() == I18nTagHandlerPlugin.class)
		{
			return 1;
		}
		if (plugin.getPluginClass() == NamespacedJsTagHandlerPlugin.class)
		{
			return -1;
		}
		if (plugin.getPluginClass() == AliasingTagHandlerPlugin.class)
		{
			return -2;
		}
		return 0;
	}
	
}
