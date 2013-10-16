package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClassDictionary
{

	private Map<String, File> classnameToFilelookup = new HashMap<String, File>();
	private Map<File, String> fileToClassnamelookup = new HashMap<File, String>();

	public void add(File file, String classname)
	{
		classnameToFilelookup.put(classname, file);
		fileToClassnamelookup.put(file, classname);
	}

	public File lookup(String classname)
	{
		return classnameToFilelookup.get(classname);
	}

	public String lookup(File file)
	{
		return fileToClassnamelookup.get(file);
	}
	
	public boolean contains(String classname)
	{
		return classnameToFilelookup.containsKey(classname);
	}
}
