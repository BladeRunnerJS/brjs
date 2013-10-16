package com.caplin.cutlass.bundler.js.aliasing;

import org.bladerunnerjs.model.AliasDefinition;

public class AliasInformation {

	private String aliasName;
	private AliasDefinition aliasDefinition;
	private ScenarioAliases scenarioAliases;

	public AliasInformation(String aliasName, AliasDefinition aliasDefinition, ScenarioAliases scenarioAliases) {
		this.aliasName = aliasName;
		this.aliasDefinition = aliasDefinition;
		this.scenarioAliases = scenarioAliases;
	}

	public String getAliasName() {
		return aliasName;
	}

	public AliasDefinition getAliasDefinition() {
		return aliasDefinition;
	}

	public ScenarioAliases getScenarioAliases() {
		return scenarioAliases;
	}
}
