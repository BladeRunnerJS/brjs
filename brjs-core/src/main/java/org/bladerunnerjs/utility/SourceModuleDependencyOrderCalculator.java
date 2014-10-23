package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.CircularDependencyException;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class SourceModuleDependencyOrderCalculator {
	// TODO: having alias dependencies passed in rather than having each source-module knowing it's dependencies is a hack we can remove once we fix <https://github.com/BladeRunnerJS/brjs/issues/482>
	public static List<SourceModule> getOrderedSourceModules(BundlableNode bundlableNode, List<SourceModule> bootstrappingSourceModules, Set<SourceModule> unorderedSourceModules, Map<SourceModule, Set<SourceModule>> aliasDependencies) throws ModelOperationException {
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
				if (dependenciesHaveBeenMet(getOrderDependentSourceModules(bundlableNode, sourceModule, aliasDependencies), metDependencies)) {
					progressMade = true;
					orderedSourceModules.add(sourceModule);
					metDependencies.add(sourceModule);
				}
				else {
					unprocessedSourceModules.add(sourceModule);
				}
			}
			
			if (!progressMade)
			{
				throw new CircularDependencyException(unprocessedSourceModules);
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
	
	private static List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode, SourceModule sourceModule, Map<SourceModule, Set<SourceModule>> aliasDependencies) throws ModelOperationException {
		List<Asset> orderDependentSourceModules = new ArrayList<>(sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode));
		
		if(aliasDependencies.containsKey(sourceModule)) {
			orderDependentSourceModules.addAll(aliasDependencies.get(sourceModule));
		}
		
		return extractSourceModules(orderDependentSourceModules);
	}
	
	private static List<SourceModule> extractSourceModules(List<Asset> assets){
		List<SourceModule> sourceModules = new ArrayList<SourceModule>();
		for(Asset asset : assets){
			if(asset instanceof SourceModule){
				sourceModules.add((SourceModule)asset);
			}
		}
		return sourceModules;
	}
}
