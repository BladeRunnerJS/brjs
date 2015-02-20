package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class NonCircularTransitivePreExportDependencyGraphCreator {
	public static Map<SourceModule, List<SourceModule>> createGraph(Map<SourceModule, List<SourceModule>> preExportDependencyGraph, Map<SourceModule, List<SourceModule>> postExportDependencyGraph) throws ModelOperationException {
		Map<SourceModule, List<SourceModule>> combinedDefineTimeDependencyGraph = new HashMap<>();
		
		for(SourceModule sourceModule : preExportDependencyGraph.keySet()) {
			List<SourceModule> combinedDefineTimeDependencies = new ArrayList<>(preExportDependencyGraph.get(sourceModule));
			combinedDefineTimeDependencies.addAll(postExportDependencyGraph.get(sourceModule));
			
			combinedDefineTimeDependencyGraph.put(sourceModule, combinedDefineTimeDependencies);
		}
		
		Map<SourceModule, List<SourceModule>> nonCircularPostExportDependencyGraph = nonCircularPostExportDependencies(postExportDependencyGraph, combinedDefineTimeDependencyGraph);
		
		return growDependencyGraph(preExportDependencyGraph, nonCircularPostExportDependencyGraph);
	}

	private static Map<SourceModule, List<SourceModule>> nonCircularPostExportDependencies(Map<SourceModule, List<SourceModule>> postExportDependencyGraph, Map<SourceModule, List<SourceModule>> combinedDefineTimeDependencyGraph) {
		Map<SourceModule, List<SourceModule>> nonCircularPostExportDependencyGraph = new HashMap<>();
		
		for(SourceModule sourceModule : postExportDependencyGraph.keySet()) {
			List<SourceModule> dependentSourceModules = postExportDependencyGraph.get(sourceModule);
			List<SourceModule> nonCircularDependentSourceModules = new ArrayList<>();
			
			for(SourceModule dependentSourceModule : dependentSourceModules) {
				if(!reachable(dependentSourceModule, sourceModule, combinedDefineTimeDependencyGraph, new HashSet<>())) {
					nonCircularDependentSourceModules.add(dependentSourceModule);
				}
			}
			
			nonCircularPostExportDependencyGraph.put(sourceModule, nonCircularDependentSourceModules);
		}
		
		return nonCircularPostExportDependencyGraph;
	}

	private static boolean reachable(SourceModule fromSourceModule, SourceModule toSourceModule, Map<SourceModule, List<SourceModule>> combinedDefineTimeDependencyGraph, Set<SourceModule> visitedSourceModules) {
		for(SourceModule dependentSourceModule : combinedDefineTimeDependencyGraph.get(fromSourceModule)) {
			if(dependentSourceModule == toSourceModule) {
				return true;
			}
			
			if(!visitedSourceModules.contains(dependentSourceModule)) {
				visitedSourceModules.add(dependentSourceModule);
				
				if(reachable(dependentSourceModule, toSourceModule, combinedDefineTimeDependencyGraph, visitedSourceModules)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private static Map<SourceModule, List<SourceModule>> growDependencyGraph(Map<SourceModule, List<SourceModule>> dependencyGraph, Map<SourceModule, List<SourceModule>> nonCircularPostExportDependencyGraph) {
		boolean progressMade;
		
		do {
			progressMade = false;
			
			for(SourceModule sourceModule : nonCircularPostExportDependencyGraph.keySet()) {
				for(SourceModule dependentSourceModule : nonCircularPostExportDependencyGraph.get(sourceModule)) {
					if(!dependencyGraph.get(sourceModule).contains(dependentSourceModule)) {
						progressMade = true;
						dependencyGraph.get(sourceModule).add(dependentSourceModule);
					}
				}
			}
		} while(progressMade);
		
		return dependencyGraph;
	}
}
