package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;

public class DependencyInfoFactory {
	public static DependencyInfo buildForwardDependencyMap(BundlableNode bundlableNode) throws ModelOperationException {
		return buildDependencyGraphFromBundleSet(bundlableNode.getBundleSet(), new DependencyAdder() {
			@Override
			public void add(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
				addDependency(dependencies, sourceAsset, targetAsset);
			}
		});
	}
	
	public static DependencyInfo buildReverseDependencyMap(BundlableNode bundlableNode, SourceModule sourceModule) throws ModelOperationException {
		BundleSet bundleSet = bundlableNode.getBundleSet();
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
			reverseDependencyGraph = buildDependencyGraphFromBundlableNode(bundleSet.bundlableNode(), dependencyAdder);
		}
		
		return reverseDependencyGraph;
	}
	
	private static DependencyInfo buildDependencyGraphFromBundleSet(BundleSet bundleSet, DependencyAdder dependencyAdder) throws ModelOperationException {
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		DependencyInfo dependencyInfo = new DependencyInfo();
		
		addSeedDependencies(dependencyAdder, bundlableNode, dependencyInfo);
		
		for(LinkedAsset asset : bundleSet.getAssets(LinkedAsset.class)) {
			addAssetDependencies(dependencyAdder, bundlableNode, dependencyInfo, asset);
		}
		
		return dependencyInfo;
	}
	
	private static DependencyInfo buildDependencyGraphFromBundlableNode(BundlableNode bundlableNode, DependencyAdder dependencyAdder) throws ModelOperationException {
		DependencyInfo dependencyInfo = new DependencyInfo();
		
		addSeedDependencies(dependencyAdder, bundlableNode, dependencyInfo);
		
		for(AssetContainer assetContainer : bundlableNode.scopeAssetContainers()) {			
			for(Asset asset : assetContainer.assets()) {
				if(asset instanceof SourceModule){
					addAssetDependencies(dependencyAdder, bundlableNode, dependencyInfo, (SourceModule)asset);
				}
			}
		}
		
		return dependencyInfo;
	}
	
	private static void addSeedDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode, DependencyInfo dependencyInfo) throws ModelOperationException {
		for(LinkedAsset seedAsset : bundlableNode.seedAssets()) {
			dependencyInfo.seedAssets.add(seedAsset);
			List<Asset>  assets = seedAsset.getDependentAssets(bundlableNode);
			addDependencies(dependencyAdder, dependencyInfo, seedAsset, extractSourceModules(assets));
		}
	}
	
	private static List<SourceModule> extractSourceModules(List<Asset> assets){
		List<SourceModule> results = new ArrayList<SourceModule>();
		for(Asset asset : assets){
			if(asset instanceof SourceModule){
				results.add((SourceModule)asset);
			}
		}
		return results;
	}
	
	private static void addAssetDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode,
		DependencyInfo dependencyInfo, LinkedAsset asset) throws ModelOperationException {
		
		if (asset instanceof SourceModule) {
			SourceModule sourceModule = (SourceModule) asset;
			List<SourceModule> orderDependentSourceModules = extractSourceModules( sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode) );
			addOrderedDependencies(dependencyAdder, dependencyInfo, sourceModule, orderDependentSourceModules);
		}
		if (bundlableNode instanceof Workbench<?> && asset.file().isChildOf(asset.assetContainer().file("resources"))) {
			return;
		}
		List<Asset>  assets = asset.getDependentAssets(bundlableNode);
		addDependencies(dependencyAdder, dependencyInfo, asset, extractSourceModules(assets));
	}
	
	private static void addOrderedDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencyInfo, SourceModule sourceModule, List<SourceModule> orderDependentSourceModules) throws ModelOperationException {
		for(SourceModule dependentSourceModule : orderDependentSourceModules) {
			if(!dependencyInfo.staticDeps.containsKey(sourceModule)) {
				dependencyInfo.staticDeps.put(sourceModule, new LinkedHashSet<LinkedAsset>());
			}
			
			dependencyInfo.staticDeps.get(sourceModule).add(dependentSourceModule);
		}
		
		addDependencies(dependencyAdder, dependencyInfo, sourceModule, orderDependentSourceModules);
	}
	
	private static void addDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencyInfo, LinkedAsset sourceAsset, List<SourceModule> targetAssets) throws ModelOperationException {
		for(SourceModule sourceModuleDependency : targetAssets) {
			dependencyAdder.add(dependencyInfo, sourceAsset, sourceModuleDependency);
		}
	}
	
	private static void addDependency(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset) {
		if(targetAsset == null) {
			throw new RuntimeException("Attempt to map '" + sourceAsset.getAssetPath() + "' to null.");
		}
		else if (sourceAsset == targetAsset) {
			throw new RuntimeException("Attempt to map '" + sourceAsset.getAssetPath() + "' to '" + targetAsset.getAssetPath() + "'.");
		}
		else {
			Set<LinkedAsset> targetDependencies = dependencies.map.get(sourceAsset);
			
 			if((targetDependencies != null) && targetDependencies.contains(targetAsset) && !dependencies.seedAssets.contains(sourceAsset)) {
//				throw new RuntimeException("Attempt to re-map '" + sourceAsset.getAssetPath() + "' to '" + targetAsset.getAssetPath() + "'.");
			}
		}
		
		if(!dependencies.map.containsKey(sourceAsset)) {
			dependencies.map.put(sourceAsset, new LinkedHashSet<LinkedAsset>());
		}
		dependencies.map.get(sourceAsset).add(targetAsset);
	}
	
	private static interface DependencyAdder {
		void add(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset);
	}
}
