package org.bladerunnerjs.specutil.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;

public class AliasDefinitionsFileBuilder {
	private AliasDefinitionsFile aliasDefinitionsFile;
	private BuilderChainer builderChainer;
	private List<AliasDefinition> aliases = new ArrayList<>();
	
	public AliasDefinitionsFileBuilder(SpecTest specTest, AliasDefinitionsFile aliasDefinitionsFile) {
		this.aliasDefinitionsFile = aliasDefinitionsFile;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer exists() throws Exception {
		writeAliasesFile();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		aliases.add(new AliasDefinition(aliasName, classRef, null));
		writeAliasesFile();
		
		return builderChainer;
	}
	
	private void writeAliasesFile() throws Exception {
		StringBuilder aliasDefinitionsFileContents = new StringBuilder("<aliasDefinitions xmlns='http://schema.caplin.com/CaplinTrader/aliasDefinitions'>\n");
		
		for(AliasDefinition aliasDefinition : aliases) {
			aliasDefinitionsFileContents.append("\t<alias name='" + aliasDefinition.getName() + "' defaultClass='" + aliasDefinition.getClassName() + "'/>\n");
		}
		aliasDefinitionsFileContents.append("</aliasDefinitions>\n");
		
		FileUtils.write(aliasDefinitionsFile, aliasDefinitionsFileContents.toString());
	}
}
