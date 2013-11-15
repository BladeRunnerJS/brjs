package org.bladerunnerjs.model;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.core.log.LoggerType;


public class AssetLocationUtility
{
	
	public static <AF extends AssetFile> List<AF> getFilesNamed(AssetContainer assetContainer, File dir, Class<? extends AssetFile> assetFileType, String... fileNames)
	{
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetContainer, assetFileType, FileUtils.listFiles(dir, new NameFileFilter(fileNames), TrueFileFilter.INSTANCE) );
	}
	
	public static <AF extends AssetFile> List<AF> getFilesWithExtension(AssetContainer assetContainer, File dir, Class<? extends AssetFile> assetFileType, String... extensions)
	{
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetContainer, assetFileType, FileUtils.listFiles(dir, new SuffixFileFilter(extensions), TrueFileFilter.INSTANCE) );
	}
	
	@SuppressWarnings("unchecked")
	private static <AF extends AssetFile> List<AF> createAssetFileListFromFiles(AssetContainer assetContainer, Class<? extends AssetFile> assetFileType, Collection<File> files)
	{
		List<AF> assetFiles = new LinkedList<AF>();		
		
		for (File file : files)
		{
			try
			{
				assetFiles.add( (AF) assetContainer.root().getAssetFile(assetFileType, assetContainer, file) );
			}
			catch (UnableToInstantiateAssetFileException e)
			{
				assetContainer.root().logger(LoggerType.UTIL, AssetLocationUtility.class).error(e.getMessage());
			}
		}
		
		return assetFiles;
	}
}
