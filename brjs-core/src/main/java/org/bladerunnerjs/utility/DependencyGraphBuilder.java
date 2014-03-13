package org.bladerunnerjs.utility;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class DependencyGraphBuilder {
	public static String createDependencyGraph(Aspect aspect) throws ModelOperationException {
		return "Aspect '" + aspect.getName() + "' dependencies found:\n" + createDependencyGraph((BrowsableNode) aspect);
	}
	
	public static String createDependencyGraph(Workbench workbench) throws ModelOperationException {
		return "Workbench dependencies found:\n" + createDependencyGraph((BrowsableNode) workbench);
	}
	
	private static String createDependencyGraph(BrowsableNode browsableNode) throws ModelOperationException {
		StringBuilder stringBuilder = new StringBuilder();
		HashSet<LinkedAsset> processedAssets = new HashSet<>();
		MutableBoolean hasOmittedDependencies = new MutableBoolean(false);
		
		for(LinkedAsset seedAsset : browsableNode.seedFiles()) {
			addDependency(seedAsset, stringBuilder, browsableNode, processedAssets, 1, hasOmittedDependencies);
		}
		
		if(hasOmittedDependencies.isTrue()) {
			stringBuilder.append("\n    (*) - dependencies omitted (listed previously)");
		}
		
		return stringBuilder.toString();
	}
	
	private static void addDependency(LinkedAsset linkedAsset, StringBuilder stringBuilder, BundlableNode bundlableNode, Set<LinkedAsset> processedAssets, int indentLevel, MutableBoolean hasOmittedDependencies) throws ModelOperationException {
		appendAssetPath(linkedAsset, stringBuilder, indentLevel, (indentLevel == 1), processedAssets.contains(linkedAsset));
		
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : linkedAsset.getDependentSourceModules(bundlableNode)) {
				addDependency(dependentAsset, stringBuilder, bundlableNode, processedAssets, indentLevel + 1, hasOmittedDependencies);
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
}
