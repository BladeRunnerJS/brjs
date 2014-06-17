package org.bladerunnerjs.plugin.utility;

import java.io.File;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.AssetPlugin;

public class AssetLocator {
	private final Map<String, Asset> cachedAssets = new TreeMap<>();
	private final AssetLocation assetLocation;
	private final MemoizedValue<Assets> assetsList;
	
	public AssetLocator(AssetLocation assetLocation) {
		this.assetLocation = assetLocation;
		assetsList = new MemoizedValue<>("AssetLocation.assets", assetLocation.root(), assetLocation.root().dir());
	}
	
	public Assets assets(List<File> assetFiles) {
		return assetsList.value(new Getter<RuntimeException>() {
			@Override
			public Object get() {
				Assets assets = new Assets(assetLocation.root());
				
				for(File assetFile : assetFiles) {
					for(AssetPlugin assetPlugin : assetLocation.root().plugins().assetPlugins()) {
						if(assetPlugin.canHandleAsset(assetFile, assetLocation)) {
							String assetFilePath = assetFile.getAbsolutePath();
							
							if(!cachedAssets.containsKey(assetFilePath)) {
								try {
									cachedAssets.put(assetFilePath, assetPlugin.createAsset(assetFile, assetLocation));
								}
								catch (AssetFileInstantationException e) {
									throw new RuntimeException(e);
								}
							}
							
							Asset asset = cachedAssets.get(assetFilePath);
							if(asset instanceof SourceModule) {
								assets.sourceModules.add((SourceModule) asset);
							}
							else if(asset instanceof LinkedAsset) {
								assets.linkedAssets.add((LinkedAsset) asset);
								assets.pluginAssets.get(assetPlugin).add(asset);
							}
							else {
								assets.pluginAssets.get(assetPlugin).add(asset);
							}
							
							break;
						}
					}
				}
				
				return assets;
			}
		});
	}
}
