package com.caplin.cutlass.bundler.js.aliasing;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;

import org.bladerunnerjs.model.AliasDefinition;

public class ScenarioAliases
{
	private Map<String, AliasDefinition> aliases = new LinkedHashMap<>();
	
	public Set<String> getAliasNames()
	{
		return aliases.keySet();
	}
	
	public boolean hasAlias(String aliasName)
	{
		return aliases.containsKey(aliasName);
	}
	
	public AliasDefinition getAlias(String aliasName)
	{
		return aliases.get(aliasName);
	}
	
	public void addAlias(String aliasName, AliasDefinition aliasDefinition)
	{
		aliases.put(aliasName, aliasDefinition);
	}
}
