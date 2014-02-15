package org.bladerunnerjs.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.UnicodeReader;

public class SourceModulePatch
{

	private static Map<String, SourceModulePatch> patchesCache = new HashMap<String, SourceModulePatch>();
	
	
	private File patchFile;


	private Reader patchFileReader;
	
	//TODO: this only supports patching JS files
	public SourceModulePatch(BRJS brjs, String requirePath)
	{
		String patchPath = requirePath.replace(".", "/") + ".js";
		patchFile = new File(brjs.jsPatches().dir(), patchPath);
		
		if (patchFile.isFile())
		{
			try
			{
				patchFileReader = new BufferedReader(new UnicodeReader(patchFile, brjs.bladerunnerConf().getDefaultInputEncoding()));
			}
			catch (IOException | ConfigException e)
			{
				throw new RuntimeException(e);
			}			
		}
		else
		{
			patchFileReader = new StringReader("");
		}
	}

	public File getPatchFile()
	{
		return patchFile;
	}
	
	public Reader getReader()
	{
		return patchFileReader;
	}
	
	
	
	public static SourceModulePatch getPatchForRequirePath(BRJS brjs, String requirePath)
	{
		String key = getPatchesCacheKey(brjs, requirePath);
		if (patchesCache.containsKey(key))
		{
			return patchesCache.get(key);
		}
		return new SourceModulePatch(brjs, requirePath);
	}
	
	
	private static String getPatchesCacheKey(BRJS brjs, String requirePath)
	{
		return brjs.toString() + "-" + requirePath;
	}
	
}
