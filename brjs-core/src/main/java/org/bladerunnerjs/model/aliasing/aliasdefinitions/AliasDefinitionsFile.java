package org.bladerunnerjs.model.aliasing.aliasdefinitions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasOverride;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;

public class AliasDefinitionsFile {
	private final AliasDefinitionsData data = new AliasDefinitionsData();
	private final AliasDefinitionsReader reader;
	private final AliasDefinitionsWriter writer;
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	
	public AliasDefinitionsFile(AssetContainer assetContainer, File parent, String child) {
		file = new File(parent, child);
		fileModifiedChecker = new FileModifiedChecker(file);
		reader = new AliasDefinitionsReader(data, file, assetContainer);
		writer = new AliasDefinitionsWriter(data, file);
	}
	
	public File getUnderlyingFile() {
		return file;
	}
	
	public List<String> aliasNames() throws BundlerFileProcessingException {
		List<String> aliasNames = new ArrayList<>();
		
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
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
	
	public void addAlias(AliasDefinition aliasDefinition) {
		data.aliasDefinitions.add(aliasDefinition);
	}
	
	public List<AliasDefinition> aliases() throws BundlerFileProcessingException {
		return data.aliasDefinitions;
	}
	
	public void addScenarioAlias(String scenarioName, AliasOverride scenarioAlias) {
		data.getScenarioAliases(scenarioAlias.getName()).put(scenarioName, scenarioAlias);
	}
	
	public Map<String, AliasOverride> scenarioAliases(AliasDefinition alias) throws BundlerFileProcessingException {
		return data.scenarioAliases.get(alias.getName());
	}
	
	public void addGroupAliasOverride(String groupName, AliasOverride groupAlias) {
		data.getGroupAliases(groupName).add(groupAlias);
	}
	
	public Set<String> groupNames() {
		return data.groupAliases.keySet();
	}
	
	public List<AliasOverride> groupAliases(String groupName) throws BundlerFileProcessingException {
		return ((data.groupAliases.containsKey(groupName)) ? data.groupAliases.get(groupName) : new ArrayList<AliasOverride>());
	}
	
	public AliasDefinition getAlias(String aliasName, String scenarioName, List<String> groupNames) throws BundlerFileProcessingException {
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
						throw new AmbiguousAliasException(file, aliasName, scenarioName);
					}
					
					aliasDefinition = nextAliasDefinition;
				}
			}
			
			for(String groupName : groupNames) {
				for(AliasOverride nextGroupAlias : groupAliases(groupName)) {
					if(nextGroupAlias.getName().equals(aliasName)) {
						if(aliasDefinition != null) {
							throw new AmbiguousAliasException(file, aliasName, scenarioName);
						}
						
						aliasDefinition = new AliasDefinition(nextGroupAlias.getName(), nextGroupAlias.getClassName(), null);
					}
				}
			}
		}
		catch(AmbiguousAliasException e) {
			throw new BundlerFileProcessingException(file, e);
		}
		
		return aliasDefinition;
	}
	
	public void write() throws IOException {
		writer.write();
	}
}
