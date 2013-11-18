package org.bladerunnerjs.core.plugin.bundler;

import java.util.List;

import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.AssetFileAccessor;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;


public interface BundlerPlugin extends TagHandlerPlugin, ServletPlugin {
	AssetFileAccessor getAssetFileAccessor();
	List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
	List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException;
}
