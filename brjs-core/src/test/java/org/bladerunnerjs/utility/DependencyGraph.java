package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.SourceModule;

import com.google.common.base.Joiner;

public class DependencyGraph {
	private Map<SourceModule, List<SourceModule>> graph;

	public DependencyGraph(Map<SourceModule, List<SourceModule>> graph) {
		this.graph = graph;
	}

	public String dependenciesOf(FakeSourceModule sourceModule) {
		return Joiner.on(", ").join(requirePaths(graph.get(sourceModule)));
	}
	
	private List<String> requirePaths(List<SourceModule> sourceModules) {
		List<String> requirePaths = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			requirePaths.add(sourceModule.getPrimaryRequirePath());
		}
		
		return requirePaths;
	}
}
