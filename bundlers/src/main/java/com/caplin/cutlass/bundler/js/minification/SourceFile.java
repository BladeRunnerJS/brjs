package com.caplin.cutlass.bundler.js.minification;

public class SourceFile
{
	private final String fileName;
	private final String sourceCode;
	
	public SourceFile(String fileName, String sourceCode)
	{
		this.fileName = fileName;
		this.sourceCode = sourceCode;
	}
	
	public String getName()
	{
		return fileName;
	}
	
	public String getCode()
	{
		return sourceCode;
	}
}
