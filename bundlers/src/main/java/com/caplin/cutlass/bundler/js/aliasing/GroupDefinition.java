package com.caplin.cutlass.bundler.js.aliasing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;


public class GroupDefinition implements AliasContainer{

	private String name;
	private Map<String, List<AliasDefinition>> aliasDefinitionsInScenario = new HashMap<String, List<AliasDefinition>>();
	
	public GroupDefinition(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public List<AliasDefinition> getAliasDefinitions(String scenarioName) {
		//TODO is it necessary to check if scenarioName is not in the map?
		return aliasDefinitionsInScenario.get(scenarioName);
	}

	@Override
	public void addClassAlias(AliasDefinition alias, String scenario) throws ContentProcessingException {
		if (!aliasDefinitionsInScenario.containsKey(scenario))
		{
			aliasDefinitionsInScenario.put(scenario, new ArrayList<AliasDefinition>());
		}
		aliasDefinitionsInScenario.get(scenario).add(alias);
	}
}
