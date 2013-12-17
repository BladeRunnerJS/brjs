package org.bladerunnerjs.model;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.UnhandledAssetContainerException;
import org.bladerunnerjs.plugin.AssetPlugin;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	public AbstractAssetContainer(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public App getApp() {
		Node node = this.parentNode();
		
		while(!(node instanceof App)) {
			node = node.parentNode();
		}
		
		return (App) node;
	}
	
	@Override
	public String requirePrefix() {
		return namespace().replaceAll("\\.", "/");
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		List<SourceModule> sourceModules = new ArrayList<SourceModule>();
		
		List<AssetPlugin> assetProducers = (root()).plugins().assetProducers();
		
		for (AssetLocation assetLocation : assetLocations())
		{
			CachedAssetLocation cachedAssetLocation = new CachedAssetLocation(assetLocation);
			for(AssetPlugin assetPlugin : assetProducers)
			{
				sourceModules.addAll(assetPlugin.getSourceModules(assetLocation));
			}
		}
		
		return sourceModules;
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
		String normalizedLocationPath = normalizePath(locationPath);
		AssetLocation assetLocation = null;
		URI assetContainerUri = dir().toURI();
		
		for(AssetLocation nextAssetLocation : assetLocations()) {
			String nextLocationPath = normalizePath(assetContainerUri.relativize(nextAssetLocation.dir().toURI()).getPath());
			
			if(nextLocationPath.equals(normalizedLocationPath)) {
				assetLocation = nextAssetLocation;
				break;
			}
		}
		
		return assetLocation;
	}
	
	@Override
	public List<AssetLocation> assetLocations() {
		
		for(AssetPlugin assetPlugin : root().plugins().assetProducers()) {
			try 
			{
				return new ArrayList<AssetLocation>( assetPlugin.getAssetLocations(this) );
			}
			catch (UnhandledAssetContainerException ex)
			{
				continue;
			}
		}
		
		return null;
	}
	
	private String normalizePath(String path) {
		return path.replaceAll("/$", "");
	}
}
