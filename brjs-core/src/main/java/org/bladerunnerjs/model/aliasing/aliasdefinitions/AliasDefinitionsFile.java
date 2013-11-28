package org.bladerunnerjs.model.aliasing.aliasdefinitions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasOverride;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.specutil.XmlBuilderSerializer;

import com.jamesmurty.utils.XMLBuilder;

public class AliasDefinitionsFile {
	private final AliasDefinitionsData data = new AliasDefinitionsData();
	private final AliasDefinitionsReader reader;
	private final File file;
	private final FileModifiedChecker fileModifiedChecker;
	
	public AliasDefinitionsFile(AssetContainer assetContainer, File parent, String child) {
		file = new File(parent, child);
		fileModifiedChecker = new FileModifiedChecker(file);
		reader = new AliasDefinitionsReader(data, file, assetContainer);
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
		try {
			XMLBuilder builder = XMLBuilder.create("aliasDefinitions").ns("http://schema.caplin.com/CaplinTrader/aliasDefinitions");
			
			for(AliasDefinition aliasDefinition : data.aliasDefinitions) {
				XMLBuilder aliasBuilder = builder.e("alias").a("name", aliasDefinition.getName()).a("defaultClass", aliasDefinition.getClassName());
				Map<String, AliasOverride> scenarioAliases = data.getScenarioAliases(aliasDefinition.getName());
				
				for(String scenarioName : scenarioAliases.keySet()) {
					AliasOverride scenarioAlias = scenarioAliases.get(scenarioName);
					aliasBuilder.e("scenario").a("name", scenarioName).a("class", scenarioAlias.getClassName());
				}
			}
			
			for(String groupName : data.groupAliases.keySet()) {
				XMLBuilder groupBuilder = builder.e("group").a("name", groupName);
				
				for(AliasOverride groupAlias : data.groupAliases.get(groupName)) {
					groupBuilder.e("alias").a("name", groupAlias.getName()).a("class", groupAlias.getClassName());
				}
			}
			
			FileUtils.write(file, XmlBuilderSerializer.serialize(builder));
		}
		catch(IOException | ParserConfigurationException | FactoryConfigurationError | TransformerException e) {
			throw new IOException(e);
		}
	}
}
