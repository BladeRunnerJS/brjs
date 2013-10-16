package com.caplin.cutlass.bundler.js.minification;

import com.google.javascript.jscomp.CompilationLevel;

public class WhitespaceClosureCompilerMinifier extends AbstractClosureCompilerMinifier
{
	public WhitespaceClosureCompilerMinifier()
	{
		CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(compilerOptions);
		compilerOptions.lineBreak = true;
	}
}
