package org.bladerunnerjs.plugin.bundlers.commonjs;

import org.bladerunnerjs.model.AugmentedContentSourceModule;

public interface CommonJsSourceModule extends AugmentedContentSourceModule {
	public static final String JS_STYLE = "common-js";
	public static final String COMMONJS_DEFINE_BLOCK_HEADER = "define('%s', function(require, exports, module) {\n";
	public static final String COMMONJS_DEFINE_BLOCK_FOOTER = "\n});\n";
}