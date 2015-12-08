package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.CircularDependencyException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class SourceModuleDependencyOrderCalculator {
	public static List<SourceModule> getOrderedSourceModules(BundlableNode bundlableNode, Map<String,SourceModule> bootstrappingSourceModules, Map<String,SourceModule> unorderedSourceModules) throws ModelOperationException {
		
		Map<SourceModule, List<SourceModule>> preExportDefineTimeDependencyGraph = DefineTimeDependencyGraphCreator.createGraph(bundlableNode, new LinkedHashSet<>(unorderedSourceModules.values()), true);
		Map<SourceModule, List<SourceModule>> postExportDefineTimeDependencyGraph = DefineTimeDependencyGraphCreator.createGraph(bundlableNode, new LinkedHashSet<>(unorderedSourceModules.values()), false);
		Map<SourceModule, List<SourceModule>> sourceModuleDependencies = 
				NonCircularTransitivePreExportDependencyGraphCreator.createGraph(preExportDefineTimeDependencyGraph, postExportDefineTimeDependencyGraph);
		
		Map<String,SourceModule> orderedSourceModules = new LinkedHashMap<>();
		Set<String> metDependencies = new LinkedHashSet<>();		
		
		orderedSourceModules.putAll(bootstrappingSourceModules);
		metDependencies.addAll(bootstrappingSourceModules.keySet());
		
		while (!unorderedSourceModules.isEmpty()) {
			Map<String,SourceModule> unprocessedSourceModules = new LinkedHashMap<>();
			boolean progressMade = false;
			
			for (String sourceModuleRequirePath : unorderedSourceModules.keySet()) {
				SourceModule sourceModule = unorderedSourceModules.get(sourceModuleRequirePath);
				if (dependenciesHaveBeenMet(sourceModuleDependencies.get(sourceModule), metDependencies)) {
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
	
	private static boolean dependenciesHaveBeenMet(List<SourceModule> dependencies, Set<String> metDependencies) throws ModelOperationException {
		for (LinkedAsset dependentSourceModule : dependencies) {
			if(!metDependencies.contains(dependentSourceModule.getPrimaryRequirePath())) {
				return false;
			}
		}
		return true;
	}
}
