package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	private AssetLocationPlugin previousAssetLocationPlugin;
	private Map<String, AssetLocation> assetLocationCache;
	
	private final MemoizedValue<List<SourceModule>> sourceModulesList = new MemoizedValue<>("AssetContainer.sourceModules", root(), root().dir());
	private final MemoizedValue<Map<String,AssetLocation>> assetLocationsMap = new MemoizedValue<>("AssetContainer.assetLocations", root(), root().dir());
	
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
	public String namespace() {
		return requirePrefix().replace("/", ".");
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		return sourceModulesList.value(() -> {
			List<SourceModule> sourceModules = new ArrayList<SourceModule>();
			
			for(AssetPlugin assetPlugin : (root()).plugins().assetProducers()) {
				for (AssetLocation assetLocation : assetLocations())
				{
					sourceModules.addAll(assetPlugin.getSourceModules(assetLocation));
				}
			}
			
			return sourceModules;
		});
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) {
		for(SourceModule sourceModule : sourceModules()) {
			if(sourceModule.getRequirePath().equals(requirePath)) {
				return sourceModule;
			}
		}
		
		return null;
	}
	
	@Override
	public AssetLocation assetLocation(String locationPath) {
		locationPath = locationPath.endsWith("/") ? StringUtils.substringBeforeLast(locationPath, "/") : locationPath;
		return namedAssetLocations().get(locationPath);
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		return new ArrayList<AssetLocation>( namedAssetLocations().values() );
	}
	
	@Override
	public Map<String,AssetLocation> namedAssetLocations() {
		return assetLocationsMap.value(() -> {
			Map<String,AssetLocation> assetLocations = new LinkedHashMap<>();
			
			for(AssetLocationPlugin assetLocationPlugin : root().plugins().assetLocationProducers()) {
				if(assetLocationPlugin.canHandleAssetContainer(this)) {
					if(assetLocationPlugin != previousAssetLocationPlugin) {
						previousAssetLocationPlugin = assetLocationPlugin;
						assetLocationCache = new HashMap<>();
					}
					
					
					for (AssetLocation assetLocation : assetLocationPlugin.getAssetLocations(this, assetLocationCache))
					{
						String locationPath = normalizePath(RelativePathUtility.get(dir(), assetLocation.dir()));
						locationPath = locationPath.endsWith("/") ? StringUtils.substringBeforeLast(locationPath, "/") : locationPath;
						assetLocations.put( locationPath, assetLocation );
					}
					break;
				}
			}
			
			return assetLocations;
		});
	}
	
	private String normalizePath(String path) {
		return path.replaceAll("/$", "");
	}
}
