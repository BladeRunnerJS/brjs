package org.bladerunnerjs.model.exception;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;

import com.google.common.base.Joiner;

public class CircularDependencyException extends ModelOperationException {
	private static final long serialVersionUID = 1L;
	
	public CircularDependencyException(BundlableNode bundlableNode, Set<SourceModule> sourceModules) throws ModelOperationException {
		super("Circular dependency detected: " + getCircularDependency(bundlableNode, sourceModules));
	}
	
	private static String getCircularDependency(BundlableNode bundlableNode, Set<SourceModule> sourceModules) throws ModelOperationException {
		SourceModule initialSourceModule = sourceModules.iterator().next();
		ArrayList<String> dependencyChain = new ArrayList<>();
		dependencyChain.add(initialSourceModule.getPrimaryRequirePath());
		
		return Joiner.on(" => ").join(traverseDependencies(bundlableNode, initialSourceModule, dependencyChain, new HashSet<SourceModule>()));
	}
	
	private static List<String> traverseDependencies(BundlableNode bundlableNode, SourceModule sourceModule, List<String> dependencyChain, HashSet<SourceModule> visitedSourceModules) throws ModelOperationException {
		for(Asset dependentAsset : sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode)) {
			if(dependentAsset instanceof SourceModule) {
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
						
						List<String> circularDependency = traverseDependencies(bundlableNode, dependentSourceModule, dependencyChain, visitedSourceModules);
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
