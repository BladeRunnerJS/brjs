package org.bladerunnerjs.model.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;


public class BundleSetBuilder {
	Set<LinkedAsset> seedFiles = new HashSet<>();
	Set<SourceModule> sourceFiles = new LinkedHashSet<>();
	Set<AliasDefinition> activeAliases = new HashSet<>();
	Set<AssetLocation> resources = new HashSet<>();
	private BundlableNode bundlableNode;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		addImplicitDependencies();
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourcesList = new ArrayList<>();
		
		try {
			activeAliasList.addAll(activeAliases);
			resourcesList.addAll(resources);
			
			for(AliasDefinition aliasDefinition : activeAliases) {
				String requirePath = aliasDefinition.getClassName().replaceAll("\\.", "/");
				sourceFiles.add(bundlableNode.getSourceFile(requirePath));
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return new BundleSet(bundlableNode, orderSourceFiles(sourceFiles), activeAliasList, resourcesList);
	}
	
	public void addSeedFile(LinkedAsset seedFile) throws ModelOperationException {
		seedFiles.add(seedFile);
		activeAliases.addAll(getAliases(seedFile.getAliasNames()));
	}
	
	public boolean addSourceFile(SourceModule sourceFile) throws ModelOperationException {
		boolean isNewSourceFile = false;
		
		if(sourceFiles.add(sourceFile)) {
			isNewSourceFile = true;
			activeAliases.addAll(getAliases(sourceFile.getAliasNames()));
			resources.addAll(sourceFile.getAssetLocation().getAssetContainer().getAllAssetLocations());
		}
		
		return isNewSourceFile;
	}
	
	private List<AliasDefinition> getAliases(List<String> aliasNames) throws ModelOperationException {
		List<AliasDefinition> aliases = new ArrayList<>();
		
		try {
			for(String aliasName : aliasNames) {
				aliases.add(bundlableNode.getAlias(aliasName));
			}
		}
		catch(AliasException | BundlerFileProcessingException e) {
			throw new ModelOperationException(e);
		}
		
		return aliases;
	}

	private List<SourceModule> orderSourceFiles(Set<SourceModule> sourceFiles) throws ModelOperationException {
		List<SourceModule> sourceFileList = new ArrayList<>();
		Set<LinkedAsset> metDependencies = new HashSet<>();
		
		
		int maxIterations = sourceFiles.size() * sourceFiles.size();
		int iterationCount = 0;
		
		while(!sourceFiles.isEmpty()) {
			Set<SourceModule> unprocessedSourceFiles = new HashSet<>();
			for(SourceModule sourceFile : sourceFiles) {
				if(dependenciesHaveBeenMet(sourceFile, metDependencies)) {
					sourceFileList.add(sourceFile);
					metDependencies.add(sourceFile);
				}
				else {
					unprocessedSourceFiles.add(sourceFile);
				}
			}
			
			if (iterationCount++ > maxIterations)
			{
				throw new ModelOperationException("Error satisfying source file dependencies. unprocessedSourceFiles = "+stringifySourceFiles(unprocessedSourceFiles));
			}
			
			sourceFiles = unprocessedSourceFiles;
		}
		
		return sourceFileList;
	}
	
	private boolean dependenciesHaveBeenMet(SourceModule sourceModule, Set<LinkedAsset> metDependencies) throws ModelOperationException {
		for(LinkedAsset dependentSourceModule : sourceModule.getOrderDependentSourceModules(bundlableNode)) {
			if(!metDependencies.contains(dependentSourceModule)) {
				return false;
			}
		}
		
		return true;
	}
	
	private String stringifySourceFiles(Set<SourceModule> sourceFiles)
	{
		StringBuilder builder = new StringBuilder();
		for (SourceModule sourceFile : sourceFiles)
		{
			builder.append(sourceFile.getRequirePath()+", ");
		}
		builder.setLength(builder.length()-2);
		return builder.toString();
		
	}
	
	private void addImplicitDependencies() {
		try {
			sourceFiles.add(bundlableNode.getSourceFile("bootstrap"));
		} catch (RequirePathException e) {
			// do nothing: 'bootstrap' is only an implicit dependency if it exists 
		}
	}
}
