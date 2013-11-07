package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.minifier.ClosureMinifierPlugin;
import org.bladerunnerjs.core.plugin.minifier.ConcatentatingMinifierPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;

public class MinifierPluginFactory {
	public static List<MinifierPlugin> createMinifierPlugins(BRJS brjs) {
		List<MinifierPlugin> minifierPlugins = new ArrayList<>();
		
		// 
		minifierPlugins.add(new ConcatentatingMinifierPlugin());
		minifierPlugins.add(new ClosureMinifierPlugin());
		
		return minifierPlugins;
	}
}
