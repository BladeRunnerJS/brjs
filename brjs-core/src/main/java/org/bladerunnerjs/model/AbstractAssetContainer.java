package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.plugin.AssetLocationPlugin;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	private final MemoizedValue<Map<String, LinkedAsset>> linkedAssetMap = new MemoizedValue<>("AssetContainer.sourceModulesMap", this);
	private final MemoizedValue<Map<String, AssetLocation>> assetLocationsMap = new MemoizedValue<>("AssetContainer.assetLocationsMap", this);
	private final Map<String, AssetLocation> cachedAssetLocations = new TreeMap<>();
	
	public AbstractAssetContainer(RootNode rootNode, Node parent, File dir) {
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
	
	@Override
	public RootAssetLocation rootAssetLocation() {
		AssetLocation assetLocation = assetLocation(".");
		return ((assetLocation != null) && (assetLocation instanceof RootAssetLocation)) ? (RootAssetLocation) assetLocation : null;
	}
	
	@Override
	public List<String> getAssetLocationPaths()
	{
		List<String> assetLocationPaths = new ArrayList<String>();
		assetLocationPaths.addAll( assetLocationsMap().keySet() );
		return assetLocationPaths;
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
				
				for(AssetLocationPlugin assetLocationPlugin : root().plugins().assetLocationPlugins()) {
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
	
	private void createAssetLocation(String locationPath, Map<String, AssetLocation> assetLocations, AssetLocationPlugin assetLocationPlugin ) {
		
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
}
