package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.file.AliasDefinitionsFile;

public class ShallowResources implements Resources {
	protected BRJS brjs;
	protected File dir;
	protected CompositeFileSet<LinkedAssetFile> seedFileSet = new CompositeFileSet<>();
	protected CompositeFileSet<AssetFile> fileSet = new CompositeFileSet<>();
	
	private boolean objectInitialized = false;
	
	public ShallowResources(BRJS brjs, File dir) {
		this.brjs = brjs;
		this.dir = dir;
	}
	
	@Override
	public File dir() {
		return dir;
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitions() {
		initialize();
		
		// TODO: implement this method
		return null;
	}
	
	@Override
	public List<LinkedAssetFile> seedResources() {
		initialize();
		
		return seedFileSet.getFiles();
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
		initialize();
		
		return fileSet.getFiles();
	}
	
	// TODO: can we find a better solution to the construction ordering problem that we have
	private void initialize() {
		if(!objectInitialized) {
			for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins()) {
				seedFileSet.addFileSet(bundlerPlugin.getFileSetFactory().getLinkedResourceFileSet(this));
				fileSet.addFileSet(bundlerPlugin.getFileSetFactory().getResourceFileSet(this));
			}
			
			objectInitialized = true;
		}
	}
}
