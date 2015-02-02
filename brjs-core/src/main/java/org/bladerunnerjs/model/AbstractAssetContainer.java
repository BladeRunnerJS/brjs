package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.api.plugin.LegacyAssetLocationPlugin;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	private final MemoizedValue<Map<String, LinkedAsset>> linkedAssetMap = new MemoizedValue<>("AssetContainer.sourceModulesMap", this);
	private final MemoizedValue<Map<String, AssetLocation>> assetLocationsMap = new MemoizedValue<>("AssetContainer.assetLocationsMap", this);
	private final Map<String, AssetLocation> cachedAssetLocations = new TreeMap<>();
	
	public AbstractAssetContainer(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public App app() {
		Node node = this.parentNode();
		
		while(!(node instanceof App) && node != null) {
			node = node.parentNode();
		}
		
		if (node == null) {
			AssetContainer assetContainer = root().locateAncestorNodeOfClass(dir().getParentFile(), AssetContainer.class);
			if (assetContainer != null) {
				return assetContainer.app();				
			}
		}
		
		return (App) node;
	}
	
	@Override
	public Set<LinkedAsset> linkedAssets() {
		return new LinkedHashSet<LinkedAsset>(linkedAssetsMap().values());
	}
	
	@Override
	public LinkedAsset linkedAsset(String requirePath) {
		return linkedAssetsMap().get(requirePath);
	}
	
	@Override
	public AssetLocation assetLocation(String locationPath) {
		return assetLocationsMap().get(locationPath);
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return new ArrayList<>(assetLocationsMap().values());
	}
	
	private Map<String, LinkedAsset> linkedAssetsMap() {
		return linkedAssetMap.value(() -> {
			Map<String, LinkedAsset> linkedAssetsMap = new LinkedHashMap<>();
			
			for (AssetLocation assetLocation : assetLocations())
			{
				for(SourceModule sourceModule : assetLocation.sourceModules()) {
					linkedAssetsMap.put(sourceModule.getPrimaryRequirePath(), sourceModule);
				}
				for(LinkedAsset asset : assetLocation.linkedAssets()) {
					linkedAssetsMap.put(asset.getPrimaryRequirePath(), asset);
				}
			}
			
			return linkedAssetsMap;
		});
	}
	
	private Map<String, AssetLocation> assetLocationsMap() {
		return assetLocationsMap.value(() -> {
			Map<String, AssetLocation> assetLocations = new LinkedHashMap<>();
			
			for(LegacyAssetLocationPlugin assetLocationPlugin : root().plugins().legacyAssetLocationPlugins()) {
				List<String> assetLocationDirectories = assetLocationPlugin.getAssetLocationDirectories(this);
				
				if(assetLocationDirectories.size() > 0) {
					for(String locationPath : assetLocationDirectories) {
						createAssetLocation(locationPath, assetLocations, assetLocationPlugin);
					}
					
					if(!assetLocationPlugin.allowFurtherProcessing()) {
						break;
					}
				}
			}
			
			return assetLocations;
		});
	}
	
	private void createAssetLocation(String locationPath, Map<String, AssetLocation> assetLocations, LegacyAssetLocationPlugin assetLocationPlugin ) {
		
		if (!assetLocations.containsKey(locationPath)) {
			AssetLocation newAssetLocation;
			if (!cachedAssetLocations.containsKey(locationPath)) {
				newAssetLocation = assetLocationPlugin.createAssetLocation(this, locationPath, cachedAssetLocations);
				initAndCacheAssetLocation(locationPath, newAssetLocation);
			} else {
				AssetLocation oldAssetLocation = cachedAssetLocations.get(locationPath);
				newAssetLocation = assetLocationPlugin.createAssetLocation(this, locationPath, cachedAssetLocations);
				if (newAssetLocation.getClass() != oldAssetLocation.getClass()) {
					rootNode.clearRegisteredNode(oldAssetLocation);
					initAndCacheAssetLocation(locationPath, newAssetLocation);
				} else {
					newAssetLocation = oldAssetLocation;
				}
			}
			
			assetLocations.put(locationPath, newAssetLocation);
		}
	}
	
	private void initAndCacheAssetLocation(String locationPath, AssetLocation assetLocation)
	{
		try
		{
			rootNode.registerNode(assetLocation);
		}
		catch (NodeAlreadyRegisteredException ex)
		{
			throw new RuntimeException(ex);
		}	
		cachedAssetLocations.put(locationPath, assetLocation);
	}
	
	
	
	
	public AliasDefinitionsFile aliasDefinitionsFile(String path) {
		return assetLocation(path).aliasDefinitionsFile();
	}
}
