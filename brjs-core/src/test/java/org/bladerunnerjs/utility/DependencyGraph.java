package org.bladerunnerjs.utility;

import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.SourceModule;

import com.google.common.base.Joiner;

public class DependencyGraph {
	
	private Map<String, List<String>> graph;
	Map<String,SourceModule> allSourceModules;

	public DependencyGraph(Map<String, List<String>> graph) {
		this.graph = graph;
	}

	public String dependenciesOf(FakeSourceModule sourceModule) {
		return Joiner.on(", ").join(graph.get(sourceModule.getPrimaryRequirePath()));
	}
	
}
