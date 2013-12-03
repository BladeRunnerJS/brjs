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
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.core.log.LoggerType;


public class AssetLocationUtility
{
	
	private final Map<String, Asset> assetFiles = new HashMap<>();
	
	<AF extends Asset> List<AF> getAssetFilesNamed(AssetLocation assetLocation, Class<? extends Asset> assetFileType, String... fileNames)
	{
		File dir = assetLocation.dir();
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetLocation, assetFileType, FileUtils.listFiles(dir, new NameFileFilter(fileNames), FalseFileFilter.INSTANCE) );
	}
	
	<AF extends Asset> List<AF> getAssetFilesWithExtension(AssetLocation assetLocation, Class<? extends Asset> assetFileType, String... extensions)
	{
		File dir = assetLocation.dir();
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetLocation, assetFileType, FileUtils.listFiles(dir, new SuffixFileFilter(extensions), FalseFileFilter.INSTANCE) );
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private <AF extends Asset> List<AF> createAssetFileListFromFiles(AssetLocation assetLocation, Class<? extends Asset> assetFileType, Collection<File> files)
	{
		List<AF> assetFiles = new LinkedList<AF>();		
		
		for (File file : files)
		{
			try
			{
				assetFiles.add( (AF) getAssetFile(assetFileType, assetLocation, file) );
			}
			catch (UnableToInstantiateAssetFileException e)
			{
				assetLocation.getAssetContainer().root().logger(LoggerType.UTIL, AssetLocationUtility.class).error(e.getMessage());
			}
		}
		
		return assetFiles;
	}
	
	public <AF extends Asset> Asset getAssetFile(Class<? extends Asset> assetFileType, AssetLocation assetLocation, File file) throws UnableToInstantiateAssetFileException {
		String absolutePath = file.getAbsolutePath();
		Asset assetFile;
		
		if(assetFiles.containsKey(absolutePath)) {
			assetFile = assetFiles.get(absolutePath);
		}
		else {
			assetFile = createAssetFileObjectForFile(assetFileType, assetLocation, file);
			assetFiles.put(absolutePath, assetFile);
		}
		
		return assetFile;
	}
	
	@SuppressWarnings("unchecked")
	private <AF extends Asset> AF createAssetFileObjectForFile(Class<? extends Asset> assetFileType, AssetLocation assetLocation, File file) throws UnableToInstantiateAssetFileException
	{
		try
		{
			//TODO: discuss whether we *really* want to use a non-default constructor or an interface method gives a better dev experience
			//		if we use this constructor delete the setters on AssetFile interface
//			Constructor<? extends AssetFile> ctor = assetFileType.getConstructor(AssetContainer.class, File.class);
			Constructor<? extends Asset> ctor = assetFileType.getConstructor();
			
			AF assetFile = (AF) ctor.newInstance();
			
			assetFile.initializeUnderlyingObjects(assetLocation, file);
			
			return assetFile;
		}
		catch (SecurityException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (Exception ex)
		{
			throw new UnableToInstantiateAssetFileException(ex, assetFileType);
		}		
	}
	
	
}
