package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

public class ShallowAssetLocation extends AbstractBRJSNode implements AssetLocation {
	
	private AssetContainer assetContainer;
	private BRJS brjs;
	private File dir;
	
	private final Map<String, ShallowAssetLocation> resources = new HashMap<>();
	private AliasDefinitionsFile aliasDefinitionsFile;
	
	
	public ShallowAssetLocation(AssetContainer assetContainer, File dir) {
		this.assetContainer = assetContainer;
		this.dir = dir;
		this.brjs = assetContainer.root();
	}
	
	@Override
	public File dir() {
		return dir;
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {		
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(dir(), "resources/aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
		
	@Override
	public List<LinkedAssetFile> seedResources() {
		List<LinkedAssetFile> seedResources = new LinkedList<LinkedAssetFile>();
			
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
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
		
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
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
    	List<AssetLocation> resourcesList = new ArrayList<>();
    	
    	File srcDir = dir;
    	
    	while (srcDir != null)
    	{
    		resourcesList.add(createResource(srcDir));
    		if (srcDir.equals(assetContainer.src().dir()))
    		{
    			break;
    		}
    		srcDir = srcDir.getParentFile();
    	}
    	
    	return resourcesList;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
	
	private AssetLocation createResource(File srcDir) {
		String srcPath = srcDir.getAbsolutePath();
		
		if(!resources.containsKey(srcPath)) {
			resources.put(srcPath, new ShallowAssetLocation(assetContainer, srcDir));
		}
		
		return resources.get(srcPath);
	}
}
