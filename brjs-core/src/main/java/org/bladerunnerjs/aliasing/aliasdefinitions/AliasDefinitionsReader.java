package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.SchemaConverter;
import org.bladerunnerjs.aliasing.SchemaCreationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.utility.stax.XmlStreamReader;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

public class AliasDefinitionsReader {
	private static final XMLValidationSchema aliasDefinitionsSchema;
	
	private final AliasDefinitionsData data;
	private final File file;
	private final AssetContainer assetContainer;
	private final String defaultInputEncoding;
	
	static {
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try {
			aliasDefinitionsSchema = schemaFactory.createSchema(SchemaConverter
				.convertToRng("org/bladerunnerjs/model/aliasing/aliasDefinitions.rnc"));
		} catch (XMLStreamException | SchemaCreationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public AliasDefinitionsReader(AliasDefinitionsData data, File file, AssetContainer assetContainer) {
		try {
			this.data = data;
			this.file = file;
			this.assetContainer = assetContainer;
			defaultInputEncoding = assetContainer.root().bladerunnerConf().getDefaultInputEncoding();
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void read() throws ContentFileProcessingException {
		data.aliasDefinitions = new ArrayList<>();
		data.scenarioAliases = new HashMap<>();
		data.groupAliases = new HashMap<>();
		
		if(file.exists()) {
			try(XmlStreamReader streamReader = XmlStreamReaderFactory.createReader(file, defaultInputEncoding, aliasDefinitionsSchema)) {
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
				
				throw new ContentFileProcessingException(file, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
			}
			catch (IOException | NamespaceException e) {
				throw new ContentFileProcessingException(file, e);
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
		
		data.aliasDefinitions.add(new AliasDefinition(aliasName, aliasClass, aliasInterface));
		
		while(streamReader.hasNextTag()) {
			streamReader.nextTag();
			
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(streamReader.getLocalName()) {
					case "scenario":
						processScenario(aliasName, streamReader);
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
		AliasOverride groupAlias = new AliasOverride(aliasName, aliasClass);
		
		data.getGroupAliases(groupName).add(groupAlias);
	}
	
	private void processScenario(String aliasName, XmlStreamReader streamReader) {
		String scenarioName = streamReader.getAttributeValue("name");
		String aliasClass = streamReader.getAttributeValue("class");
		AliasOverride scenarioAlias = new AliasOverride(aliasName, aliasClass);
		
		data.getScenarioAliases(aliasName).put(scenarioName, scenarioAlias);
	}
}
