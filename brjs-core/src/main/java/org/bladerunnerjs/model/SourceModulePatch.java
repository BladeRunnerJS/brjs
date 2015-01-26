package org.bladerunnerjs.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.utility.UnicodeReader;

public class SourceModulePatch
{
	public static final String PATCH_APPLIED_MESSAGE = "Patch found for %s, applying patch from %s.";
	public static final String NO_PATCH_APPLIED_MESSAGE = "No patch found for %s, there was no patch file at %s so no patch will be applied.";
	
	private MemoizedFile patchFile;
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
		patchFile = brjs.jsPatches().file(patchPath);
	}

	public MemoizedFile getPatchFile()
	{
		return patchFile;
	}
	
	public boolean patchAvailable() {
		return patchFile.isFile() && assetLocation.assetContainer() instanceof JsLib;
	}
	
	public Reader getReader()
	{
		Reader reader;
		
		if ( !(assetLocation.assetContainer() instanceof JsLib) )
		{
			reader = new StringReader("");
		}
		else
		{
    		if (patchFile.isFile())
    		{
    			brjs.logger(SourceModulePatch.class).debug(PATCH_APPLIED_MESSAGE, requirePath, brjs.dir().getRelativePath(patchFile));
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
				brjs.logger(SourceModulePatch.class).debug(NO_PATCH_APPLIED_MESSAGE, requirePath, brjs.dir().getRelativePath(patchFile));
				reader = new StringReader("");
    		}
		}
		
		return reader;
	}
	
	public static SourceModulePatch getPatchForRequirePath(AssetLocation assetLocation, String requirePath) {
		return new SourceModulePatch(assetLocation, requirePath);
	}
}
