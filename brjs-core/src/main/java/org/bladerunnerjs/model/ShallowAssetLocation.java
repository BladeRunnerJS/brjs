package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.file.AliasDefinitionsFile;

public class ShallowAssetLocation implements AssetLocation {
	protected BRJS brjs;
	protected File dir;
	
	public ShallowAssetLocation(BRJS brjs, File dir) {
		this.brjs = brjs;
		this.dir = dir;
	}
	
	@Override
	public File dir() {
		return dir;
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitions() {		
		// TODO: implement this method
		return null;
	}
		
	@Override
	public List<LinkedAssetFile> seedResources() {
		List<LinkedAssetFile> seedResources = new LinkedList<LinkedAssetFile>();
			
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
			seedResources.addAll(bundlerPlugin.getAssetFileAccessor().getLinkedResourceFiles(this));
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
			bundleResources.addAll(bundlerPlugin.getAssetFileAccessor().getResourceFiles(this));
		}
		
		return bundleResources;
	}
}
