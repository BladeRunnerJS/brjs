package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class NonCircularTransitivePreExportDependencyGraphCreator {
	
	public static Map<String, List<String>> createGraph(Map<String, List<String>> preExportDependencyGraph, Map<String, List<String>> postExportDependencyGraph) throws ModelOperationException {
		
		Map<String, List<String>> combinedDefineTimeDependencyGraph = new LinkedHashMap<>();
		
		for (String sourceModuleRequirePath : preExportDependencyGraph.keySet()) {
			List<String> combinedDefineTimeDependencies = new ArrayList<>(preExportDependencyGraph.get(sourceModuleRequirePath));
			combinedDefineTimeDependencies.addAll(postExportDependencyGraph.get(sourceModuleRequirePath));
			
			combinedDefineTimeDependencyGraph.put(sourceModuleRequirePath, combinedDefineTimeDependencies);
		}
		
		Map<String, List<String>> nonCircularPostExportDependencyGraph = nonCircularPostExportDependencies(postExportDependencyGraph, combinedDefineTimeDependencyGraph);
		
		return growDependencyGraph(preExportDependencyGraph, nonCircularPostExportDependencyGraph);
	}

	private static Map<String, List<String>> nonCircularPostExportDependencies(Map<String, List<String>> postExportDependencyGraph, Map<String, List<String>> combinedDefineTimeDependencyGraph) {
		
		Map<String, List<String>> nonCircularPostExportDependencyGraph = new LinkedHashMap<>();
		
		for (String sourceModuleRequirePath : postExportDependencyGraph.keySet()) {
			List<String> dependentSourceModules = postExportDependencyGraph.get(sourceModuleRequirePath);
			List<String> nonCircularDependentSourceModules = new ArrayList<>();
			
			for (String dependentSourceModuleRequirePath : dependentSourceModules) {
				if (!reachable(dependentSourceModuleRequirePath, sourceModuleRequirePath, combinedDefineTimeDependencyGraph, new LinkedHashSet<>())) {
					nonCircularDependentSourceModules.add(dependentSourceModuleRequirePath);
				}
			}
			
			nonCircularPostExportDependencyGraph.put(sourceModuleRequirePath, nonCircularDependentSourceModules);
		}
		
		return nonCircularPostExportDependencyGraph;
	}

	private static boolean reachable(String fromSourceModuleRequirePath, String toSourceModuleRequirePath, Map<String, List<String>> combinedDefineTimeDependencyGraph, Set<String> visitedSourceModules) {
		
		for(String dependentSourceModuleRequirePath : combinedDefineTimeDependencyGraph.get(fromSourceModuleRequirePath)) {
			
			if (dependentSourceModuleRequirePath.equals(toSourceModuleRequirePath)) {
				return true;
			}
			
			if(!visitedSourceModules.contains(dependentSourceModuleRequirePath)) {
				visitedSourceModules.add(dependentSourceModuleRequirePath);
				
				if(reachable(dependentSourceModuleRequirePath, toSourceModuleRequirePath, combinedDefineTimeDependencyGraph, visitedSourceModules)) {
					return true;
				}
			}
		}
		
		return false;
	}

	private static Map<String, List<String>> growDependencyGraph(Map<String, List<String>> dependencyGraph, Map<String, List<String>> nonCircularPostExportDependencyGraph) {
		
		boolean progressMade;
		
		do {
			progressMade = false;
			
			for(String sourceModuleRequirePath : nonCircularPostExportDependencyGraph.keySet()) {
				for(String dependentSourceModuleRequirePath : nonCircularPostExportDependencyGraph.get(sourceModuleRequirePath)) {
					if(!dependencyGraph.get(sourceModuleRequirePath).contains(dependentSourceModuleRequirePath)) {
						progressMade = true;
						dependencyGraph.get(sourceModuleRequirePath).add(dependentSourceModuleRequirePath);
					}
				}
			}
		} while(progressMade);
		
		return dependencyGraph;
	}
}
