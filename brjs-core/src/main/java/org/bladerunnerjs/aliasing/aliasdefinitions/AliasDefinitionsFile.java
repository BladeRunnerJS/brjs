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
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.FileModifiedChecker;

public class AliasDefinitionsFile {
	private final AliasDefinitionsData data = new AliasDefinitionsData();
	private final AliasDefinitionsReader reader;
	private final AliasDefinitionsWriter writer;
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	
	public AliasDefinitionsFile(AssetContainer assetContainer, File parent, String child) {
		try {
			file = new File(parent, child);
			fileModifiedChecker = new FileModifiedChecker(file);
			reader = new AliasDefinitionsReader(data, file, assetContainer);
			writer = new AliasDefinitionsWriter(data, file, assetContainer.root().bladerunnerConf().getDefaultInputEncoding());
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public File getUnderlyingFile() {
		return file;
	}
	
	public List<String> aliasNames() throws ContentFileProcessingException {
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
	
	public List<AliasDefinition> aliases() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.aliasDefinitions;
	}
	
	public void addScenarioAlias(String scenarioName, AliasOverride scenarioAlias) {
		data.getScenarioAliases(scenarioAlias.getName()).put(scenarioName, scenarioAlias);
	}
	
	public Map<String, AliasOverride> scenarioAliases(AliasDefinition alias) throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.scenarioAliases.get(alias.getName());
	}
	
	public void addGroupAliasOverride(String groupName, AliasOverride groupAlias) {
		data.getGroupAliases(groupName).add(groupAlias);
	}
	
	public Set<String> groupNames() throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return data.groupAliases.keySet();
	}
	
	public List<AliasOverride> groupAliases(String groupName) throws ContentFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reader.read();
		}
		
		return ((data.groupAliases.containsKey(groupName)) ? data.groupAliases.get(groupName) : new ArrayList<AliasOverride>());
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
						throw new AmbiguousAliasException(file, aliasName, scenarioName);
					}
					
					aliasDefinition = nextAliasDefinition;
				}
			}
		}
		catch(AmbiguousAliasException e) {
			throw new ContentFileProcessingException(file, e);
		}
		
		return aliasDefinition;
	}
	
	public AliasOverride getGroupOverride(String aliasName, List<String> groupNames) throws ContentFileProcessingException, AmbiguousAliasException {
		AliasOverride aliasOverride = null;
		
		for(String groupName : groupNames) {
			for(AliasOverride nextGroupAlias : groupAliases(groupName)) {
				if(nextGroupAlias.getName().equals(aliasName)) {
					if(aliasOverride != null) {
						throw new AmbiguousAliasException(file, aliasName, groupNames);
					}
					
					aliasOverride = nextGroupAlias;
				}
			}
		}
		
		return aliasOverride;
	}
	
	public void write() throws IOException {
		writer.write();
	}
}
