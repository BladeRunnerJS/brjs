package com.caplin.cutlass.bundler.js.minification;

import com.google.javascript.jscomp.CompilationLevel;

public class AdvancedClosureCompilerMinifier extends AbstractClosureCompilerMinifier
{
	public AdvancedClosureCompilerMinifier()
	{
		CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
	}
}
