package org.bladerunnerjs.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.bladerunnerjs.utility.UnicodeReader;

public class SourceModulePatch
{

	public static final String PATCH_APPLIED_MESSAGE = "Patch found for %s, applying patch from %s.";
	public static final String NO_PATCH_APPLIED_MESSAGE = "No patch found for %s, there was no patch file at %s so no patch will be applied.";
	
	private static Map<String, SourceModulePatch> patchesCache = new HashMap<String, SourceModulePatch>();
	
	
	private File patchFile;


	private Reader patchFileReader;
	
	//TODO: this only supports patching files with a .js extension
	public SourceModulePatch(AssetLocation assetLocation, String requirePath)
	{
		BRJS brjs = assetLocation.root();
		
		String patchPath = requirePath.replace(".", "/") + ".js";
		patchFile = new File(brjs.jsPatches().dir(), patchPath);
		
		if ( !(assetLocation.getAssetContainer() instanceof JsLib) )
		{
			patchFileReader = new StringReader("");
		}
		else
		{
    		if (patchFile.isFile())
    		{
    			brjs.logger(LoggerType.CORE, SourceModulePatch.class).debug(PATCH_APPLIED_MESSAGE, requirePath, RelativePathUtility.get(brjs.dir(), patchFile));
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
				brjs.logger(LoggerType.CORE, SourceModulePatch.class).debug(NO_PATCH_APPLIED_MESSAGE, requirePath, RelativePathUtility.get(brjs.dir(), patchFile));
    			patchFileReader = new StringReader("");
    		}
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
