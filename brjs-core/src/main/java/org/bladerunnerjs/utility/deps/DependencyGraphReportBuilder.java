package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;

public class DependencyGraphReportBuilder {
	public static String createReport(Aspect aspect) throws ModelOperationException {
		return "Aspect '" + aspect.getName() + "' dependencies found:\n" + createReport(aspect, aspect.seedFiles(),
			new MappedDependencyProvider(DependencyGraphBuilder.buildForwardDependencyGraph(aspect), true));
	}
	
	public static String createReport(Workbench workbench) throws ModelOperationException {
		return "Workbench dependencies found:\n" + createReport(workbench, workbench.seedFiles(),
			new MappedDependencyProvider(DependencyGraphBuilder.buildForwardDependencyGraph(workbench), true));
	}
	
	public static String createReport(BrowsableNode browsableNode, String requirePath) throws ModelOperationException, RequirePathException {
		SourceModule sourceModule = browsableNode.getSourceModule(requirePath);
		List<LinkedAsset> linkedAssets = new ArrayList<>();
		linkedAssets.add(sourceModule);
		
		return "Source module '" + sourceModule.getRequirePath() + "' dependencies found:\n" + createReport(browsableNode, linkedAssets,
			new MappedDependencyProvider(DependencyGraphBuilder.buildReverseDependencyGraph(browsableNode), false));
	}
	
	private static String createReport(BundlableNode bundlableNode, List<LinkedAsset> linkedAssets, DependencyProvider dependencyProvider) throws ModelOperationException {
		StringBuilder stringBuilder = new StringBuilder();
		HashSet<LinkedAsset> processedAssets = new HashSet<>();
		MutableBoolean hasOmittedDependencies = new MutableBoolean(false);
		
		for(LinkedAsset linkedAsset : linkedAssets) {
			addDependency(bundlableNode, linkedAsset, dependencyProvider, stringBuilder, processedAssets, hasOmittedDependencies, 1);
		}
		
		if(hasOmittedDependencies.isTrue()) {
			stringBuilder.append("\n    (*) - dependencies omitted (listed previously)");
		}
		
		return stringBuilder.toString();
	}
	
	private static void addDependency(BundlableNode bundlableNode, LinkedAsset linkedAsset, DependencyProvider dependencyProvider, StringBuilder stringBuilder, Set<LinkedAsset> processedAssets, MutableBoolean hasOmittedDependencies, int indentLevel) throws ModelOperationException {
		appendAssetPath(linkedAsset, stringBuilder, indentLevel, dependencyProvider.areRootDependenciesSeeds() && (indentLevel == 1), processedAssets.contains(linkedAsset));
		
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : dependencyProvider.getDependencies(bundlableNode, linkedAsset)) {
				addDependency(bundlableNode, dependentAsset, dependencyProvider, stringBuilder, processedAssets, hasOmittedDependencies, indentLevel + 1);
			}
		}
		else {
			hasOmittedDependencies.setValue(true);
		}
	}
	
	private static void appendAssetPath(LinkedAsset linkedAsset, StringBuilder stringBuilder, int indentLevel, boolean isSeedFile, boolean alreadyProcessedDependency) {
		stringBuilder.append("    ");
		
		if(indentLevel == 1) {
			stringBuilder.append("+--- ");
		}
		else {
			for(int i = 0; i < indentLevel; ++i) {
				if(i == (indentLevel - 1)) {
					stringBuilder.append("\\--- ");
				}
				else {
					stringBuilder.append("|    ");
				}
			}
		}
		
		stringBuilder.append("'" + linkedAsset.getAssetPath() + "'");
		
		if(isSeedFile) {
			stringBuilder.append(" (seed file)");
		}
		else if(alreadyProcessedDependency) {
			stringBuilder.append(" (*)");
		}
		
		stringBuilder.append("\n");
	}
	
	private interface DependencyProvider {
		List<LinkedAsset> getDependencies(BundlableNode bundlableNode, LinkedAsset linkedAsset) throws ModelOperationException;
		boolean areRootDependenciesSeeds();
	}
	
	private static class MappedDependencyProvider implements DependencyProvider {
		private Map<LinkedAsset, Set<LinkedAsset>> dependencyMap;
		private boolean areRootDependenciesSeeds;
		
		public MappedDependencyProvider(Map<LinkedAsset, Set<LinkedAsset>> dependencyMap, boolean areRootDependenciesSeeds) throws ModelOperationException {
			this.dependencyMap = dependencyMap;
			this.areRootDependenciesSeeds = areRootDependenciesSeeds;
		}
		
		@Override
		public List<LinkedAsset> getDependencies(BundlableNode bundlableNode, LinkedAsset linkedAsset) {
			List<LinkedAsset> dependencies =  new ArrayList<>();
			
			if(dependencyMap.containsKey(linkedAsset)) {
				dependencies.addAll(dependencyMap.get(linkedAsset));
			}
			
			return dependencies;
		}
		
		@Override
		public boolean areRootDependenciesSeeds() {
			return areRootDependenciesSeeds;
		}
	}
}
