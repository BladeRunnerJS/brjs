package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.AliasOverride;

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
		aliasDefinitionsFile.addAlias(new AliasDefinition(aliasName, classRef, null));
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasScenarioAlias(String scenarioName, String aliasName, String classRef) throws Exception {
		aliasDefinitionsFile.addScenarioAlias(scenarioName, new AliasOverride(aliasName, classRef));
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasGroupAlias(String groupName, String aliasName, String classRef) throws Exception {
		aliasDefinitionsFile.addGroupAliasOverride(groupName, new AliasOverride(aliasName, classRef));
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
}
