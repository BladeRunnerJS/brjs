package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.stax.XmlStreamReader;
import org.bladerunnerjs.model.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.specutil.XmlBuilderSerializer;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;
import com.jamesmurty.utils.XMLBuilder;

public class AliasDefinitionsFile extends File {
	private static final long serialVersionUID = 1L;
	private static XMLValidationSchema aliasDefinitionsSchema;
	
	private final FileModifiedChecker fileModifiedChecker;
	private List<AliasDefinition> aliasDefinitions = new ArrayList<>();
	private Map<String, Map<String, AliasDefinition>> scenarioAliases = new HashMap<>();
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
	
	public List<String> aliasNames() throws BundlerFileProcessingException {
		List<String> aliasNames = new ArrayList<>();
		
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		for(AliasDefinition aliasDefinition : aliasDefinitions) {
			aliasNames.add(aliasDefinition.getName());
		}
		
		for(Map<String, AliasDefinition> aliasScenarioAliases : scenarioAliases.values()) {
			for(AliasDefinition scenarioAlias : aliasScenarioAliases.values()) {
				aliasNames.add(scenarioAlias.getName());
			}
		}
		
		for(List<AliasDefinition> groupAliasList : groupAliases.values()) {
			for(AliasDefinition groupAlias : groupAliasList) {
				aliasNames.add(groupAlias.getName());
			}
		}
		
		return aliasNames;
	}
	
	public void addAlias(AliasDefinition aliasDefinition) {
		aliasDefinitions.add(aliasDefinition);
	}
	
	public List<AliasDefinition> aliases() throws BundlerFileProcessingException {
		return aliasDefinitions;
	}
	
	public void addScenarioAlias(String scenarioName, AliasDefinition aliasDefinition) {
		getScenarioAliases(aliasDefinition.getName()).put(scenarioName, aliasDefinition);
	}
	
	public Map<String, AliasDefinition> scenarioAliases(AliasDefinition alias) throws BundlerFileProcessingException {
		return scenarioAliases.get(alias.getName());
	}
	
	// TODO: AliasDefinition -> AliasOverride
	public void addGroupAliasOverride(String groupName, AliasDefinition aliasOverride) {
		getGroupAliases(groupName).add(aliasOverride);
	}
	
	public Set<String> groupNames() {
		return groupAliases.keySet();
	}
	
	public List<AliasDefinition> groupAliases(String groupName) throws BundlerFileProcessingException {
		return ((groupAliases.containsKey(groupName)) ? groupAliases.get(groupName) : new ArrayList<AliasDefinition>());
	}
	
	public AliasDefinition getAlias(String aliasName, String scenarioName, List<String> groupNames) throws BundlerFileProcessingException {
		AliasDefinition aliasDefinition = null;
		
		try {
			for(AliasDefinition nextAliasDefinition : aliases()) {
				if(nextAliasDefinition.getName().equals(aliasName)) {
					if(scenarioName != null) {
						AliasDefinition scenarioAliasDefinition = getScenarioAliases(nextAliasDefinition.getName()).get(scenarioName);
						
						if(scenarioAliasDefinition != null) {
							nextAliasDefinition = scenarioAliasDefinition;
						}
					}
					
					if(aliasDefinition != null) {
						throw new AmbiguousAliasException(this, aliasName, scenarioName);
					}
					
					aliasDefinition = nextAliasDefinition;
				}
			}
			
			for(String groupName : groupNames) {
				for(AliasDefinition nextGroupAlias : groupAliases(groupName)) {
					if(nextGroupAlias.getName().equals(aliasName)) {
						if(aliasDefinition != null) {
							throw new AmbiguousAliasException(this, aliasName, scenarioName);
						}
						
						aliasDefinition = nextGroupAlias;
					}
				}
			}
		}
		catch(AmbiguousAliasException e) {
			throw new BundlerFileProcessingException(this, e);
		}
		
		return aliasDefinition;
	}
	
	public void write() throws IOException {
		try {
			XMLBuilder builder = XMLBuilder.create("aliasDefinitions").ns("http://schema.caplin.com/CaplinTrader/aliasDefinitions");
			
			for(AliasDefinition aliasDefinition : aliasDefinitions) {
				XMLBuilder aliasBuilder = builder.e("alias").a("name", aliasDefinition.getName()).a("defaultClass", aliasDefinition.getClassName());
				Map<String, AliasDefinition> scenarioAliases = getScenarioAliases(aliasDefinition.getName());
				
				for(String scenarioName : scenarioAliases.keySet()) {
					AliasDefinition scenarioAliasDefinition = scenarioAliases.get(scenarioName);
					aliasBuilder.e("scenario").a("name", scenarioName).a("class", scenarioAliasDefinition.getClassName());
				}
			}
			
			for(String groupName : groupAliases.keySet()) {
				XMLBuilder groupBuilder = builder.e("group").a("name", groupName);
				
				for(AliasDefinition groupAlias : groupAliases.get(groupName)) {
					groupBuilder.e("alias").a("name", groupAlias.getName()).a("class", groupAlias.getClassName());
				}
			}
			
			FileUtils.write(this, XmlBuilderSerializer.serialize(builder));
		}
		catch(IOException | ParserConfigurationException | FactoryConfigurationError | TransformerException e) {
			throw new IOException(e);
		}
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
		
		getScenarioAliases(aliasName).put(scenarioName, aliasDefinition);
	}
	
	private Map<String, AliasDefinition> getScenarioAliases(String aliasName) {
		if(!scenarioAliases.containsKey(aliasName)) {
			scenarioAliases.put(aliasName, new HashMap<String, AliasDefinition>());
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
