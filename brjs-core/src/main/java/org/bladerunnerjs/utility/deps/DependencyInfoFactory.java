package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.model.AssetContainer;
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
		return buildDependencyGraphFromBundleSet(browsableNode.getBundleSet(), new DependencyAdder() {
			@Override
			public void add(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencies, sourceAsset, targetAsset);
			}
		});
	}
	
	public static DependencyInfo buildReverseDependencyMap(BrowsableNode browsableNode, SourceModule sourceModule) throws ModelOperationException {
		BundleSet bundleSet = browsableNode.getBundleSet();
		DependencyAdder dependencyAdder = new DependencyAdder() {
			@Override
			public void add(DependencyInfo dependencyInfo, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencyInfo, targetAsset, sourceAsset);
			}
		};
		DependencyInfo reverseDependencyGraph;
		
		if((sourceModule == null) || bundleSet.getSourceModules().contains(sourceModule)) {
			reverseDependencyGraph = buildDependencyGraphFromBundleSet(bundleSet, dependencyAdder);
		}
		else {
			reverseDependencyGraph = buildDependencyGraphFromBundlableNode(bundleSet.getBundlableNode(), dependencyAdder);
		}
		
		return reverseDependencyGraph;
	}
	
	private static DependencyInfo buildDependencyGraphFromBundleSet(BundleSet bundleSet, DependencyAdder dependencyAdder) throws ModelOperationException {
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		DependencyInfo dependencyInfo = new DependencyInfo();
		
		addSeedDependencies(dependencyAdder, bundlableNode, dependencyInfo);
		
		for(AssetLocation assetLocation : bundleSet.getResourceNodes()) {
			addAssetLocationDependencies(dependencyAdder, bundlableNode, dependencyInfo, assetLocation);
		}
		
		for(SourceModule sourceModule : bundleSet.getSourceModules()) {
			addSourceModuleDependencies(dependencyAdder, bundlableNode, dependencyInfo, sourceModule);
		}
		
		return dependencyInfo;
	}
	
	private static DependencyInfo buildDependencyGraphFromBundlableNode(BundlableNode bundlableNode, DependencyAdder dependencyAdder) throws ModelOperationException {
		DependencyInfo dependencyInfo = new DependencyInfo();
		
		addSeedDependencies(dependencyAdder, bundlableNode, dependencyInfo);
		
		for(AssetContainer assetContainer : bundlableNode.assetContainers()) {
			for(AssetLocation assetLocation : assetContainer.assetLocations()) {
				addAssetLocationDependencies(dependencyAdder, bundlableNode, dependencyInfo, assetLocation);
			}
			
			for(SourceModule sourceModule : assetContainer.sourceModules()) {
				addSourceModuleDependencies(dependencyAdder, bundlableNode, dependencyInfo, sourceModule);
			}
		}
		
		return dependencyInfo;
	}
	
	private static void addSeedDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode, DependencyInfo dependencyInfo)
		throws ModelOperationException {
		for(LinkedAsset seedAsset : bundlableNode.seedFiles()) {
			dependencyInfo.seedAssets.add(seedAsset);
			addDependencies(dependencyAdder, dependencyInfo, seedAsset, seedAsset.getDependentSourceModules(bundlableNode));
			addAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, seedAsset);
		}
	}
	
	private static void addAssetLocationDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode,
		DependencyInfo dependencyInfo, AssetLocation assetLocation) throws ModelOperationException {
		for(LinkedAsset resourceAsset : assetLocation.seedResources()) {
			dependencyInfo.resourceAssets.add(resourceAsset);
			addDependencies(dependencyAdder, dependencyInfo, resourceAsset, resourceAsset.getDependentSourceModules(bundlableNode));
			addAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, resourceAsset);
		}
	}
	
	private static void addSourceModuleDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode,
		DependencyInfo dependencyInfo, SourceModule sourceModule) throws ModelOperationException {
		addDependencies(dependencyAdder, dependencyInfo, sourceModule, sourceModule.getOrderDependentSourceModules(bundlableNode));
		addDependencies(dependencyAdder, dependencyInfo, sourceModule, sourceModule.getDependentSourceModules(bundlableNode));
		addAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, sourceModule);
		
		for(AssetLocation assetLocation : allAssetLocations(sourceModule)) {
			for(LinkedAsset assetLocationLinkedAsset : assetLocation.seedResources()) {
				if((assetLocationLinkedAsset.getDependentSourceModules(bundlableNode).size() > 0) || (assetLocationLinkedAsset.getAliasNames().size() > 0)) {
					dependencyAdder.add(dependencyInfo, sourceModule, assetLocationLinkedAsset);
				}
				
				addAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, assetLocationLinkedAsset);
			}
		}
	}
	
	private static List<AssetLocation> allAssetLocations(SourceModule sourceModule) {
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.add(sourceModule.getAssetLocation());
		assetLocations.addAll(sourceModule.getAssetLocation().dependentAssetLocations());
		
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
