package org.bladerunnerjs.model;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.core.log.LoggerType;


public class AssetLocationUtility
{
	
	public static <AF extends AssetFile> List<AF> getFilesWithExtension(AssetContainer assetContainer, Class<? extends AssetFile> assetFileType, String... extensions)
	{
		File srcDir = assetContainer.file("src"); //TODO: use the model to get this, we probably should be looking at resources too. And it might not be something we even do here, we should just look at 'dir()'
		if (!srcDir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetContainer, assetFileType, FileUtils.listFiles(srcDir, new SuffixFileFilter(extensions), TrueFileFilter.INSTANCE) );
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
