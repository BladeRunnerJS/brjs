package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		return "Aspect '" + aspect.getName() + "' dependencies found:\n" +
			createReport(aspect, aspect.seedFiles(), DependencyInfoFactory.buildForwardDependencyMap(aspect), true);
	}
	
	public static String createReport(Workbench workbench) throws ModelOperationException {
		return "Workbench dependencies found:\n" +
			createReport(workbench, workbench.seedFiles(), DependencyInfoFactory.buildForwardDependencyMap(workbench), true);
	}
	
	public static String createReport(BrowsableNode browsableNode, String requirePath) throws ModelOperationException, RequirePathException {
		SourceModule sourceModule = browsableNode.getSourceModule(requirePath);
		List<LinkedAsset> linkedAssets = new ArrayList<>();
		linkedAssets.add(sourceModule);
		
		return "Source module '" + sourceModule.getRequirePath() + "' dependencies found:\n" +
			createReport(browsableNode, linkedAssets, DependencyInfoFactory.buildReverseDependencyMap(browsableNode), false);
	}
	
	private static String createReport(BundlableNode bundlableNode, List<LinkedAsset> linkedAssets, DependencyInfo dependencyInfo, boolean isSeed) throws ModelOperationException {
		StringBuilder stringBuilder = new StringBuilder();
		HashSet<LinkedAsset> processedAssets = new HashSet<>();
		MutableBoolean hasOmittedDependencies = new MutableBoolean(false);
		
		for(LinkedAsset linkedAsset : linkedAssets) {
			addDependency(bundlableNode, linkedAsset, dependencyInfo, stringBuilder, processedAssets, hasOmittedDependencies, 1, isSeed);
		}
		
		if(hasOmittedDependencies.isTrue()) {
			stringBuilder.append("\n    (*) - dependencies omitted (listed previously)");
		}
		
		return stringBuilder.toString();
	}
	
	private static void addDependency(BundlableNode bundlableNode, LinkedAsset linkedAsset, DependencyInfo dependencyInfo, StringBuilder stringBuilder, Set<LinkedAsset> processedAssets, MutableBoolean hasOmittedDependencies, int indentLevel, boolean isSeed) throws ModelOperationException {
		appendAssetPath(linkedAsset, dependencyInfo, stringBuilder, indentLevel, isSeed && (indentLevel == 1), processedAssets.contains(linkedAsset));
		
		List<LinkedAsset> assetDependencies = getDependencies(bundlableNode, linkedAsset, dependencyInfo);
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : assetDependencies) {
				addDependency(bundlableNode, dependentAsset, dependencyInfo, stringBuilder, processedAssets, hasOmittedDependencies, indentLevel + 1, isSeed);
			}
		}
		else if(assetDependencies.size() > 0) {
			hasOmittedDependencies.setValue(true);
		}
	}
	
	private static void appendAssetPath(LinkedAsset linkedAsset, DependencyInfo dependencyInfo, StringBuilder stringBuilder, int indentLevel, boolean isSeedFile, boolean alreadyProcessedDependency) {
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
		
		if(dependencyInfo.seedAssets.contains(linkedAsset)) {
			stringBuilder.append(" (seed file)");
		}
		else if(dependencyInfo.resourceAssets.contains(linkedAsset)) {
			stringBuilder.append(" (implicit resource)");
		}
		else if(linkedAsset instanceof AliasAsset) {
			stringBuilder.append(" (alias dep.)");
		}
		
		if(alreadyProcessedDependency) {
			stringBuilder.append(" (*)");
		}
		
		stringBuilder.append("\n");
	}
	
	private static List<LinkedAsset> getDependencies(BundlableNode bundlableNode, LinkedAsset linkedAsset, DependencyInfo dependencyInfo) {
		List<LinkedAsset> dependencies =  new ArrayList<>();
		
		if(dependencyInfo.map.containsKey(linkedAsset)) {
			dependencies.addAll(dependencyInfo.map.get(linkedAsset));
		}
		
		return dependencies;
	}
}
