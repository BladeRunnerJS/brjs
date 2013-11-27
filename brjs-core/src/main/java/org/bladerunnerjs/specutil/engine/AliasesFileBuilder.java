package org.bladerunnerjs.specutil.engine;

import java.util.Arrays;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasesFile;

public class AliasesFileBuilder {
	private AliasesFile aliasesFile;
	private BuilderChainer builderChainer;
	
	public AliasesFileBuilder(SpecTest specTest, AliasesFile aliasesFile) {
		this.aliasesFile = aliasesFile;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer exists() throws Exception {
		aliasesFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer usesScenario(String scenario) throws Exception {
		aliasesFile.setScenarioName(scenario);
		aliasesFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer usesGroups(String... groups) throws Exception {
		aliasesFile.setGroupNames(Arrays.asList(groups));
		aliasesFile.write();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		aliasesFile.addAlias(new AliasDefinition(aliasName, classRef, null));
		aliasesFile.write();
		
		return builderChainer;
	}
}
