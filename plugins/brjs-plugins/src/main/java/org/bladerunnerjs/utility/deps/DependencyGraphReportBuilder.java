package org.bladerunnerjs.utility.deps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.BrowsableNode;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.model.DirectoryLinkedAsset;

public class DependencyGraphReportBuilder {
	private final List<LinkedAsset> linkedAssets;
	private final boolean showAllDependencies;
	private final DependencyInfo dependencyInfo;
	private final Set<LinkedAsset> manyLinkedAssets;
	private final StringBuilder reportBuilder;
	private final MutableBoolean hasOmittedDependencies;
	private BundleSet bundleSet;
	
	public static String createReport(BundlableNode bundlableNode, boolean showAllDependencies) throws ModelOperationException {
		return "Bundle '" + bundlableNode.root().dir().getRelativePath(bundlableNode.dir()) + "' dependencies found:\n" +
			new DependencyGraphReportBuilder(bundlableNode.getBundleSet(), bundlableNode.getBundleSet().seedAssets(), DependencyInfoFactory.buildForwardDependencyMap(bundlableNode), showAllDependencies).createReport();
	}
	
	public static String createReport(Aspect aspect, boolean showAllDependencies) throws ModelOperationException {
		return "Aspect '" + aspect.getName() + "' dependencies found:\n" +
			new DependencyGraphReportBuilder(aspect.getBundleSet(), aspect.getBundleSet().seedAssets(), DependencyInfoFactory.buildForwardDependencyMap(aspect), showAllDependencies).createReport();
	}
	
	public static String createReport(BladeWorkbench workbench, boolean showAllDependencies) throws ModelOperationException {
		return "Workbench dependencies found:\n" +
			new DependencyGraphReportBuilder(workbench.getBundleSet(), workbench.getBundleSet().seedAssets(), DependencyInfoFactory.buildForwardDependencyMap(workbench), showAllDependencies).createReport();
	}
	
	public static String createReport(BrowsableNode browsableNode, String requirePath, boolean showAllDependencies) throws ModelOperationException {
		try {
			SourceModule sourceModule =  (SourceModule)browsableNode.getLinkedAsset(requirePath);
			List<LinkedAsset> linkedAssets = new ArrayList<>();
			linkedAssets.add(sourceModule);
			
			return "Source module '" + sourceModule.getPrimaryRequirePath() + "' dependencies found:\n" +
			new DependencyGraphReportBuilder(browsableNode.getBundleSet(), linkedAssets, DependencyInfoFactory.buildReverseDependencyMap(browsableNode, sourceModule), showAllDependencies).createReport();
		}
		catch(RequirePathException e) {
			return e.getMessage();
		}
	}
	
	public static String createReportForRequirePrefix(BrowsableNode browsableNode, String requirePathPrefix, boolean showAllDependencies) throws ModelOperationException {
		List<LinkedAsset> linkedAssets = new ArrayList<>();
		
		for(SourceModule sourceModule : browsableNode.getBundleSet().getAssets(SourceModule.class)) {
			if(sourceModule.getPrimaryRequirePath().startsWith(requirePathPrefix)) {
				linkedAssets.add(sourceModule);
			}
		}
		
		return "Require path prefix '" + requirePathPrefix + "' dependencies found:\n" +
		new DependencyGraphReportBuilder(browsableNode.getBundleSet(), linkedAssets, DependencyInfoFactory.buildReverseDependencyMap(browsableNode, null), showAllDependencies).createReport();
	}
	
	private DependencyGraphReportBuilder(BundleSet bundleSet, List<LinkedAsset> linkedAssets, DependencyInfo dependencyInfo, boolean showAllDependencies) throws ModelOperationException {
		this.bundleSet = bundleSet;
		this.linkedAssets = linkedAssets;
		this.dependencyInfo = dependencyInfo;
		this.showAllDependencies = showAllDependencies;
		
		manyLinkedAssets = determineManyLinkedAssets(new LinkedHashSet<LinkedAsset>());
		reportBuilder = new StringBuilder();
		hasOmittedDependencies = new MutableBoolean(false);
	}
	
	private String createReport() throws ModelOperationException {
		HashSet<LinkedAsset> processedAssets = new LinkedHashSet<>();
		for(LinkedAsset linkedAsset : bundleSet.getAssets(LinkedAsset.class)) {
			addDependency(linkedAsset, null, processedAssets, 1);
		}
		
		if(!showAllDependencies && !manyLinkedAssets.isEmpty()) {
			reportBuilder.append("\n    (*) - subsequent instances not shown (use -A or --all to show)");
		}
		else if(showAllDependencies && hasOmittedDependencies.isTrue()) {
			reportBuilder.append("\n    (*) - dependencies omitted (listed previously)");
		}
		
		return reportBuilder.toString();
	}
	
	private Set<LinkedAsset> determineManyLinkedAssets(HashSet<LinkedAsset> processedAssets) {
		Set<LinkedAsset> manyLinkedAssets = new LinkedHashSet<>();
		for(LinkedAsset linkedAsset : linkedAssets) {
			buildManyLinkedAssets(linkedAsset, processedAssets, manyLinkedAssets);
		}
		
		return manyLinkedAssets;
	}
	
	private void buildManyLinkedAssets(LinkedAsset linkedAsset, Set<LinkedAsset> processedAssets, Set<LinkedAsset> manyLinkedAssets) {
		List<LinkedAsset> assetDependencies = getDependencies(linkedAsset);
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : assetDependencies) {
				buildManyLinkedAssets(dependentAsset, processedAssets, manyLinkedAssets);
			}
		}
		else {
			manyLinkedAssets.add(linkedAsset);
		}
	}
	
	private void addDependency(LinkedAsset linkedAsset, LinkedAsset referringAsset, Set<LinkedAsset> processedAssets, int indentLevel) throws ModelOperationException {
		if(showAllDependencies || !processedAssets.contains(linkedAsset)) {
			appendAssetPath(linkedAsset, referringAsset, indentLevel, processedAssets.contains(linkedAsset));
		}
		
		List<LinkedAsset> assetDependencies = getDependencies(linkedAsset);
		if(processedAssets.add(linkedAsset)) {
			for(LinkedAsset dependentAsset : assetDependencies) {
				addDependency(dependentAsset, linkedAsset, processedAssets, indentLevel + 1);
			}
		}
		else if(assetDependencies.size() > 0) {
			hasOmittedDependencies.setValue(true);
		}
	}
	
	private void appendAssetPath(LinkedAsset linkedAsset, LinkedAsset referringAsset, int indentLevel, boolean alreadyProcessedDependency) {
		
		
		if (linkedAsset instanceof DirectoryLinkedAsset) {
			return;
		}
		
		
		reportBuilder.append("    ");
		
		if(indentLevel == 1) {
			reportBuilder.append("+--- ");
		}
		else {
			for(int i = 0; i < indentLevel; ++i) {
				if(i == (indentLevel - 1)) {
					reportBuilder.append("\\--- ");
				}
				else {
					reportBuilder.append("|    ");
				}
			}
		}
		
		reportBuilder.append("'" + linkedAsset.getAssetPath() + "'");
		
		if(dependencyInfo.seedAssets.contains(linkedAsset)) {
			reportBuilder.append(" (seed file)");
		}
		else if((dependencyInfo.staticDeps.get(referringAsset) != null) && dependencyInfo.staticDeps.get(referringAsset).contains(linkedAsset)) {
			reportBuilder.append(" (static dep.)");
		}
		else if(linkedAsset.getPrimaryRequirePath().startsWith("alias!")) {
			reportBuilder.append(" (alias dep.)");
		}
		else if(linkedAsset.getPrimaryRequirePath().startsWith("service!")) {
			reportBuilder.append(" (service dep.)");
		}
		
		if((showAllDependencies && alreadyProcessedDependency) || (!showAllDependencies && manyLinkedAssets.contains(linkedAsset))) {
			reportBuilder.append(" (*)");
		}
		
		reportBuilder.append("\n");
	}
	
	private List<LinkedAsset> getDependencies(LinkedAsset linkedAsset) {
		List<LinkedAsset> dependencies =  new ArrayList<>();
		
		if(dependencyInfo.map.containsKey(linkedAsset)) {
			dependencies.addAll(dependencyInfo.map.get(linkedAsset));
		}
		
		return dependencies;
	}
}
