package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.html.HTMLBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.js.CompositeJsBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.xml.XMLBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundlesource.js.CaplinJsBundlerPlugin;
//import org.bladerunnerjs.core.plugin.bundlesource.js.NodeJsBundlerPlugin;

public class BundlePluginFactory {
	public static Map<String, BundlerPlugin> createBundlerPlugins(BRJS brjs) {
		Map<String, BundlerPlugin> bundlerPlugins = new HashMap<>();
		List<BundlerPlugin> bundlerPluginList = new ArrayList<>();
		
		// TODO: start discovering these plug-ins automatically
		bundlerPluginList.add(new CompositeJsBundlerPlugin());
		bundlerPluginList.add(new CaplinJsBundlerPlugin());
		bundlerPluginList.add(new XMLBundlerPlugin());
		bundlerPluginList.add(new HTMLBundlerPlugin());
//		bundlerPluginList.add(new NodeJsBundlerPlugin());
		
		for(BundlerPlugin bundlerPlugin : bundlerPluginList) {
			bundlerPlugin.setBRJS(brjs);
			bundlerPlugins.put(bundlerPlugin.getTagName(), bundlerPlugin);
		}
		
		return bundlerPlugins;
	}
}
