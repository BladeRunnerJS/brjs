package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
	private List<SourceModule> bootstrappingSourceModules;
	private List<SourceModule> orderedSourceModules;
	private Set<SourceModule> metDependencies;
	private final Map<SourceModule, Set<SourceModule>> orderDependentSourceModuleDependencies;
	
	public SourceModuleDependencyOrderCalculator(BundlableNode bundlableNode, List<SourceModule> bootstrappingSourceModules, Set<SourceModule> unorderedSourceModules, Map<SourceModule, Set<SourceModule>> orderDependentSourceModuleDependencies)
	{
		this.bundlableNode = bundlableNode;
		this.bootstrappingSourceModules = bootstrappingSourceModules;
		this.unorderedSourceModules = unorderedSourceModules;
		this.orderDependentSourceModuleDependencies = orderDependentSourceModuleDependencies;
		orderedSourceModules = new ArrayList<SourceModule>();
		metDependencies = new HashSet<SourceModule>();
	}
	
	public List<SourceModule> getOrderedSourceModules() throws ModelOperationException {
		orderedSourceModules = new ArrayList<>();
		metDependencies = new HashSet<>();		
		
		for (SourceModule bootstrapModule : bootstrappingSourceModules)
		{
			addMetDependencyToOrderedSourceModules(bootstrapModule);			
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
		List<SourceModule> orderDependentSourceModules = new ArrayList<>(sourceModule.getOrderDependentSourceModules(bundlableNode));
		
		if(orderDependentSourceModuleDependencies.containsKey(sourceModule)) {
			orderDependentSourceModules.addAll(orderDependentSourceModuleDependencies.get(sourceModule));
		}
		
		return orderDependentSourceModules;
	}
	
}
