package com.caplin.cutlass.bundler.js.aliasing;

import java.util.LinkedHashMap;
import java.util.Map;

public class Aliases
{
	Map<String, ScenarioAliases> aliases = new LinkedHashMap<>();
	
	public boolean hasScenario(String scenarioName)
	{
		return aliases.containsKey(scenarioName);
	}
	
	public void addScenario(String scenarioName)
	{
		aliases.put(scenarioName, new ScenarioAliases());
	}

	public ScenarioAliases getScenarioAliases(String scenarioName)
	{
		return aliases.get(scenarioName);
	}
}
