package com.caplin.cutlass.bundler.js.minification;


public class MinifierFactory
{
	public static Minifier createMinifier(String minifierName)
	{
		Minifier minifier;
		
		if(minifierName == null) {
			minifier = new ConcatenatingMinifier();
		}
		else {
			switch(minifierName) {
				case "closure-whitespace":
					minifier = new WhitespaceClosureCompilerMinifier();
					break;
				
				case "closure-simple":
					minifier = new SimpleClosureCompilerMinifier();
					break;
				
				case "closure-advanced":
					minifier = new AdvancedClosureCompilerMinifier();
					break;
				
				default:
					minifier = new ConcatenatingMinifier();
					break;
			}
		}
		
		return minifier;
	}
}
