package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasException;
import org.bladerunnerjs.api.aliasing.AliasOverride;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;

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
		
		for(AssetContainer assetContainer : bundlableNode.scopeAssetContainers()) {
			for(AssetLocation assetLocation : assetContainer.assetLocations()) {
				addAssetLocationDependencies(dependencyAdder, bundlableNode, dependencyInfo, assetLocation);
			}
			
			for(LinkedAsset asset : assetContainer.linkedAssets()) {
				if(asset instanceof SourceModule){
					addSourceModuleDependencies(dependencyAdder, bundlableNode, dependencyInfo, (SourceModule)asset);
				}
			}
		}
		
		return dependencyInfo;
	}
	
	private static void addSeedDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode, DependencyInfo dependencyInfo) throws ModelOperationException {
		addOutboundAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode);
		
		for(LinkedAsset seedAsset : bundlableNode.seedAssets()) {
			dependencyInfo.seedAssets.add(seedAsset);
			List<Asset>  assets = seedAsset.getDependentAssets(bundlableNode);
			addDependencies(dependencyAdder, dependencyInfo, seedAsset, extractSourceModules(assets));
			addInboundAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, seedAsset);
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
	
	private static void addAssetLocationDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode,
		DependencyInfo dependencyInfo, AssetLocation assetLocation) throws ModelOperationException {
		for(LinkedAsset resourceAsset : assetLocation.linkedAssets()) {
			dependencyInfo.resourceAssets.add(resourceAsset);
			
			// TODO: the need for this code shows that our interfaces are incorrectly abstracted, which we should fix at some point
			if (bundlableNode instanceof Workbench && assetLocation.assetContainer() instanceof Aspect) {
				continue;
			}
			
			List<Asset>  assets = resourceAsset.getDependentAssets(bundlableNode);
			addDependencies(dependencyAdder, dependencyInfo, resourceAsset, extractSourceModules(assets));
			addInboundAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, resourceAsset);
		}
	}
	
	private static void addSourceModuleDependencies(DependencyAdder dependencyAdder, BundlableNode bundlableNode,
		DependencyInfo dependencyInfo, SourceModule sourceModule) throws ModelOperationException {
		List<SourceModule> orderDependentSourceModules = extractSourceModules( sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode) );
		addOrderedDependencies(dependencyAdder, dependencyInfo, sourceModule, orderDependentSourceModules);
		List<Asset>  assets = sourceModule.getDependentAssets(bundlableNode);
		addDependencies(dependencyAdder, dependencyInfo, sourceModule, extractSourceModules(assets));
		addInboundAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, sourceModule);
		
		for(AssetLocation assetLocation : sourceModule.assetLocations()) {
			for(LinkedAsset assetLocationLinkedAsset : assetLocation.linkedAssets()) {
				if((assetLocationLinkedAsset.getDependentAssets(bundlableNode).size() > 0) || (assetLocationLinkedAsset.getAliasNames().size() > 0)) {
					dependencyAdder.add(dependencyInfo, sourceModule, assetLocationLinkedAsset);
				}
				
				addInboundAliasDependencies(dependencyAdder, dependencyInfo, bundlableNode, assetLocationLinkedAsset);
			}
		}
	}
	
	private static void addOrderedDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencyInfo, SourceModule sourceModule, List<SourceModule> orderDependentSourceModules) throws ModelOperationException {
		for(SourceModule dependentSourceModule : orderDependentSourceModules) {
			if(!dependencyInfo.staticDeps.containsKey(sourceModule)) {
				dependencyInfo.staticDeps.put(sourceModule, new HashSet<LinkedAsset>());
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
		
//		System.out.println(sourceAsset.getAssetPath() + " -> " + targetAsset.getAssetPath());
		dependencies.map.get(sourceAsset).add(targetAsset);
	}
	
	private static void addOutboundAliasDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencies, BundlableNode bundlableNode) throws ModelOperationException {
		try {
			for(AliasOverride aliasOverride : bundlableNode.aliasesFile().aliasOverrides()) {
				addOutboundAliasDependency(dependencyAdder, dependencies, bundlableNode, bundlableNode.getAlias(aliasOverride.getName()));
			}
			
			for(AliasDefinitionsFile aliasDefinitionFile : bundlableNode.aliasDefinitionFiles()) {
				for(AliasDefinition aliasDefinition : aliasDefinitionFile.aliases()) {
					AliasDefinition alias = bundlableNode.getAlias(aliasDefinition.getName());
					addOutboundAliasDependency(dependencyAdder, dependencies, bundlableNode, alias);
				}
			}
		}
		catch(ContentFileProcessingException | RequirePathException | AliasException e) {
			throw new ModelOperationException(e);
		}
	}
	
	private static void addOutboundAliasDependency(DependencyAdder dependencyAdder, DependencyInfo dependencies, BundlableNode bundlableNode, AliasDefinition alias) throws RequirePathException {
		AliasAsset aliasAsset = new AliasAsset(alias);
		dependencies.aliasAssets.put(alias.getName(), aliasAsset);
		dependencyAdder.add(dependencies, aliasAsset, bundlableNode.getLinkedAsset(alias.getRequirePath()));
	}
	
	private static void addInboundAliasDependencies(DependencyAdder dependencyAdder, DependencyInfo dependencies, BundlableNode bundlableNode, LinkedAsset linkedAsset) throws ModelOperationException {
		try {
			for(String aliasName : linkedAsset.getAliasNames()) {
				AliasDefinition alias = bundlableNode.getAlias(aliasName);
				AliasAsset aliasAsset = dependencies.aliasAssets.get(alias.getName());
				dependencyAdder.add(dependencies, linkedAsset, aliasAsset);
			}
		}
		catch(AliasException | ContentFileProcessingException e) {
			throw new ModelOperationException(e);
		}
	}
	
	private static interface DependencyAdder {
		void add(DependencyInfo dependencies, LinkedAsset sourceAsset, LinkedAsset targetAsset);
	}
}
