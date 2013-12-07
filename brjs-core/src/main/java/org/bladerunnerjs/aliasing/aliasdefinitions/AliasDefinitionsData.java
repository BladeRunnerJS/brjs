package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasOverride;

public class AliasDefinitionsData {
	public List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	public Map<String, Map<String, AliasOverride>> scenarioAliases = new HashMap<>();
	public Map<String, List<AliasOverride>> groupAliases = new HashMap<>();
	
	public Map<String, AliasOverride> getScenarioAliases(String aliasName) {
		if(!scenarioAliases.containsKey(aliasName)) {
			scenarioAliases.put(aliasName, new HashMap<String, AliasOverride>());
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
