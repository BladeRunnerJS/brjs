package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.CircularDependencyException;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class SourceModuleDependencyOrderCalculator
{
	
	private BundlableNode bundlableNode;
	
	public SourceModuleDependencyOrderCalculator(BundlableNode bundlableNode)
	{
		this.bundlableNode = bundlableNode;
	}
	
	public List<SourceModule> orderSourceModules(SourceModule bootstrapSourceModule, Set<SourceModule> sourceModules) throws ModelOperationException {
		List<SourceModule> orderedSourceModules = new ArrayList<>();
		Set<LinkedAsset> metDependencies = new HashSet<>();		
		
		if (!sourceModules.isEmpty() && bootstrapSourceModule != null)
		{
			addMetDependencyToOrderedSourceModules(orderedSourceModules, metDependencies, bootstrapSourceModule);
		}
		
		while (!sourceModules.isEmpty()) {
			Set<SourceModule> unprocessedSourceModules = new LinkedHashSet<>();
			boolean progressMade = false;
			
			for(SourceModule sourceModule : sourceModules) {
				if (dependenciesHaveBeenMet(sourceModule, metDependencies)) {
					progressMade = true;
					addMetDependencyToOrderedSourceModules(orderedSourceModules, metDependencies, sourceModule);
				}
				else {
					unprocessedSourceModules.add(sourceModule);
				}
			}
			
			if (!progressMade)
			{
				throw new CircularDependencyException(unprocessedSourceModules);
			}
			
			sourceModules = unprocessedSourceModules;
		}
		
		return orderedSourceModules;
	}

	private void addMetDependencyToOrderedSourceModules(List<SourceModule> orderedSourceModules, Set<LinkedAsset> metDependencies, SourceModule sourceModule)
	{
		if (!orderedSourceModules.contains(sourceModule))
		{
			orderedSourceModules.add(sourceModule);
		}
		metDependencies.add(sourceModule);
	}
	
	private boolean dependenciesHaveBeenMet(SourceModule sourceModule, Set<LinkedAsset> metDependencies) throws ModelOperationException {
		for(LinkedAsset dependentSourceModule : getOrderDependentSourceModules(sourceModule, bundlableNode)) {
			if(!metDependencies.contains(dependentSourceModule)) {
				return false;
			}
		}
		
		return true;
	}
	
	
	private List<SourceModule> getOrderDependentSourceModules(SourceModule sourceModule, BundlableNode bundlableNode) throws ModelOperationException
	{
		List<SourceModule> orderDependentSourceModules = sourceModule.getOrderDependentSourceModules(bundlableNode);
		return orderDependentSourceModules;
	}
	
}
