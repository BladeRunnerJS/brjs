package org.bladerunnerjs.model;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


public class AssetLocationUtility
{
	private final Map<String, Asset> assetFiles = new HashMap<>();
	private final AssetLocation assetLocation;
	
	public AssetLocationUtility(AssetLocation assetLocation) {
		this.assetLocation = assetLocation;
	}
	
	@SuppressWarnings("unchecked")
	public <A extends Asset> A obtainAsset(Class<? extends A> assetFileClass, File assetFile) throws AssetFileInstantationException {
		String absolutePath = assetFile.getAbsolutePath();
		A asset;
		
		if(assetFiles.containsKey(absolutePath)) {
			asset = (A) assetFiles.get(absolutePath);
		}
		else {
			asset = createAssetInstance(assetFileClass, assetFile);
			assetFiles.put(absolutePath, asset);
		}
		
		return asset;
	}
	
	@SuppressWarnings("unchecked")
	private <A extends Asset> A createAssetInstance(Class<? extends Asset> assetFileClass, File file) throws AssetFileInstantationException
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
