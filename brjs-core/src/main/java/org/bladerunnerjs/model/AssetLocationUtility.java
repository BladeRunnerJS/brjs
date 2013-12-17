package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class AssetLocationUtility
{
	private final Map<String, Asset> assetFiles = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	<A extends Asset> A createAssetFile(Class<? extends A> assetFileClass, AssetLocation assetLocation, File assetFile) throws AssetFileInstantationException {
		String absolutePath = assetFile.getAbsolutePath();
		A asset;
		
		if(assetFiles.containsKey(absolutePath)) {
			asset = (A) assetFiles.get(absolutePath);
		}
		else {
			asset = createAssetInstance(assetFileClass, assetLocation, assetFile);
			assetFiles.put(absolutePath, asset);
		}
		
		return asset;
	}
	
	@SuppressWarnings("unchecked")
	<A extends Asset> List<A> createAssetFiles(Class<? extends Asset> assetFileClass, AssetLocation assetLocation, Collection<File> assetFiles) throws AssetFileInstantationException
	{
		List<A> assets = new LinkedList<A>();		
				
		for (File file : assetFiles)
		{
			assets.add( (A) createAssetFile(assetFileClass, assetLocation, file) );
		}
		
		return assets;
	}
	
	<A extends Asset> List<A> createAssetFilesWithExtension(Class<? extends Asset> assetFileClass, AssetLocation assetLocation, String... extensions) throws AssetFileInstantationException
	{
		return createAssetFiles( assetFileClass, assetLocation, filterFilesList(assetLocation.getFiles(), new SuffixFileFilter(extensions)) );
	}
	
	<A extends Asset> List<A> createAssetFilesWithName(Class<? extends Asset> assetFileClass, AssetLocation assetLocation, String... fileNames) throws AssetFileInstantationException
	{
		return createAssetFiles( assetFileClass, assetLocation, filterFilesList(assetLocation.getFiles(), new NameFileFilter(fileNames)) );
	}
	
	
	
	@SuppressWarnings("unchecked")
	private <A extends Asset> A createAssetInstance(Class<? extends Asset> assetFileClass, AssetLocation assetLocation, File file) throws AssetFileInstantationException
	{
		if (assetLocation instanceof CachingAssetLocationWrapper)
		{
			assetLocation = ((CachingAssetLocationWrapper) assetLocation).getWrappedAssetLocation();
		}
		
		try
		{
			Constructor<? extends Asset> ctor = assetFileClass.getConstructor();
			A assetFile = (A) ctor.newInstance();
			assetFile.initialize(assetLocation, file);
			
			return assetFile;
		}
		catch(AssetFileInstantationException ex) {
			throw ex;
		}
		catch (SecurityException ex)
		{
			throw new RuntimeException(ex);
		}
		catch (Exception ex)
		{
			throw new AssetFileInstantationException(ex, assetFileClass);
		}		
	}
	
	private List<File> filterFilesList(List<File> files, FileFilter filter)
	{
		List<File> filteredFiles = new ArrayList<File>();
		for (File f : files)
		{
			if (filter.accept(f))
			{
				filteredFiles.add(f);
			}
		}
		return filteredFiles;
	}
	
}
