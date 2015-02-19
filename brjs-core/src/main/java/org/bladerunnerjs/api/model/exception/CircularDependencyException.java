package org.bladerunnerjs.api.model.exception;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.model.BundlableNode;

import com.google.common.base.Joiner;

/**
 * Thrown when several classes have produced a circular dependency that prevented the bundle from being written.
*/

public class CircularDependencyException extends ModelOperationException {
	private static final long serialVersionUID = 1L;
	
	public CircularDependencyException(BundlableNode bundlableNode, Set<SourceModule> sourceModules) throws ModelOperationException {
		super("Circular dependency detected: " + getCircularDependency(bundlableNode, sourceModules));
	}
	
	private static String getCircularDependency(BundlableNode bundlableNode, Set<SourceModule> sourceModules) throws ModelOperationException {
		SourceModule initialSourceModule = sourceModules.iterator().next();
		ArrayList<String> dependencyChain = new ArrayList<>();
		dependencyChain.add(initialSourceModule.getPrimaryRequirePath());
		List<String> circularDependency = null;
		
		for(SourceModule sourceModule : sourceModules) {
			dependencyChain = new ArrayList<>();
			dependencyChain.add(sourceModule.getPrimaryRequirePath());
			circularDependency = traverseDependencies(bundlableNode, sourceModule, sourceModules, dependencyChain, new HashSet<SourceModule>());
			
			if(circularDependency != null) {
				break;
			}
		}
		
		return Joiner.on(" => ").join(circularDependency);
	}
	
	private static List<String> traverseDependencies(BundlableNode bundlableNode, SourceModule sourceModule, Set<SourceModule> sourceModules, List<String> dependencyChain, HashSet<SourceModule> visitedSourceModules) throws ModelOperationException {
		for(Asset dependentAsset : sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode)) {
			if(sourceModules.contains(dependentAsset)) {
				SourceModule dependentSourceModule = (SourceModule) dependentAsset;
				String requirePath = dependentSourceModule.getPrimaryRequirePath();
				
				if(dependencyChain.contains(requirePath)) {
					dependencyChain.add(requirePath);
					return dependencyChain.subList(dependencyChain.indexOf(requirePath), dependencyChain.size());
				}
				else {
					if(!visitedSourceModules.contains(dependentSourceModule)) {
						visitedSourceModules.add(dependentSourceModule);
						dependencyChain.add(requirePath);
						
						List<String> circularDependency = traverseDependencies(bundlableNode, dependentSourceModule, sourceModules, dependencyChain, visitedSourceModules);
						if(circularDependency != null) {
							return circularDependency;
						}
						
						dependencyChain.remove(dependencyChain.size() - 1);
					}
				}
				
				
			}
		}
		
		return null;
	}
}
