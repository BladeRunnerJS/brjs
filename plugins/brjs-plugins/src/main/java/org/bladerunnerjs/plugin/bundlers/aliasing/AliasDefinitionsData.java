package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AliasDefinitionsData {
	public List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	public Map<String, Map<String, AliasOverride>> scenarioAliases = new LinkedHashMap<>();
	public Map<String, List<AliasOverride>> groupAliases = new LinkedHashMap<>();
	
	public AliasDefinitionsData() {
		// do nothing
	}
	
	public AliasDefinitionsData(AliasDefinitionsData aliasDefinitionsData) {
		aliasDefinitions.addAll(aliasDefinitionsData.aliasDefinitions);
		scenarioAliases.putAll(aliasDefinitionsData.scenarioAliases);
		groupAliases.putAll(aliasDefinitionsData.groupAliases);
	}
	
	public boolean equals(AliasDefinitionsData aliasDefinitionsData) {
		return aliasDefinitions.equals(aliasDefinitionsData) && scenarioAliases.equals(aliasDefinitionsData.scenarioAliases) && groupAliases.equals(aliasDefinitionsData.groupAliases);
	}
	
	public Map<String, AliasOverride> getScenarioAliases(String aliasName) {
		if(!scenarioAliases.containsKey(aliasName)) {
			scenarioAliases.put(aliasName, new LinkedHashMap<String, AliasOverride>());
		}
		
		return scenarioAliases.get(aliasName);
	}
	
	public List<AliasOverride> getGroupAliases(String groupName) {
		if(!groupAliases.containsKey(groupName)) {
			groupAliases.put(groupName, new ArrayList<AliasOverride>());
		}
		
		return groupAliases.get(groupName);
	}
}
