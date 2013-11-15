package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.core.log.LoggerType;


public class AssetLocationUtility
{
	
	private final Map<String, AssetFile> assetFiles = new HashMap<>();
	
	<AF extends AssetFile> List<AF> getAssetFilesNamed(AssetContainer assetContainer, File dir, Class<? extends AssetFile> assetFileType, String... fileNames)
	{
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetContainer, assetFileType, FileUtils.listFiles(dir, new NameFileFilter(fileNames), TrueFileFilter.INSTANCE) );
	}
	
	<AF extends AssetFile> List<AF> getAssetFilesWithExtension(AssetContainer assetContainer, File dir, Class<? extends AssetFile> assetFileType, String... extensions)
	{
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetContainer, assetFileType, FileUtils.listFiles(dir, new SuffixFileFilter(extensions), TrueFileFilter.INSTANCE) );
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private <AF extends AssetFile> List<AF> createAssetFileListFromFiles(AssetContainer assetContainer, Class<? extends AssetFile> assetFileType, Collection<File> files)
	{
		List<AF> assetFiles = new LinkedList<AF>();		
		
		for (File file : files)
		{
			try
			{
				assetFiles.add( (AF) getAssetFile(assetFileType, assetContainer, file) );
			}
			catch (UnableToInstantiateAssetFileException e)
			{
				assetContainer.root().logger(LoggerType.UTIL, AssetLocationUtility.class).error(e.getMessage());
			}
		}
		
		return assetFiles;
	}
	
	public <AF extends AssetFile> AssetFile getAssetFile(Class<? extends AssetFile> assetFileType, AssetContainer assetContainer, File file) throws UnableToInstantiateAssetFileException {
		String absolutePath = file.getAbsolutePath();
		AssetFile assetFile;
		
		if(assetFiles.containsKey(absolutePath)) {
			assetFile = assetFiles.get(absolutePath);
		}
		else {
			assetFile = createAssetFileObjectForFile(assetFileType, assetContainer, file);
			assetFiles.put(absolutePath, assetFile);
		}
		
		return assetFile;
	}
	
	@SuppressWarnings("unchecked")
	private <AF extends AssetFile> AF createAssetFileObjectForFile(Class<? extends AssetFile> assetFileType, AssetContainer assetContainer, File file) throws UnableToInstantiateAssetFileException
	{
		try
		{
			Constructor<? extends AssetFile> ctor = assetFileType.getConstructor(AssetContainer.class, File.class);
			return (AF) ctor.newInstance(assetContainer, file);
		}
		catch (SecurityException e)
		{
			throw new RuntimeException(e);
		}
		catch (Exception e)
		{
			throw new UnableToInstantiateAssetFileException(assetFileType, AssetContainer.class, File.class);
		}		
	}
	
	
}
