package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.model.AssetFileInstantationException;

public class Assets {
	
	private MemoizedValue<ComputedValue> computedValue;
	private AssetLocation assetLocation;
	private BRJS brjs;
	private Map<String,Asset> cachedAssets = new HashMap<>();
	
	public Assets(AssetLocation assetLocation) {
		computedValue = new MemoizedValue<>(assetLocation.dir()+" - AssetLocation.assets", assetLocation.root(), assetLocation.root().dir());
		this.assetLocation = assetLocation;
		this.brjs = assetLocation.root();
	}
	
	public Map<AssetPlugin, List<Asset>> pluginAssets() {
		return getComputedValue().pluginAssets;
	}

	public List<LinkedAsset> linkedAssets() {
		return getComputedValue().linkedAssets;
	}
	
	public List<SourceModule> sourceModules() {
		return getComputedValue().sourceModules;
	}
	
	
	private ComputedValue getComputedValue() {
		return computedValue.value(new Getter<RuntimeException>() {			
			@Override
			public Object get() {
				
				ComputedValue computedValue = new ComputedValue();
				
				for(AssetPlugin assetPlugin : brjs.plugins().assetPlugins()) {
					computedValue.pluginAssets.put(assetPlugin, new ArrayList<>());
				}
				
				for(MemoizedFile assetFile : assetLocation.getCandidateFiles()) {
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
										String relativePathFromAssetContainer = createdAsset.assetLocation().assetContainer().dir().getRelativePath(createdAsset.dir());
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
								computedValue.sourceModules.add((SourceModule) asset);
							}
							else if(asset instanceof LinkedAsset) {
								computedValue.linkedAssets.add((LinkedAsset) asset);
								computedValue.pluginAssets.get(assetPlugin).add(asset);
							}
							else {
								computedValue.pluginAssets.get(assetPlugin).add(asset);
							}
							
							break;
						}
					}
				}
				
				return computedValue;
			}
		});
		
	}
	
	
	class ComputedValue {
		Map<AssetPlugin, List<Asset>> pluginAssets = new HashMap<>();
		List<LinkedAsset> linkedAssets = new ArrayList<>();
		List<SourceModule> sourceModules = new ArrayList<>();
	}
	
	
}