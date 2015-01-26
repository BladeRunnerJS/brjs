package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.CircularDependencyException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.BundlableNode;

public class SourceModuleDependencyOrderCalculator {
	public static List<SourceModule> getOrderedSourceModules(BundlableNode bundlableNode, List<SourceModule> bootstrappingSourceModules, Set<SourceModule> unorderedSourceModules) throws ModelOperationException {
		Map<SourceModule, List<SourceModule>> sourceModuleDependencies = NonCircularTransitivePreExportDependencyGraphCreator.createGraph(
			DefineTimeDependencyGraphCreator.createGraph(bundlableNode, unorderedSourceModules, true), DefineTimeDependencyGraphCreator.createGraph(bundlableNode, unorderedSourceModules, false));
		Set<SourceModule> orderedSourceModules = new LinkedHashSet<>();
		Set<SourceModule> metDependencies = new HashSet<>();		
		
		for (SourceModule bootstrapModule : bootstrappingSourceModules) {
			orderedSourceModules.add(bootstrapModule);
			metDependencies.add(bootstrapModule);
		}
		
		while (!unorderedSourceModules.isEmpty()) {
			Set<SourceModule> unprocessedSourceModules = new LinkedHashSet<>();
			boolean progressMade = false;
			
			for(SourceModule sourceModule : unorderedSourceModules) {
				if (dependenciesHaveBeenMet(sourceModuleDependencies.get(sourceModule), metDependencies)) {
					progressMade = true;
					orderedSourceModules.add(sourceModule);
					metDependencies.add(sourceModule);
				}
				else {
					unprocessedSourceModules.add(sourceModule);
				}
			}
			
			if (!progressMade) {
				throw new CircularDependencyException(bundlableNode, unprocessedSourceModules);
			}
			
			unorderedSourceModules = unprocessedSourceModules;
		}
		
		return new ArrayList<>(orderedSourceModules);
	}
	
	private static boolean dependenciesHaveBeenMet(List<SourceModule> dependencies, Set<SourceModule> metDependencies) throws ModelOperationException {
		for (LinkedAsset dependentSourceModule : dependencies) {
			if(!metDependencies.contains(dependentSourceModule)) {
				return false;
			}
		}
		return true;
	}
}
