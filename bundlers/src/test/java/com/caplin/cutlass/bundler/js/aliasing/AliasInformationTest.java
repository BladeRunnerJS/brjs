package com.caplin.cutlass.bundler.js.aliasing;

import static org.junit.Assert.*;

import org.junit.Test;
import org.bladerunnerjs.model.aliasing.AliasDefinition;

public class AliasInformationTest {

	private ScenarioAliases scenarioAliases = new ScenarioAliases();
	private AliasDefinition aliasDefinition = new AliasDefinition( null, "className", "interfaceName" );
	private AliasInformation aliasInformation = new AliasInformation( "alias", aliasDefinition, scenarioAliases);
	
	@Test
	public void getAliasName() {
		assertEquals( "alias", aliasInformation.getAliasName() );
	}
	
	@Test
	public void getAliasDefinition() {
		assertEquals( aliasDefinition, aliasInformation.getAliasDefinition() );
	}
	
	@Test
	public void getScenarioAliases() {
		assertEquals( scenarioAliases, aliasInformation.getScenarioAliases() );
	}
}
