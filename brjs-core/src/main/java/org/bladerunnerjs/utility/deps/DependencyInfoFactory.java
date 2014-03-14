package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class DependencyInfoFactory {
	public static DependencyInfo buildForwardDependencyMap(BrowsableNode browsableNode) throws ModelOperationException {
		return buildDependencyGraph(browsableNode, new DependencyAdder() {
			@Override
			public void add(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencies, sourceAsset, targetAsset);
			}
		});
	}
	
	public static DependencyInfo buildReverseDependencyMap(BrowsableNode browsableNode) throws ModelOperationException {
		return buildDependencyGraph(browsableNode, new DependencyAdder() {
			@Override
			public void add(DependencyInfo dependencyInfo, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencyInfo, targetAsset, sourceAsset);
			}
		});
	}
	
	private static DependencyInfo buildDependencyGraph(BrowsableNode browsableNode, DependencyAdder dependencyAdder) throws ModelOperationException {
		DependencyInfo dependencyInfo = new DependencyInfo();
		BundleSet bundleSet = browsableNode.getBundleSet();
		
		for(LinkedAsset seedAsset : browsableNode.seedFiles()) {
			dependencyInfo.seedAssets.add(seedAsset);
			addDependencies(dependencyAdder, dependencyInfo, seedAsset, seedAsset.getDependentSourceModules(browsableNode));
			addAliasDependencies(dependencyAdder, dependencyInfo, browsableNode, seedAsset);
		}
		
		for(AssetLocation assetLocation : bundleSet.getResourceNodes()) {
			for(LinkedAsset resourceAsset : assetLocation.seedResources()) {
				dependencyInfo.resourceAssets.add(resourceAsset);
				addDependencies(dependencyAdder, dependencyInfo, resourceAsset, resourceAsset.getDependentSourceModules(browsableNode));
				addAliasDependencies(dependencyAdder, dependencyInfo, browsableNode, resourceAsset);
			}
		}
		
		for(SourceModule sourceModule : bundleSet.getSourceModules()) {
			addDependencies(dependencyAdder, dependencyInfo, sourceModule, sourceModule.getOrderDependentSourceModules(browsableNode));
			addDependencies(dependencyAdder, dependencyInfo, sourceModule, sourceModule.getDependentSourceModules(browsableNode));
			addAliasDependencies(dependencyAdder, dependencyInfo, browsableNode, sourceModule);
			
			for(AssetLocation assetLocation : allAssetLocations(sourceModule)) {
				for(LinkedAsset assetLocationLinkedAsset : assetLocation.seedResources()) {
					if((assetLocationLinkedAsset.getDependentSourceModules(browsableNode).size() > 0) || (assetLocationLinkedAsset.getAliasNames().size() > 0)) {
						dependencyAdder.add(dependencyInfo, sourceModule, assetLocationLinkedAsset);
					}
					
					addAliasDependencies(dependencyAdder, dependencyInfo, browsableNode, assetLocationLinkedAsset);
				}
			}
		}
		
		return dependencyInfo;
	}
	
	private static List<AssetLocation> allAssetLocations(SourceModule sourceModule) {
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add(sourceModule.getAssetLocation());
		assetLocations.addAll(sourceModule.getAssetLocation().getDependentAssetLocations());
		
		return assetLocations;
	}
	
	private static void addDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencyInfo, LinkedAsset sourceAsset, List<SourceModule> targetAssets) throws ModelOperationException {
		for(SourceModule sourceModuleDependency : targetAssets) {
			dependencyAdder.add(dependencyInfo, sourceAsset, sourceModuleDependency);
		}
	}
	
	private static void addDependency(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
		if(!dependencies.map.containsKey(sourceAsset)) {
			dependencies.map.put(sourceAsset, new LinkedHashSet<LinkedAsset>());
		}
		
		dependencies.map.get(sourceAsset).add(targetAsset);
	}
	
	private static void addAliasDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencies, BundlableNode bundlableNode, LinkedAsset linkedAsset) throws ModelOperationException {
		try {
			for(String aliasName : linkedAsset.getAliasNames()) {
				AliasDefinition alias = bundlableNode.getAlias(aliasName);
				AliasAsset aliasAsset = new AliasAsset(alias);
				dependencyAdder.add(dependencies, linkedAsset, aliasAsset);
				dependencyAdder.add(dependencies, aliasAsset, bundlableNode.getSourceModule(alias.getRequirePath()));
			}
		}
		catch(AliasException | ContentFileProcessingException | RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
	}
	
	private static interface DependencyAdder {
		void add(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset);
	}
}
