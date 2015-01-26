package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.BundlableNode;

public class DefineTimeDependencyGraphCreator {
	public static Map<SourceModule, List<SourceModule>> createGraph(BundlableNode bundlableNode, Set<SourceModule> sourceModules, boolean isPreExport) throws ModelOperationException {
		Map<SourceModule, List<SourceModule>> dependencyGraph = new HashMap<>();
		
		for(SourceModule sourceModule : sourceModules) {
			List<Asset> dependentAssets = (isPreExport) ? sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode) : sourceModule.getPostExportDefineTimeDependentAssets(bundlableNode);
			dependencyGraph.put(sourceModule, extractSourceModules(dependentAssets));
		}
		
		return dependencyGraph;
	}
	
	private static List<SourceModule> extractSourceModules(List<Asset> assets){
		List<SourceModule> sourceModules = new ArrayList<SourceModule>();
		for(Asset asset : assets){
			if(asset instanceof SourceModule){
				sourceModules.add((SourceModule)asset);
			}
		}
		return sourceModules;
	}
}