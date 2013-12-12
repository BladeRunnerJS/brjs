package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.JsStyleUtility;

public class ShallowAssetLocation extends InstantiatedBRJSNode implements AssetLocation {
	protected AssetContainer assetContainer;
	private AliasDefinitionsFile aliasDefinitionsFile;
	
	public ShallowAssetLocation(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		this.assetContainer = (AssetContainer) parent;
	}
	
	@Override
	public File dir() {
		return super.dir();
	}
	
	@Override
	public String getJsStyle() {
		return JsStyleUtility.getJsStyle(root(), dir());
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {		
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
		
	@Override
	public List<LinkedAsset> seedResources() {
		List<LinkedAsset> seedResources = new LinkedList<LinkedAsset>();
			
		for(AssetPlugin assetPlugin : root().plugins().assetProducers()) {
			seedResources.addAll(assetPlugin.getLinkedResourceFiles(this));
		}
		
		return seedResources;
	}
	
	
	@Override
	public List<LinkedAsset> seedResources(String fileExtension) {
		List<LinkedAsset> typedSeedResources = new ArrayList<>();
		
		for(LinkedAsset seedResource : seedResources()) {
			if(seedResource.getAssetName().endsWith("." + fileExtension)) {
				typedSeedResources.add(seedResource);
			}
		}
		
		return typedSeedResources;
	}
	
	@Override
	public List<Asset> bundleResources(String fileExtension) {
		List<Asset> bundleResources = new LinkedList<Asset>();
		
		for(AssetPlugin assetPlugin : root().plugins().assetProducers()) {
			bundleResources.addAll(assetPlugin.getResourceFiles(this));
		}
		
		return bundleResources;
	}

	@Override
	public AssetContainer getAssetContainer()
	{
		return assetContainer;
	}

	@Override
	public List<AssetLocation> getDependentAssetLocations()
	{
    	return new ArrayList<>();
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
}
