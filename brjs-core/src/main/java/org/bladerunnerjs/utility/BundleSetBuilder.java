package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;


public class BundleSetBuilder {
	Set<LinkedAsset> seedFiles = new HashSet<>();
	Set<SourceModule> sourceModules = new LinkedHashSet<>();
	Set<AliasDefinition> activeAliases = new HashSet<>();
	Set<AssetLocation> resources = new HashSet<>();
	private BundlableNode bundlableNode;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourcesList = new ArrayList<>();
		
		try {
			activeAliasList.addAll(activeAliases);
			resourcesList.addAll(resources);
			
			for(AliasDefinition aliasDefinition : activeAliases) {
				String requirePath = aliasDefinition.getClassName().replaceAll("\\.", "/");
				sourceModules.add(bundlableNode.getSourceModule(requirePath));
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return new BundleSet(bundlableNode, orderSourceModules(sourceModules), activeAliasList, resourcesList);
	}
	
	public void addSeedFile(LinkedAsset seedFile) throws ModelOperationException {
		seedFiles.add(seedFile);
		activeAliases.addAll(getAliases(seedFile.getAliasNames()));
	}
	
	public boolean addSourceModule(SourceModule sourceModule) throws ModelOperationException {
		boolean isNewSourceFile = false;
		
		if(sourceModules.add(sourceModule)) {
			isNewSourceFile = true;
			activeAliases.addAll(getAliases(sourceModule.getAliasNames()));
			resources.addAll(sourceModule.getAssetLocation().getAssetContainer().getAllAssetLocations());
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

	private List<SourceModule> orderSourceModules(Set<SourceModule> sourceModules) throws ModelOperationException {
		List<SourceModule> sourceModulesList = new ArrayList<>();
		Set<LinkedAsset> metDependencies = new HashSet<>();
		
		
		int maxIterations = sourceModules.size() * sourceModules.size();
		int iterationCount = 0;
		
		while(!sourceModules.isEmpty()) {
			Set<SourceModule> unprocessedSourceModules = new HashSet<>();
			for(SourceModule sourceModule : sourceModules) {
				if(dependenciesHaveBeenMet(sourceModule, metDependencies)) {
					sourceModulesList.add(sourceModule);
					metDependencies.add(sourceModule);
				}
				else {
					unprocessedSourceModules.add(sourceModule);
				}
			}
			
			if (iterationCount++ > maxIterations)
			{
				throw new ModelOperationException("Error satisfying source file dependencies. unprocessedSourceFiles = "+stringifySourceModules(unprocessedSourceModules));
			}
			
			sourceModules = unprocessedSourceModules;
		}
		
		return sourceModulesList;
	}
	
	private boolean dependenciesHaveBeenMet(SourceModule sourceModule, Set<LinkedAsset> metDependencies) throws ModelOperationException {
		for(LinkedAsset dependentSourceModule : getOrderDependentSourceModules(sourceModule, bundlableNode)) {
			if(!metDependencies.contains(dependentSourceModule)) {
				return false;
			}
		}
		
		return true;
	}
	
	private List<SourceModule> getOrderDependentSourceModules(SourceModule sourceModule, BundlableNode bundlableNode) throws ModelOperationException {
		List<SourceModule> orderDependentSourceModules = sourceModule.getOrderDependentSourceModules(bundlableNode);
		
		if(!sourceModule.getRequirePath().equals("bootstrap")) {
			try {
				orderDependentSourceModules.add(bundlableNode.getSourceModule("bootstrap"));
			}
			catch(RequirePathException e) {
				// do nothing: 'bootstrap' is only an implicit dependency if it exists 
			}
		}
		
		return orderDependentSourceModules;
	}

	private String stringifySourceModules(Set<SourceModule> sourceModules)
	{
		StringBuilder builder = new StringBuilder();
		for (SourceModule sourceModule : sourceModules)
		{
			builder.append(sourceModule.getRequirePath()+", ");
		}
		builder.setLength(builder.length()-2);
		return builder.toString();
		
	}
}
