package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.plugin.bundler.BundlerPlugin;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	private final NodeItem<SourceAssetLocation> src = new NodeItem<>(SourceAssetLocation.class, "src");
	private final NodeItem<DeepAssetLocation> resources = new NodeItem<>(DeepAssetLocation.class, "resources");
	
	public AbstractAssetContainer(RootNode rootNode, File dir) {
		init(rootNode, rootNode, dir);
	}
	
	public SourceAssetLocation src() {
		return item(src);
	}
	
	public AssetLocation resources()
	{
		return item(resources);
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
		return "/" + namespace().replaceAll("\\.", "/");
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		List<SourceModule> sourceModules = new ArrayList<SourceModule>();
			
		for(BundlerPlugin bundlerPlugin : (root()).plugins().bundlers()) {
			for (AssetLocation assetLocation : getAllAssetLocations())
			{
				sourceModules.addAll(bundlerPlugin.getSourceModules(assetLocation));
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
	public List<AssetLocation> getAllAssetLocations() {
		List<AssetLocation> assetLocations = new ArrayList<>();
		
		assetLocations.add(resources());
		assetLocations.add(src());
		assetLocations.addAll(src().getChildAssetLocations());
		
		return assetLocations;
	}
}
