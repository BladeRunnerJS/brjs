package org.bladerunnerjs.plugin.utility;

import java.io.File;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.memoization.Getter;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

public class AssetLocator {
	private final Map<String, Asset> cachedAssets = new TreeMap<>();
	private final AssetLocation assetLocation;
	private final MemoizedValue<Assets> assetsList;
	
	public AssetLocator(AssetLocation assetLocation) {
		this.assetLocation = assetLocation;
		assetsList = new MemoizedValue<>(assetLocation.dir()+" - AssetLocation.assets", assetLocation.root(), assetLocation.root().dir());
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
									Asset createdAsset = assetPlugin.createAsset(assetFile, assetLocation);
									assetLocation.root().logger(this.getClass()).debug("creating new asset for the path '%s'", 
											createdAsset.getAssetPath());
									String assetPrimaryRequirePath = createdAsset.getPrimaryRequirePath();
									
									if (createdAsset instanceof SourceModule && createdAsset.assetLocation().assetContainer().isNamespaceEnforced()) {
										String relativePathFromAssetContainer = RelativePathUtility.get(assetLocation.root(), createdAsset.assetLocation().assetContainer().dir(), createdAsset.dir());
										String relativeRequirePathPathFromAssetContainer = StringUtils.substringAfter(relativePathFromAssetContainer, "/"); // strip of 'src/' at the start of the relative path
										String appRequirePrefix = assetLocation.assetContainer().app().getRequirePrefix();
										String createdAssetContainerRequirePrefix = createdAsset.assetLocation().assetContainer().requirePrefix();
										if (relativeRequirePathPathFromAssetContainer.startsWith(appRequirePrefix) && !relativeRequirePathPathFromAssetContainer.startsWith(createdAssetContainerRequirePrefix)) {
											InvalidRequirePathException wrappedRequirePathException = new InvalidRequirePathException(
													"The source module at '"+createdAsset.getAssetPath()+"' is in an invalid location. "+
													"It's require path starts with the apps require prefix ('"+appRequirePrefix+"') which suggests it's require path is intended to be '"+createdAssetContainerRequirePrefix+"'. "+
													"The require path defined by the source modules location is '"+relativeRequirePathPathFromAssetContainer+"'. Either it's package structure should be '"+createdAssetContainerRequirePrefix+"/*' or "+
													"remove the folders '"+relativeRequirePathPathFromAssetContainer+"' to allow the require prefix to be calculated automatically.");
											throw new RuntimeException(wrappedRequirePathException);
										}
									}
									
									
									if (assetPrimaryRequirePath != null) {
										assetLocation.root().logger(this.getClass()).debug("asset at '%s' is a '%s' and it's primary require path is '%s'", 
												createdAsset.getAssetPath(), createdAsset.getClass().getSimpleName(), assetPrimaryRequirePath);
									} else {
										assetLocation.root().logger(this.getClass()).debug("asset at '%s' is a '%s'", 
												createdAsset.getAssetPath(), createdAsset.getClass().getSimpleName());
									}
									cachedAssets.put(assetFilePath, createdAsset);
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
