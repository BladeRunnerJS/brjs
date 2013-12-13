package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.logging.LoggerType;


public class AssetLocationUtility
{
	
	private final Map<String, Asset> assetFiles = new HashMap<>();
	
	<AF extends Asset> List<AF> getAssetFilesNamed(AssetLocation assetLocation, Class<? extends Asset> assetFileType, List<File> files, String... fileNames)
	{
		File dir = assetLocation.dir();
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetLocation, assetFileType, files, new NameFileFilter(fileNames));
	}
	
	<AF extends Asset> List<AF> getAssetFilesWithExtension(AssetLocation assetLocation, Class<? extends Asset> assetFileType, List<File> files, String... extensions)
	{
		File dir = assetLocation.dir();
		if (!dir.isDirectory()) { return Arrays.asList(); }
		
		return createAssetFileListFromFiles( assetLocation, assetFileType, files, new SuffixFileFilter(extensions) );
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private <AF extends Asset> List<AF> createAssetFileListFromFiles(AssetLocation assetLocation, Class<? extends Asset> assetFileType, List<File> files, FileFilter fileFilter)
	{
		List<AF> assetFiles = new LinkedList<AF>();		
	
		List<File> filteredFiles = new ArrayList<File>();
		for (File f : files)
		{
			if (fileFilter.accept(f.getAbsoluteFile()))
			{
				filteredFiles.add(f);
			}
		}
		
		for (File file : filteredFiles)
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
	
	@SuppressWarnings("unchecked")
	public <AF extends Asset> AF getAssetFile(Class<? extends AF> assetFileType, AssetLocation assetLocation, File file) throws UnableToInstantiateAssetFileException {
		String absolutePath = file.getAbsolutePath();
		AF assetFile;
		
		if(assetFiles.containsKey(absolutePath)) {
			assetFile = (AF) assetFiles.get(absolutePath);
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
