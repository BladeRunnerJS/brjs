package org.bladerunnerjs.plugin.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.plugin.AssetPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link AssetPlugin}.
 */
public abstract class AbstractAssetPlugin extends AbstractPlugin implements AssetPlugin {
	// TODO: the final solution will ideally be able to use the File itself as they key?
	private Map<String, Asset> cachedAssets = new HashMap<>();
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	protected List<Asset> _getAssets(AssetLocation assetLocation) {
		try {
			List<Asset> assets = new ArrayList<>();
			FileInfo dirInfo = brjs.getFileInfo(assetLocation.dir());
			
			if(dirInfo.exists()) {
				List<File> files = (assetLocation instanceof DeepAssetLocation) ? dirInfo.nestedFiles() : dirInfo.files();
				
				for(File assetFile : files) {
					if(canHandleAsset(assetFile, assetLocation)) {
						String assetFilePath = assetFile.getAbsolutePath();
						
						if(!cachedAssets.containsKey(assetFilePath)) {
							cachedAssets.put(assetFilePath, createAsset(assetFile, assetLocation));
						}
						
						assets.add(cachedAssets.get(assetFilePath));
					}
				}
			}
			
			return assets;
		}
		catch(AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
	}
}
