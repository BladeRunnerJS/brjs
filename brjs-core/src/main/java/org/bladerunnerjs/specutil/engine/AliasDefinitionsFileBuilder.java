package org.bladerunnerjs.specutil.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasDefinitionsFile;
import org.bladerunnerjs.specutil.XmlBuilderSerializer;

import com.jamesmurty.utils.XMLBuilder;

public class AliasDefinitionsFileBuilder {
	private AliasDefinitionsFile aliasDefinitionsFile;
	private BuilderChainer builderChainer;
	private List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	private Map<String, List<AliasDefinition>> scenarioAliases = new HashMap<>();
	private Map<String, List<AliasDefinition>> groupAliases = new HashMap<>();
	
	public AliasDefinitionsFileBuilder(SpecTest specTest, AliasDefinitionsFile aliasDefinitionsFile) {
		this.aliasDefinitionsFile = aliasDefinitionsFile;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer exists() throws Exception {
		writeAliasDefinitionsFile();
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		aliasDefinitions.add(new AliasDefinition(aliasName, classRef, null));
		writeAliasDefinitionsFile();
		
		return builderChainer;
	}
	
	public BuilderChainer hasScenarioAlias(String scenarioName, String aliasName, String classRef) throws Exception {
		AliasDefinition aliasDefinition = new AliasDefinition(aliasName, classRef, null);
		aliasDefinition.setScenario(scenarioName);
		getScenarioAliases(aliasName).add(aliasDefinition);
		writeAliasDefinitionsFile();
		
		return builderChainer;
	}
	
	public BuilderChainer hasGroupAlias(String groupName, String aliasName, String classRef) throws Exception {
		AliasDefinition aliasDefinition = new AliasDefinition(aliasName, classRef, null);
		aliasDefinition.setGroup(groupName);
		getGroupAliases(groupName).add(aliasDefinition);
		writeAliasDefinitionsFile();
		
		return builderChainer;
	}
	
	private void writeAliasDefinitionsFile() throws Exception {
		XMLBuilder builder = XMLBuilder.create("aliasDefinitions").ns("http://schema.caplin.com/CaplinTrader/aliasDefinitions");
		
		for(AliasDefinition aliasDefinition : aliasDefinitions) {
			builder.e("alias").a("name", aliasDefinition.getName()).a("defaultClass", aliasDefinition.getClassName());
			
			for(AliasDefinition scenarioAliasDefinition : getScenarioAliases(aliasDefinition.getName())) {
				builder.e("scenario").a("name", scenarioAliasDefinition.getScenario()).a("class", scenarioAliasDefinition.getClassName()).up();
			}
			
			builder.up();
		}
		
		for(String groupName : groupAliases.keySet()) {
			builder.e("group").a("name", groupName);
			
			for(AliasDefinition groupAlias : groupAliases.get(groupName)) {
				builder.e("alias").a("name", groupAlias.getName()).a("defaultClass", groupAlias.getClassName()).up();
			}
			
			builder.up();
		}
		
		FileUtils.write(aliasDefinitionsFile, XmlBuilderSerializer.serialize(builder));
	}
	
	private List<AliasDefinition> getScenarioAliases(String aliasName) {
		if(!scenarioAliases.containsKey(aliasName)) {
			scenarioAliases.put(aliasName, new ArrayList<AliasDefinition>());
		}
		
		return scenarioAliases.get(aliasName);
	}
	
	private List<AliasDefinition> getGroupAliases(String groupName) {
		if(!groupAliases.containsKey(groupName)) {
			groupAliases.put(groupName, new ArrayList<AliasDefinition>());
		}
		
		return groupAliases.get(groupName);
	}
}
