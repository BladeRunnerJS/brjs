package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class DependencyGraphReportBuilder {
	public static String createReport(Aspect aspect, boolean showAllDependencies) throws ModelOperationException {
		return "Aspect '" + aspect.getName() + "' dependencies found:\n" +
			createReport(aspect, aspect.seedFiles(), DependencyInfoFactory.buildForwardDependencyMap(aspect), showAllDependencies);
	}
	
	public static String createReport(Workbench workbench, boolean showAllDependencies) throws ModelOperationException {
		return "Workbench dependencies found:\n" +
			createReport(workbench, workbench.seedFiles(), DependencyInfoFactory.buildForwardDependencyMap(workbench), showAllDependencies);
	}
	
	public static String createReport(BrowsableNode browsableNode, String requirePath, boolean showAllDependencies) throws ModelOperationException {
		try {
			SourceModule sourceModule = browsableNode.getSourceModule(requirePath);
			List<LinkedAsset> linkedAssets = new ArrayList<>();
			linkedAssets.add(sourceModule);
			
			return "Source module '" + sourceModule.getRequirePath() + "' dependencies found:\n" +
				createReport(browsableNode, linkedAssets, DependencyInfoFactory.buildReverseDependencyMap(browsableNode), showAllDependencies);
		}
		catch(RequirePathException e) {
			return e.getMessage();
		}
	}
	
	public static String createReportForAlias(BrowsableNode browsableNode, String aliasName, boolean showAllDependencies) throws ModelOperationException {
		try {
			AliasDefinition alias = browsableNode.getAlias(aliasName);
			SourceModule sourceModule = browsableNode.getSourceModule(alias.getRequirePath());
			List<LinkedAsset> linkedAssets = new ArrayList<>();
			linkedAssets.add(sourceModule);
			
			return "Alias '" + aliasName + "' dependencies found:\n" +
				createReport(browsableNode, linkedAssets, DependencyInfoFactory.buildReverseDependencyMap(browsableNode), showAllDependencies);
		}
		catch(AliasException | RequirePathException e) {
			return e.getMessage();
		}
		catch(ContentFileProcessingException e) {
			throw new ModelOperationException(e);
		}
	}
	
	private static String createReport(BundlableNode bundlableNode, List<LinkedAsset> linkedAssets, DependencyInfo dependencyInfo, boolean showAllDependencies) throws ModelOperationException {
		StringBuilder stringBuilder = new StringBuilder();
		HashSet<LinkedAsset> processedAssets = new HashSet<>();
		MutableBoolean hasOmittedDependencies = new MutableBoolean(false);
		Set<LinkedAsset> manyLinkedAssets = determineManyLinkedAssets(bundlableNode, linkedAssets, dependencyInfo, new HashSet<LinkedAsset>());
		
		for(LinkedAsset linkedAsset : linkedAssets) {
			addDependency(bundlableNode, linkedAsset, dependencyInfo, stringBuilder, manyLinkedAssets, processedAssets, hasOmittedDependencies, showAllDependencies, 1);
		}
		
		if(!showAllDependencies && !manyLinkedAssets.isEmpty()) {
			stringBuilder.append("\n    (*) - subsequent instances not shown (use -A or --all to show)");
		}
		else if(showAllDependencies && hasOmittedDependencies.isTrue()) {
			stringBuilder.append("\n    (*) - dependencies omitted (listed previously)");
		}
		
		return stringBuilder.toString();
	}

	private static Set<LinkedAsset> determineManyLinkedAssets(BundlableNode bundlableNode, List<LinkedAsset> linkedAssets,
		DependencyInfo dependencyInfo, HashSet<LinkedAsset> processedAssets) {
		Set<LinkedAsset> manyLinkedAssets = new HashSet<>();
		
		for(LinkedAsset linkedAsset : linkedAssets) {
			buildManyLinkedAssets(bundlableNode, linkedAsset, dependencyInfo, manyLinkedAssets, processedAssets);
		}
		return manyLinkedAssets;
	}
	
	private static void buildManyLinkedAssets(BundlableNode bundlableNode, LinkedAsset linkedAsset, DependencyInfo dependencyInfo, Set<LinkedAsset> manyLinkedAssets, Set<LinkedAsset> processedAssets) {
		List<LinkedAsset> assetDependencies = getDependencies(bundlableNode, linkedAsset, dependencyInfo);
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : assetDependencies) {
				buildManyLinkedAssets(bundlableNode, dependentAsset, dependencyInfo, manyLinkedAssets, processedAssets);
			}
		}
		else {
			manyLinkedAssets.add(linkedAsset);
		}
	}
	
	private static void addDependency(BundlableNode bundlableNode, LinkedAsset linkedAsset, DependencyInfo dependencyInfo, StringBuilder stringBuilder, Set<LinkedAsset> manyLinkedAssets, Set<LinkedAsset> processedAssets, MutableBoolean hasOmittedDependencies, boolean showAllDependencies, int indentLevel) throws ModelOperationException {
		if(showAllDependencies || !processedAssets.contains(linkedAsset)) {
			appendAssetPath(linkedAsset, dependencyInfo, stringBuilder, indentLevel, manyLinkedAssets, processedAssets.contains(linkedAsset), showAllDependencies);
		}
		
		List<LinkedAsset> assetDependencies = getDependencies(bundlableNode, linkedAsset, dependencyInfo);
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : assetDependencies) {
				addDependency(bundlableNode, dependentAsset, dependencyInfo, stringBuilder, manyLinkedAssets, processedAssets, hasOmittedDependencies, showAllDependencies, indentLevel + 1);
			}
		}
		else if(assetDependencies.size() > 0) {
			hasOmittedDependencies.setValue(true);
		}
	}
	
	private static void appendAssetPath(LinkedAsset linkedAsset, DependencyInfo dependencyInfo, StringBuilder stringBuilder, int indentLevel, Set<LinkedAsset> manyLinkedAssets, boolean alreadyProcessedDependency, boolean showAllDependencies) {
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
		
		if((showAllDependencies && alreadyProcessedDependency) || (!showAllDependencies && manyLinkedAssets.contains(linkedAsset))) {
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
