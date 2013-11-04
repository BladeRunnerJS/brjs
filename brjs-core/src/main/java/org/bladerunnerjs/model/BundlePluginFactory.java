package org.bladerunnerjs.model;

import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.js.JsBundlerPlugin;

public class BundlePluginFactory {
	public static Map<String, BundlerPlugin> createBundlerPlugins(BRJS brjs) {
		Map<String, BundlerPlugin> bundlerPlugins = new HashMap<>();
		
		// TODO: start discovering these plug-ins automatically
		bundlerPlugins.put("js", new JsBundlerPlugin());
		
		for(BundlerPlugin bundlerPlugin : bundlerPlugins.values()) {
			bundlerPlugin.setBRJS(brjs);
		}
		
		return bundlerPlugins;
	}
}
