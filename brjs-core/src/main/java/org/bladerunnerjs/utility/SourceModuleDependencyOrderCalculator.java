package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.CircularDependencyException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class SourceModuleDependencyOrderCalculator {
	
	public static List<SourceModule> getOrderedSourceModules(BundlableNode bundlableNode, AssetMap<SourceModule> bootstrappingSourceModules, AssetMap<SourceModule> allSourceModules) throws ModelOperationException {
		
		Map<String, List<String>> preExportDefineTimeDependencyGraph = DefineTimeDependencyGraphCreator.createGraph(bundlableNode, allSourceModules.internalMap, true);
		Map<String, List<String>> postExportDefineTimeDependencyGraph = DefineTimeDependencyGraphCreator.createGraph(bundlableNode, allSourceModules.internalMap, false);
		
		Map<String, List<String>> sourceModuleDependencies = 
				NonCircularTransitivePreExportDependencyGraphCreator.createGraph(preExportDefineTimeDependencyGraph, postExportDefineTimeDependencyGraph);
		
		Map<String,SourceModule> orderedSourceModules = new LinkedHashMap<>();
		Set<String> metDependencies = new LinkedHashSet<>();		
		
		orderedSourceModules.putAll(bootstrappingSourceModules.internalMap);
		metDependencies.addAll(bootstrappingSourceModules.keySet());
		
		Map<String,SourceModule> unorderedSourceModules = new LinkedHashMap<>(allSourceModules.internalMap);
		
		while (!unorderedSourceModules.isEmpty()) {
			Map<String,SourceModule> unprocessedSourceModules = new LinkedHashMap<>();
			boolean progressMade = false;
			
			for (String sourceModuleRequirePath : unorderedSourceModules.keySet()) {
				SourceModule sourceModule = unorderedSourceModules.get(sourceModuleRequirePath);
				if (dependenciesHaveBeenMet(sourceModuleDependencies.get(sourceModuleRequirePath), metDependencies)) {
					progressMade = true;
					orderedSourceModules.put(sourceModuleRequirePath, sourceModule);
					metDependencies.add(sourceModuleRequirePath);
				}
				else {
					unprocessedSourceModules.put(sourceModuleRequirePath, sourceModule);
				}
			}
			
			if (!progressMade) {
				throw new CircularDependencyException(bundlableNode, new LinkedHashSet<>(unprocessedSourceModules.values()));
			}
			
			unorderedSourceModules = unprocessedSourceModules;
		}
		
		return new ArrayList<>(orderedSourceModules.values());
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
