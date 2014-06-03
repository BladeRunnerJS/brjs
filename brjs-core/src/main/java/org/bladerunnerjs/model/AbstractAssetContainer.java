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
	private final MemoizedValue<Map<String, SourceModule>> sourceModulesMap = new MemoizedValue<>("AssetContainer.sourceModulesMap", this);
	private final MemoizedValue<Map<String, AssetLocation>> assetLocationsMap = new MemoizedValue<>("AssetContainer.assetLocationsMap", this);
	private final Map<String, AssetLocation> cachedAssetLocations = new TreeMap<>();
	
	public AbstractAssetContainer(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public App app() {
		Node node = this.parentNode();
		
		while(!(node instanceof App)) {
			node = node.parentNode();
		}
		
		return (App) node;
	}
	
	@Override
	public Set<SourceModule> sourceModules() {
		return new LinkedHashSet<SourceModule>(sourceModulesMap().values());
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) {
		return sourceModulesMap().get(requirePath);
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
	
	private Map<String, SourceModule> sourceModulesMap() {
		return sourceModulesMap.value(() -> {
			Map<String, SourceModule> sourceModulesMap = new LinkedHashMap<>();
			
			for (AssetLocation assetLocation : assetLocations())
			{
				for(SourceModule sourceModule : assetLocation.sourceModules()) {
					sourceModulesMap.put(sourceModule.getRequirePath(), sourceModule);
				}
			}
			
			return sourceModulesMap;
		});
	}
	
	private Map<String, AssetLocation> assetLocationsMap() {
			return assetLocationsMap.value(() -> {
				Map<String, AssetLocation> assetLocations = new LinkedHashMap<>();
				
				for(AssetLocationPlugin assetLocationPlugin : root().plugins().assetLocationProducers()) {
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
	
	private void createAssetLocation(String locationPath, Map<String, AssetLocation> assetLocations, AssetLocationPlugin assetLocationPlugin ){
		
		if(!assetLocations.containsKey(locationPath)) {
			if(!cachedAssetLocations.containsKey(locationPath)) {
				AssetLocation assetLocation = assetLocationPlugin.createAssetLocation(this, locationPath, cachedAssetLocations);
				
//TODO: have a proper solution to know when duplicate node names are valid				
				try {
					rootNode.registerNode(assetLocation, false);
				} catch (NodeAlreadyRegisteredException e) {
					try {
						rootNode.registerNode(assetLocation, true);
					} catch (NodeAlreadyRegisteredException e1) {
						throw new RuntimeException(e);
					}
					
				}
				cachedAssetLocations.put(locationPath, assetLocation);
			}
			
			assetLocations.put(locationPath, cachedAssetLocations.get(locationPath));
		}
	}
}
