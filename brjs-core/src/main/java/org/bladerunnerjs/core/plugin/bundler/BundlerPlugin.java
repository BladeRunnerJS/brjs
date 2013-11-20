package org.bladerunnerjs.core.plugin.bundler;

import org.bladerunnerjs.core.plugin.servlet.ContentPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.AssetFileAccessor;


public interface BundlerPlugin extends TagHandlerPlugin, ContentPlugin {
	AssetFileAccessor getAssetFileAccessor();
}
