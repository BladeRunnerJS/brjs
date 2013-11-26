package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.stax.XmlStreamReader;
import org.bladerunnerjs.model.utility.XmlStreamReaderFactory;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

public class AliasDefinitionsFile extends File {
	private static final long serialVersionUID = 1L;
	private static XMLValidationSchema aliasDefinitionsSchema;
	
	private final FileModifiedChecker fileModifiedChecker;
	private List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	private Map<String, List<AliasDefinition>> scenarioAliases = new HashMap<>();
	private Map<String, List<AliasDefinition>> groupAliases = new HashMap<>();
	private AssetContainer assetContainer;
	
	static {
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try
		{
			aliasDefinitionsSchema = schemaFactory.createSchema(SchemaConverter.convertToRng("org/bladerunnerjs/model/aliasing/aliasDefinitions.rnc"));
		}
		catch (XMLStreamException | SchemaCreationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public AliasDefinitionsFile(AssetContainer assetContainer, File parent, String child) {
		super(parent, child);
		this.assetContainer = assetContainer;
		fileModifiedChecker = new FileModifiedChecker(this);
	}
	
	public AliasDefinition getAlias(AliasName aliasName, String scenarioName, List<String> groupNames) throws BundlerFileProcessingException {
		AliasDefinition aliasDefinition = null;
		
		for(AliasDefinition nextAliasDefinition : aliasDefinitions()) {
			String groupName = nextAliasDefinition.getGroup();
			boolean isValidScenario = (scenarioName == null) || nextAliasDefinition.getScenario().equals(scenarioName);
			boolean isValidGroup = (groupName == null) || groupNames.contains(groupName);
			
			if(isValidScenario && isValidGroup && nextAliasDefinition.getName().equals(aliasName.getName())) {
				aliasDefinition = nextAliasDefinition;
				break;
			}
		}
		
		return aliasDefinition;
	}
	
	public List<AliasDefinition> aliasDefinitions() throws BundlerFileProcessingException {
		List<AliasDefinition> aliasDefinitions = new ArrayList<>();
		
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		aliasDefinitions.addAll(this.aliasDefinitions);
		
		for(List<AliasDefinition> scenarioAliasList : scenarioAliases.values()) {
			aliasDefinitions.addAll(scenarioAliasList);
		}
		
		for(List<AliasDefinition> groupAliasList : groupAliases.values()) {
			aliasDefinitions.addAll(groupAliasList);
		}
		
		return aliasDefinitions;
	}
	
	private void reparseFile() throws BundlerFileProcessingException {
		aliasDefinitions = new ArrayList<>();
		scenarioAliases = new HashMap<>();
		groupAliases = new HashMap<>();
		
		if(exists()) {
			try(XmlStreamReader streamReader = XmlStreamReaderFactory.createReader(this, aliasDefinitionsSchema)) {
				while(streamReader.hasNextTag()) {
					streamReader.nextTag();
					
					if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
						switch(streamReader.getLocalName()) {
							case "aliasDefinitions":
								// do nothing
								break;
							
							case "alias":
								processAlias(streamReader.getChildReader());
								break;
							
							case "group":
								processGroup(streamReader.getChildReader());
								break;
						}
					}
				}
			}
			catch (XMLStreamException e) {
				Location location = e.getLocation();
				
				throw new BundlerFileProcessingException(this, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
			}
			catch (FileNotFoundException | NamespaceException e) {
				throw new BundlerFileProcessingException(this, e);
			}
		}
	}
	
	private void processAlias(XmlStreamReader streamReader) throws XMLStreamException, NamespaceException {
		String aliasName = streamReader.getAttributeValue("name");
		String aliasClass = streamReader.getAttributeValue("defaultClass");
		String aliasInterface = streamReader.getAttributeValue("interface");
		
		if(!aliasName.startsWith(assetContainer.namespace())) {
			throw new NamespaceException("Alias '" + aliasName + "' does not begin with required container prefix of '" + assetContainer.namespace() + "'.");
		}
		
		aliasDefinitions.add(new AliasDefinition(aliasName, aliasClass, aliasInterface));
		
		while(streamReader.hasNextTag()) {
			streamReader.nextTag();
			
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(streamReader.getLocalName()) {
					case "scenario":
						processScenario(aliasName, aliasInterface, streamReader);
						break;
				}
			}
		}
	}
	
	private void processGroup(XmlStreamReader streamReader) throws XMLStreamException {
		String groupName = streamReader.getAttributeValue("name");
		
		while(streamReader.hasNextTag()) {
			streamReader.nextTag();
			
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(streamReader.getLocalName()) {
					case "alias":
						processGroupAlias(groupName, streamReader);
						break;
				}
			}
		}
	}
	
	private void processGroupAlias(String groupName, XmlStreamReader streamReader) {
		String aliasName = streamReader.getAttributeValue("name");
		String aliasClass = streamReader.getAttributeValue("class");
		AliasDefinition aliasDefinition = new AliasDefinition(aliasName, aliasClass, null);
		aliasDefinition.setGroup(groupName);
		
		getGroupAliases(groupName).add(aliasDefinition);
	}
	
	private void processScenario(String aliasName, String aliasInterface, XmlStreamReader streamReader) {
		String scenarioName = streamReader.getAttributeValue("name");
		String aliasClass = streamReader.getAttributeValue("class");
		AliasDefinition aliasDefinition = new AliasDefinition(aliasName, aliasClass, aliasInterface);
		aliasDefinition.setScenario(scenarioName);
		
		getScenarioAliases(scenarioName).add(aliasDefinition);
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
