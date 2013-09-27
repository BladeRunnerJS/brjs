package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.model.path.AppPath;
import com.caplin.cutlass.structure.model.path.RootPath;

public class ThirdPartyLibraryFinder
{	
	public Map<String, File> getThirdPartyLibraryDirectories(File baseDir)
	{
		Map<String, File> librariesToLibDirectoriesMap = new TreeMap<String, File>();
		addThirdPartyLibraryDirectories(new File(CutlassDirectoryLocator.getParentApp(baseDir), CutlassConfig.THIRDPARTY_DIR_IN_APP), librariesToLibDirectoriesMap);
		addThirdPartyLibraryDirectories(CutlassDirectoryLocator.getSDkThirdpartySrcDir(baseDir), librariesToLibDirectoriesMap);
		return librariesToLibDirectoriesMap;
	}
	
	public File getThirdPartyLibraryDirectory(File baseDir, String libraryName) throws BundlerProcessingException
	{
		File libraryDirInApp = AppPath.locateAncestorPath(baseDir).thirdpartyLibsPath().libPath(libraryName).getDir();
		File libraryDirInSdk = RootPath.locateAncestorPath(baseDir).sdkPath().libsPath().javascriptLibsPath().thirdpartyLibsPath().libPath(libraryName).getDir();
		
		if(libraryDirInApp.exists())
		{
			return libraryDirInApp;
		}
		else if(libraryDirInSdk.exists())
		{
			return libraryDirInSdk;
		}
		else
		{
			throw new BundlerProcessingException("Couldn't find library " + libraryName);
		}
	}
	
	private void addThirdPartyLibraryDirectories(File thirdPartyLibrariesDirectory, Map<String, File> librariesToLibDirectoriesMap)
	{
		if(thirdPartyLibrariesDirectory == null || !thirdPartyLibrariesDirectory.exists())
		{
			return;
		}
		
		for(File file : thirdPartyLibrariesDirectory.listFiles())
		{
			if(file.isDirectory() && !file.isHidden() && !librariesToLibDirectoriesMap.containsKey(file.getName()))
			{
				librariesToLibDirectoriesMap.put(file.getName(), file);
			}
		}
	}
}
