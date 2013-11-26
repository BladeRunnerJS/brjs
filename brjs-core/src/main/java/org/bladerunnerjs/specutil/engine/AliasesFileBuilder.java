package org.bladerunnerjs.specutil.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.specutil.XmlBuilderSerializer;

import com.google.common.base.Joiner;
import com.jamesmurty.utils.XMLBuilder;

public class AliasesFileBuilder {
	private AliasesFile aliasesFile;
	private BuilderChainer builderChainer;
	private List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	private String scenario = null;
	private List<String> groupNames = null;
	
	public AliasesFileBuilder(SpecTest specTest, AliasesFile aliasesFile) {
		this.aliasesFile = aliasesFile;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer usesScenario(String scenario) {
		this.scenario = scenario;
		
		return builderChainer;
	}
	
	public BuilderChainer usesGroups(String... groups) {
		this.groupNames = Arrays.asList(groups);
		
		return builderChainer;
	}
	
	public BuilderChainer exists() throws Exception {
		writeAliasesFile();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		aliasDefinitions.add(new AliasDefinition(aliasName, classRef, null));
		writeAliasesFile();
		
		return builderChainer;
	}
	
	private void writeAliasesFile() throws Exception {
		XMLBuilder builder = XMLBuilder.create("aliases").ns("http://schema.caplin.com/CaplinTrader/aliases");
		
		if(scenario != null) {
			builder.a("useScenario", scenario);
		}
		
		if(groupNames != null) {
			builder.a("useGroups", Joiner.on(" ").join(groupNames));
		}
		
		for(AliasDefinition aliasDefinition : aliasDefinitions) {
			builder.e("alias").a("name", aliasDefinition.getName()).a("class", aliasDefinition.getClassName()).up();
		}
		
		FileUtils.write(aliasesFile, XmlBuilderSerializer.serialize(builder));
	}
}
