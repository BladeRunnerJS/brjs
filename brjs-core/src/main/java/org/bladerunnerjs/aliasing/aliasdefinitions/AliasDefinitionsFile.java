package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

public class AliasDefinitionsFile {
	private AliasDefinitionsData data = new AliasDefinitionsData();
	private final File aliasDefinitionsFile;
	private final PersistentAliasDefinitionsData persistentAliasDefinitionsData;
	
	public AliasDefinitionsFile(AssetLocation assetLocation, File parent, String child) {
		aliasDefinitionsFile = new File(parent, child);
		persistentAliasDefinitionsData = new PersistentAliasDefinitionsData(assetLocation, aliasDefinitionsFile);
	}
	
	public File getUnderlyingFile() {
		return aliasDefinitionsFile;
	}
	
	public List<String> aliasNames() throws ContentFileProcessingException {
		AliasDefinitionsData data = persistentAliasDefinitionsData.getData();
		List<String> aliasNames = new ArrayList<>();
		
		for(AliasDefinition aliasDefinition : data.aliasDefinitions) {
			aliasNames.add(aliasDefinition.getName());
		}
		
		for(Map<String, AliasOverride> aliasScenarioAliases : data.scenarioAliases.values()) {
			for(AliasOverride scenarioAlias : aliasScenarioAliases.values()) {
				aliasNames.add(scenarioAlias.getName());
			}
		}
		
		for(List<AliasOverride> groupAliasList : data.groupAliases.values()) {
			for(AliasOverride groupAlias : groupAliasList) {
				aliasNames.add(groupAlias.getName());
			}
		}
		
		return aliasNames;
	}
	
	public void addAlias(AliasDefinition aliasDefinition) throws ContentFileProcessingException {
		persistentAliasDefinitionsData.getData().aliasDefinitions.add(aliasDefinition);
	}
	
	public List<AliasDefinition> aliases() throws ContentFileProcessingException {
		return persistentAliasDefinitionsData.getData().aliasDefinitions;
	}
	
	public void addScenarioAlias(String scenarioName, AliasOverride scenarioAlias) throws ContentFileProcessingException {
		persistentAliasDefinitionsData.getData().getScenarioAliases(scenarioAlias.getName()).put(scenarioName, scenarioAlias);
	}
	
	public Map<String, AliasOverride> scenarioAliases(AliasDefinition alias) throws ContentFileProcessingException {
		return persistentAliasDefinitionsData.getData().scenarioAliases.get(alias.getName());
	}
	
	public void addGroupAliasOverride(String groupName, AliasOverride groupAlias) throws ContentFileProcessingException {
		persistentAliasDefinitionsData.getData().getGroupAliases(groupName).add(groupAlias);
	}
	
	public Set<String> groupNames() throws ContentFileProcessingException {
		return persistentAliasDefinitionsData.getData().groupAliases.keySet();
	}
	
	public List<AliasOverride> groupAliases(String groupName) throws ContentFileProcessingException {
		return ((persistentAliasDefinitionsData.getData().groupAliases.containsKey(groupName)) ? persistentAliasDefinitionsData.getData().groupAliases.get(groupName) : new ArrayList<AliasOverride>());
	}
	
	public AliasDefinition getAliasDefinition(String aliasName, String scenarioName, List<String> groupNames) throws ContentFileProcessingException {
		AliasDefinition aliasDefinition = null;
		
		try {
			for(AliasDefinition nextAliasDefinition : aliases()) {
				if(nextAliasDefinition.getName().equals(aliasName)) {
					if(scenarioName != null) {
						AliasOverride scenarioAlias = data.getScenarioAliases(nextAliasDefinition.getName()).get(scenarioName);
						
						if(scenarioAlias != null) {
							nextAliasDefinition = new AliasDefinition(nextAliasDefinition.getName(), scenarioAlias.getClassName(), nextAliasDefinition.getInterfaceName());
						}
					}
					
					if(aliasDefinition != null) {
						throw new AmbiguousAliasException(aliasDefinitionsFile, aliasName, scenarioName);
					}
					
					aliasDefinition = nextAliasDefinition;
				}
			}
		}
		catch(AmbiguousAliasException e) {
			throw new ContentFileProcessingException(aliasDefinitionsFile, e);
		}
		
		return aliasDefinition;
	}
	
	public AliasOverride getGroupOverride(String aliasName, List<String> groupNames) throws ContentFileProcessingException, AmbiguousAliasException {
		AliasOverride aliasOverride = null;
		
		for(String groupName : groupNames) {
			for(AliasOverride nextGroupAlias : groupAliases(groupName)) {
				if(nextGroupAlias.getName().equals(aliasName)) {
					if(aliasOverride != null) {
						throw new AmbiguousAliasException(aliasDefinitionsFile, aliasName, groupNames);
					}
					
					aliasOverride = nextGroupAlias;
				}
			}
		}
		
		return aliasOverride;
	}
	
	public void write() throws IOException, ContentFileProcessingException {
		persistentAliasDefinitionsData.writeData();
	}
}
