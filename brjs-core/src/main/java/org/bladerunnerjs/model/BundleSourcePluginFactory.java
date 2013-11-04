package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.core.plugin.bundlesource.js.CaplinJsBundleSourcePlugin;
import org.bladerunnerjs.core.plugin.bundlesource.js.NodeJsBundleSourcePlugin;

public class BundleSourcePluginFactory {
	public static List<BundleSourcePlugin> createBundleSourcePlugins(BRJS brjs) {
		List<BundleSourcePlugin> bundleSourcePlugins = new ArrayList<>();
		
		// TODO: start discovering these plug-ins automatically
		bundleSourcePlugins.add(new CaplinJsBundleSourcePlugin());
		bundleSourcePlugins.add(new NodeJsBundleSourcePlugin());
		
		for(BundleSourcePlugin bundleSourcePlugin : bundleSourcePlugins) {
			bundleSourcePlugin.setBRJS(brjs);
		}
		
		return bundleSourcePlugins;
	}
}
