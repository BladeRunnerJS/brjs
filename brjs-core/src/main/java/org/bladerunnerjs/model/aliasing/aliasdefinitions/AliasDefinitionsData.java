package org.bladerunnerjs.model.aliasing.aliasdefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasOverride;

public class AliasDefinitionsData {
	public List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	public Map<String, Map<String, AliasOverride>> scenarioAliases = new HashMap<>();
	public Map<String, List<AliasOverride>> groupAliases = new HashMap<>();
}
