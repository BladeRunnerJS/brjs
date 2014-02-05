package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class AssetLocationUtility
{
	private final Map<String, Asset> assetFiles = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public <A extends Asset> A obtainAsset(Class<? extends A> assetFileClass, AssetLocation assetLocation, File assetFile) throws AssetFileInstantationException {
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
	public <A extends Asset> List<A> obtainMatchingAssets(Class<? extends Asset> assetFileClass, AssetLocation assetLocation, Collection<File> assetFiles) throws AssetFileInstantationException
	{
		List<A> assets = new LinkedList<A>();		
		
		for (File file : assetFiles)
		{
			assets.add( (A) obtainAsset(assetFileClass, assetLocation, file) );
		}
		
		return assets;
	}
	
	@SuppressWarnings("unchecked")
	private <A extends Asset> A createAssetInstance(Class<? extends Asset> assetFileClass, AssetLocation assetLocation, File file) throws AssetFileInstantationException
	{
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
}
