package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasOverride;
import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;

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
	
	public BuilderChainer hasAlias(String aliasName, String classRef, String interfaceRef) throws Exception {
		aliasDefinitionsFile.addAlias(new AliasDefinition(aliasName, classRef, interfaceRef));
		aliasDefinitionsFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		return hasAlias(aliasName, classRef, null);
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
