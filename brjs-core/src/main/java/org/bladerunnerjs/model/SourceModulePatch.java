package org.bladerunnerjs.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.TreeMap;
import java.util.Map;

import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

public class SourceModulePatch
{
	public static final String PATCH_APPLIED_MESSAGE = "Patch found for %s, applying patch from %s.";
	public static final String NO_PATCH_APPLIED_MESSAGE = "No patch found for %s, there was no patch file at %s so no patch will be applied.";
	
	private static Map<String, SourceModulePatch> patchesCache = new TreeMap<String, SourceModulePatch>();
	
	private File patchFile;
	private BRJS brjs;
	private AssetLocation assetLocation;
	private String requirePath;
	
	//TODO: this only supports patching files with a .js extension
	private SourceModulePatch(AssetLocation assetLocation, String requirePath)
	{
		brjs = assetLocation.root();
		this.assetLocation = assetLocation;
		this.requirePath = requirePath;
		
		String patchPath = requirePath.replace(".", "/") + ".js";
		patchFile = new File(brjs.jsPatches().dir(), patchPath);
	}

	public File getPatchFile()
	{
		return patchFile;
	}
	
	public Reader getReader()
	{
		Reader reader;
		
		if ( (!patchFile.exists()) || !(assetLocation.assetContainer() instanceof JsLib) )
		{
			return null;
		}
		else
		{
    		if (patchFile.isFile())
    		{
    			brjs.logger(SourceModulePatch.class).debug(PATCH_APPLIED_MESSAGE, requirePath, RelativePathUtility.get(brjs.dir(), patchFile, assetLocation.root()));
    			try
    			{
    				reader = new BufferedReader(new UnicodeReader(patchFile, brjs.bladerunnerConf().getDefaultFileCharacterEncoding()));
    			}
    			catch (IOException | ConfigException e)
    			{
    				throw new RuntimeException(e);
    			}			
    		}
    		else
    		{
				brjs.logger(SourceModulePatch.class).debug(NO_PATCH_APPLIED_MESSAGE, requirePath, RelativePathUtility.get(brjs.dir(), patchFile,assetLocation.root()));
				reader = new StringReader("");
    		}
		}
		
		return reader;
	}
	
	
	/*----- static methods for getting patches so we can caches them -----*/
	
	public static SourceModulePatch getPatchForRequirePath(AssetLocation assetLocation, String requirePath)
	{
		String key = getPatchesCacheKey(assetLocation, requirePath);
		if (patchesCache.containsKey(key))
		{
			return patchesCache.get(key);
		}
		return new SourceModulePatch(assetLocation, requirePath);
	}
	
	
	private static String getPatchesCacheKey(AssetLocation assetLocation, String requirePath)
	{
		return assetLocation.toString() + "-" + requirePath;
	}
	
}
