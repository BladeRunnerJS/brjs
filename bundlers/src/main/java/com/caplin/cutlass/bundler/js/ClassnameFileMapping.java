package com.caplin.cutlass.bundler.js;

import java.io.File;

public class ClassnameFileMapping
{
	private String classname;
	private File file;

	public ClassnameFileMapping(String classname, File file)
	{
		this.classname = classname;
		this.file = file;
	}

	public String getClassname()
	{
		return classname;
	}

	public File getFile()
	{
		return file;
	}

	public String toString()
	{
		return classname + " -> " + file.getPath();
	}

}
