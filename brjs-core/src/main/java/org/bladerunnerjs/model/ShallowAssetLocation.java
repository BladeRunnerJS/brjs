package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

public class ShallowAssetLocation extends AbstractBRJSNode implements AssetLocation {
	protected AssetContainer assetContainer;
	private AliasDefinitionsFile aliasDefinitionsFile;
	
	public ShallowAssetLocation(RootNode rootNode, Node parent, File dir)
	{
		init(rootNode, parent, dir);
		this.assetContainer = (AssetContainer) parent;
	}
	
	@Override
	public File dir() {
		return super.dir();
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {		
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
		
	@Override
	public List<LinkedAssetFile> seedResources() {
		List<LinkedAssetFile> seedResources = new LinkedList<LinkedAssetFile>();
			
		for(BundlerPlugin bundlerPlugin : root().plugins().bundlers()) {
			seedResources.addAll(bundlerPlugin.getLinkedResourceFiles(this));
		}
		
		return seedResources;
	}
	
	
	@Override
	public List<LinkedAssetFile> seedResources(String fileExtension) {
		List<LinkedAssetFile> typedSeedResources = new ArrayList<>();
		
		for(LinkedAssetFile seedResource : seedResources()) {
			if(seedResource.getUnderlyingFile().getName().endsWith("." + fileExtension)) {
				typedSeedResources.add(seedResource);
			}
		}
		
		return typedSeedResources;
	}
	
	@Override
	public List<AssetFile> bundleResources(String fileExtension) {
		List<AssetFile> bundleResources = new LinkedList<AssetFile>();
		
		for(BundlerPlugin bundlerPlugin : root().plugins().bundlers()) {
			bundleResources.addAll(bundlerPlugin.getResourceFiles(this));
		}
		
		return bundleResources;
	}

	@Override
	public AssetContainer getAssetContainer()
	{
		return assetContainer;
	}

	@Override
	public List<AssetLocation> getAncestorAssetLocations()
	{
    	return new ArrayList<>();
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
}
