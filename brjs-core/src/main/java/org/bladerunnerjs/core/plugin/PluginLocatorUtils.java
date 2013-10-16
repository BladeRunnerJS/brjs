package org.bladerunnerjs.core.plugin;

import java.util.List;

import org.bladerunnerjs.model.BRJS;


public class PluginLocatorUtils
{

	public static List<? extends Plugin> setBRJSForPlugins(BRJS brjs, List<? extends Plugin> plugins)
	{
		for (Plugin p : plugins)
		{
			p.setBRJS(brjs);
		}
		return plugins;
	}
	
}
