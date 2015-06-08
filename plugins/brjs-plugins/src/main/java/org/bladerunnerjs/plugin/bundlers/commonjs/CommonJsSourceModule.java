package org.bladerunnerjs.plugin.bundlers.commonjs;

import org.bladerunnerjs.api.SourceModule;

public interface CommonJsSourceModule extends SourceModule {
	public static final String JS_STYLE = "common-js";
	public static final String COMMONJS_DEFINE_BLOCK_HEADER = "System.registerDynamic('%s', %s, true, function(require, exports, module) {\n";
	public static final String COMMONJS_DEFINE_BLOCK_FOOTER = "\n\n\treturn module.exports;\n});\n";
}