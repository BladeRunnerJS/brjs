package org.bladerunnerjs.model.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasException;
import org.bladerunnerjs.model.aliasing.AliasName;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;


public class BundleSetBuilder {
	Set<LinkedAssetFile> seedFiles = new HashSet<>();
	Set<SourceFile> sourceFiles = new HashSet<>();
	Set<AliasDefinition> activeAliases = new HashSet<>();
	Set<AssetLocation> resources = new HashSet<>();
	private BundlableNode bundlableNode;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourcesList = new ArrayList<>();
		
		activeAliasList.addAll(activeAliases);
		resourcesList.addAll(resources);
		
		return new BundleSet(bundlableNode, orderSourceFiles(sourceFiles), activeAliasList, resourcesList);
	}
	
	public void addSeedFile(LinkedAssetFile seedFile) throws ModelOperationException {
		seedFiles.add(seedFile);
		activeAliases.addAll(getAliases(seedFile.getAliasNames()));
	}
	
	public boolean addSourceFile(SourceFile sourceFile) throws ModelOperationException {
		boolean isNewSourceFile = false;
		
		if(sourceFiles.add(sourceFile)) {
			isNewSourceFile = true;
			activeAliases.addAll(getAliases(sourceFile.getAliasNames()));
			resources.addAll(sourceFile.getAssetLocation().getAssetContainer().getAllAssetLocations());
		}
		
		return isNewSourceFile;
	}
	
	private List<AliasDefinition> getAliases(List<AliasName> aliasNames) throws ModelOperationException {
		List<AliasDefinition> aliases = new ArrayList<>();
		
		try {
			for(AliasName aliasName : aliasNames) {
				aliases.add(bundlableNode.getAlias(aliasName));
			}
		}
		catch(AliasException | BundlerFileProcessingException e) {
			throw new ModelOperationException(e);
		}
		
		return aliases;
	}

	private List<SourceFile> orderSourceFiles(Set<SourceFile> sourceFiles) throws ModelOperationException {
		List<SourceFile> sourceFileList = new ArrayList<>();
		Set<LinkedAssetFile> metDependencies = new HashSet<>();
		
		while(!sourceFiles.isEmpty()) {
			Set<SourceFile> unprocessedSourceFiles = new HashSet<>();
			
			for(SourceFile sourceFile : sourceFiles) {
				if(dependenciesHaveBeenMet(sourceFile, metDependencies)) {
					sourceFileList.add(sourceFile);
					metDependencies.add(sourceFile);
				}
				else {
					unprocessedSourceFiles.add(sourceFile);
				}
			}
			
			sourceFiles = unprocessedSourceFiles;
		}
		
		return sourceFileList;
	}
	
	private boolean dependenciesHaveBeenMet(LinkedAssetFile sourceFile, Set<LinkedAssetFile> metDependencies) throws ModelOperationException {
		for(LinkedAssetFile dependentSourceFile : sourceFile.getDependentSourceFiles()) {
			if(!metDependencies.contains(dependentSourceFile)) {
				return false;
			}
		}
		
		return true;
	}
}
