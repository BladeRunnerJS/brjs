package org.bladerunnerjs.utility;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.CircularDependencyException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class SourceModuleDependencyOrderCalculator {
	
	public static AssetMap<SourceModule> getOrderedSourceModules(BundlableNode bundlableNode, AssetMap<SourceModule> bootstrappingSourceModules, AssetMap<SourceModule> allSourceModules) throws ModelOperationException {
		
		Map<String, List<String>> preExportDefineTimeDependencyGraph = DefineTimeDependencyGraphCreator.createGraph(bundlableNode, allSourceModules.internalMap, true);
		Map<String, List<String>> postExportDefineTimeDependencyGraph = DefineTimeDependencyGraphCreator.createGraph(bundlableNode, allSourceModules.internalMap, false);
		
		Map<String, List<String>> sourceModuleDependencies = 
				NonCircularTransitivePreExportDependencyGraphCreator.createGraph(preExportDefineTimeDependencyGraph, postExportDefineTimeDependencyGraph);
		
		AssetMap<SourceModule> orderedSourceModules = new AssetMap<>();
		Set<String> metDependencies = new LinkedHashSet<>();		
		
		orderedSourceModules.putAll(bootstrappingSourceModules);
		metDependencies.addAll(bootstrappingSourceModules.keySet());
		
		AssetMap<SourceModule> unorderedSourceModules = new AssetMap<>(allSourceModules);
		
		while (!unorderedSourceModules.isEmpty()) {
			AssetMap<SourceModule> unprocessedSourceModules = new AssetMap<>();
			boolean progressMade = false;
			
			for (String sourceModuleRequirePath : unorderedSourceModules.keySet()) {
				SourceModule sourceModule = unorderedSourceModules.get(sourceModuleRequirePath);
				if (dependenciesHaveBeenMet(sourceModuleDependencies.get(sourceModuleRequirePath), metDependencies)) {
					progressMade = true;
					orderedSourceModules.put(sourceModule);
					metDependencies.add(sourceModuleRequirePath);
				}
				else {
					unprocessedSourceModules.put(sourceModule);
				}
			}
			
			if (!progressMade) {
				throw new CircularDependencyException(bundlableNode, new LinkedHashSet<>(unprocessedSourceModules.values()));
			}
			
			unorderedSourceModules = unprocessedSourceModules;
		}
		
		return orderedSourceModules;
	}
	
	private static boolean dependenciesHaveBeenMet(List<String> dependencies, Set<String> metDependencies) throws ModelOperationException {
		for (String dependentSourceModuleRequirePath : dependencies) {
			if(!metDependencies.contains(dependentSourceModuleRequirePath)) {
				return false;
			}
		}
		return true;
	}
	
}
