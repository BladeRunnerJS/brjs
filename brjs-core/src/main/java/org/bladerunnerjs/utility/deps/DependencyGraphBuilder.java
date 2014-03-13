package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class DependencyGraphBuilder {
	public static Map<LinkedAsset, Set<LinkedAsset>> buildForwardDependencyGraph(BrowsableNode browsableNode) throws ModelOperationException {
		return buildDependencyGraph(browsableNode, new DependencyAdder() {
			@Override
			public void add(Map<LinkedAsset, Set<LinkedAsset>> dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencies, sourceAsset, targetAsset);
			}
		});
	}
	
	public static Map<LinkedAsset, Set<LinkedAsset>> buildReverseDependencyGraph(BrowsableNode browsableNode) throws ModelOperationException {
		return buildDependencyGraph(browsableNode, new DependencyAdder() {
			@Override
			public void add(Map<LinkedAsset, Set<LinkedAsset>> dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencies, targetAsset, sourceAsset);
			}
		});
	}
	
	private static Map<LinkedAsset, Set<LinkedAsset>> buildDependencyGraph(BrowsableNode browsableNode, DependencyAdder dependencyAdder) throws ModelOperationException {
		Map<LinkedAsset, Set<LinkedAsset>> dependencies = new LinkedHashMap<>();
		BundleSet bundleSet = browsableNode.getBundleSet();
		
		for(LinkedAsset linkedAsset : browsableNode.seedFiles()) {
			addDependencies(dependencyAdder, dependencies, linkedAsset, linkedAsset.getDependentSourceModules(browsableNode));
		}
		
		for(AssetLocation assetLocation : bundleSet.getResourceNodes()) {
			for(LinkedAsset linkedAsset : assetLocation.seedResources()) {
				addDependencies(dependencyAdder, dependencies, linkedAsset, linkedAsset.getDependentSourceModules(browsableNode));
			}
		}
		
		for(SourceModule sourceModule : bundleSet.getSourceModules()) {
			addDependencies(dependencyAdder, dependencies, sourceModule, sourceModule.getOrderDependentSourceModules(browsableNode));
			addDependencies(dependencyAdder, dependencies, sourceModule, sourceModule.getDependentSourceModules(browsableNode));
			
			for(AssetLocation assetLocation : allAssetLocations(sourceModule)) {
				for(LinkedAsset assetLocationLinkedAsset : assetLocation.seedResources()) {
					if(assetLocationLinkedAsset.getDependentSourceModules(browsableNode).size() > 0) {
						dependencyAdder.add(dependencies, sourceModule, assetLocationLinkedAsset);
					}
				}
			}
		}
		
		return dependencies;
	}
	
	private static List<AssetLocation> allAssetLocations(SourceModule sourceModule) {
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add(sourceModule.getAssetLocation());
		assetLocations.addAll(sourceModule.getAssetLocation().getDependentAssetLocations());
		
		return assetLocations;
	}
	
	private static void addDependencies(DependencyAdder dependencyAdder, Map<LinkedAsset, Set<LinkedAsset>> dependencies, LinkedAsset sourceAsset, List<SourceModule> targetAssets) throws ModelOperationException {
		for(SourceModule sourceModuleDependency : targetAssets) {
			dependencyAdder.add(dependencies, sourceAsset, sourceModuleDependency);
		}
	}
	
	private static void addDependency(Map<LinkedAsset, Set<LinkedAsset>> dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
		if(!dependencies.containsKey(sourceAsset)) {
			dependencies.put(sourceAsset, new LinkedHashSet<LinkedAsset>());
		}
		
		dependencies.get(sourceAsset).add(targetAsset);
	}
	
	private static interface DependencyAdder {
		void add(Map<LinkedAsset, Set<LinkedAsset>> dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset);
	}
}
