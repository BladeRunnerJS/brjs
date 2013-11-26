package org.bladerunnerjs.specutil.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.specutil.XmlBuilderSerializer;

import com.jamesmurty.utils.XMLBuilder;

public class AliasesFileBuilder {
	private AliasesFile aliasesFile;
	private BuilderChainer builderChainer;
	private List<AliasDefinition> aliases = new ArrayList<>();
	private String scenario = null;
	private String group = null;
	
	public AliasesFileBuilder(SpecTest specTest, AliasesFile aliasesFile) {
		this.aliasesFile = aliasesFile;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer usesScenario(String scenario) {
		this.scenario = scenario;
		
		return builderChainer;
	}
	
	public BuilderChainer usesGroup(String group) {
		this.group = group;
		
		return builderChainer;
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
		XMLBuilder builder = XMLBuilder.create("aliases").ns("http://schema.caplin.com/CaplinTrader/aliases");
		
		if(scenario != null) {
			builder.a("useScenario", scenario);
		}
		
		if(group != null) {
			builder.a("useGroups", group);
		}
		
		for(AliasDefinition aliasDefinition : aliases) {
			builder.e("alias").a("name", aliasDefinition.getName()).a("class", aliasDefinition.getClassName()).up();
		}
		
		FileUtils.write(aliasesFile, XmlBuilderSerializer.serialize(builder));
	}
}
