package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

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
	public List<SourceFile> sourceFiles() {
		List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
			
		for(BundlerPlugin bundlerPlugin : ((BRJS) rootNode).bundlerPlugins()) {
			for (AssetLocation assetLocation : getAllAssetLocations())
			{
				sourceFiles.addAll(bundlerPlugin.getSourceFiles(assetLocation));
			}
		}
		
		return sourceFiles;
	}
	
	@Override
	public SourceFile sourceFile(String requirePath) {
		for(SourceFile sourceFile : sourceFiles()) {
			if(sourceFile.getRequirePath().equals(requirePath)) {
				return sourceFile;
			}
		}
		
		return null;
	}
	
	@Override
	public List<AssetLocation> getAllAssetLocations() {
		List<AssetLocation> assetLocations = new ArrayList<>();
		
		assetLocations.add(resources());
		assetLocations.add(src());
		assetLocations.addAll(src().getChildAssetLocations()); // TODO: should we just be adding the src(), rather than all it's children?
		
		return assetLocations;
	}
}
