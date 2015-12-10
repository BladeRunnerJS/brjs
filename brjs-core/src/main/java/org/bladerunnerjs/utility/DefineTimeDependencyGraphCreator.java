package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;

public class DefineTimeDependencyGraphCreator {
	
	public static Map<String, List<String>> createGraph(BundlableNode bundlableNode, Map<String,SourceModule> sourceModules, boolean isPreExport) throws ModelOperationException {
		Map<String, List<String>> dependencyGraph = new LinkedHashMap<>();
		
		for (String sourceModuleRequirePath : sourceModules.keySet()) {
			SourceModule sourceModule = sourceModules.get(sourceModuleRequirePath);
			List<Asset> dependentAssets = (isPreExport) ? sourceModule.getPreExportDefineTimeDependentAssets(bundlableNode) : sourceModule.getPostExportDefineTimeDependentAssets(bundlableNode);
			dependencyGraph.put(sourceModuleRequirePath, extractSourceRequirePaths(dependentAssets));
		}
		
		return dependencyGraph;
	}
	
	private static List<String> extractSourceRequirePaths(List<Asset> assets){
		List<String> sourceModules = new ArrayList<String>();
		for(Asset asset : assets){
			if(asset instanceof SourceModule){
				sourceModules.add(asset.getPrimaryRequirePath());
			}
		}
		return sourceModules;
	}
	
}