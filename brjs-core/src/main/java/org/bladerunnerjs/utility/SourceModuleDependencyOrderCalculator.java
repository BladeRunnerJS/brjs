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
	private Set<SourceModule> unorderedSourceModules;
	private SourceModule bootstrapSourceModule;
	private List<SourceModule> orderedSourceModules;
	private Set<SourceModule> metDependencies;
	
	public SourceModuleDependencyOrderCalculator(BundlableNode bundlableNode, SourceModule bootstrapSourceModule, Set<SourceModule> unorderedSourceModules)
	{
		this.bundlableNode = bundlableNode;
		this.bootstrapSourceModule = bootstrapSourceModule;
		this.unorderedSourceModules = unorderedSourceModules;
		orderedSourceModules = new ArrayList<SourceModule>();
		metDependencies = new HashSet<SourceModule>();
	}
	
	public List<SourceModule> getOrderedSourceModules() throws ModelOperationException {
		orderedSourceModules = new ArrayList<>();
		metDependencies = new HashSet<>();		
		
		if (!unorderedSourceModules.isEmpty() && bootstrapSourceModule != null)
		{
			addMetDependencyToOrderedSourceModules(bootstrapSourceModule);
		}
		
		while (!unorderedSourceModules.isEmpty()) {
			Set<SourceModule> unprocessedSourceModules = new LinkedHashSet<>();
			boolean progressMade = false;
			
			for(SourceModule sourceModule : unorderedSourceModules) {
				if (dependenciesHaveBeenMet(sourceModule)) {
					progressMade = true;
					addMetDependencyToOrderedSourceModules(sourceModule);
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
		
		return orderedSourceModules;
	}

	private void addMetDependencyToOrderedSourceModules(SourceModule sourceModule)
	{
		if (!orderedSourceModules.contains(sourceModule))
		{
			orderedSourceModules.add(sourceModule);
		}
		metDependencies.add(sourceModule);
	}
	
	private boolean dependenciesHaveBeenMet(SourceModule sourceModule) throws ModelOperationException {
		for (LinkedAsset dependentSourceModule : getOrderDependentSourceModules(sourceModule, bundlableNode)) {
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
