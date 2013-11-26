package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;

public class AliasDefinitionsFileBuilder {
	private AliasDefinitionsFile aliasDefinitionsFile;
	private BuilderChainer builderChainer;
	
	public AliasDefinitionsFileBuilder(SpecTest specTest, AliasDefinitionsFile aliasDefinitionsFile) {
		this.aliasDefinitionsFile = aliasDefinitionsFile;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer exists() throws Exception {
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		aliasDefinitionsFile.addAliasDefinition(new AliasDefinition(aliasName, classRef, null));
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasScenarioAlias(String scenarioName, String aliasName, String classRef) throws Exception {
		AliasDefinition aliasDefinition = new AliasDefinition(aliasName, classRef, null);
		aliasDefinition.setScenario(scenarioName);
		
		aliasDefinitionsFile.addAliasDefinition(aliasDefinition);
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasGroupAlias(String groupName, String aliasName, String classRef) throws Exception {
		AliasDefinition aliasDefinition = new AliasDefinition(aliasName, classRef, null);
		aliasDefinition.setGroup(groupName);
		
		aliasDefinitionsFile.addAliasDefinition(aliasDefinition);
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
}
