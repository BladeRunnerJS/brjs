package com.caplin.cutlass.bundler.js.minification;

import com.google.javascript.jscomp.CompilationLevel;

public class SimpleClosureCompilerMinifier extends AbstractClosureCompilerMinifier
{
	public SimpleClosureCompilerMinifier()
	{
		CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
	}
}
