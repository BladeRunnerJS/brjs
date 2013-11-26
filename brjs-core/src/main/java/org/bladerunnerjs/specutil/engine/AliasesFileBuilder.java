package org.bladerunnerjs.specutil.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasesFile;

public class AliasesFileBuilder {
	private AliasesFile aliasesFile;
	private BuilderChainer builderChainer;
	private List<AliasDefinition> aliases = new ArrayList<>();
	
	public AliasesFileBuilder(SpecTest specTest, AliasesFile aliasesFile) {
		this.aliasesFile = aliasesFile;
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
		StringBuilder aliasesFileContents = new StringBuilder("<aliases xmlns='http://schema.caplin.com/CaplinTrader/aliases'>\n");
		
		for(AliasDefinition aliasDefinition : aliases) {
			aliasesFileContents.append("\t<alias name='" + aliasDefinition.getName() + "' class='" + aliasDefinition.getClassName() + "'/>\n");
		}
		aliasesFileContents.append("</aliases>\n");
		
		FileUtils.write(aliasesFile, aliasesFileContents.toString());
	}
}
