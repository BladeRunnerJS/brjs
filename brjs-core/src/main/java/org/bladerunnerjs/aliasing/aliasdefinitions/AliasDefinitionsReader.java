package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.AliasNameIsTheSameAsTheClassException;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.SchemaConverter;
import org.bladerunnerjs.aliasing.SchemaCreationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.utility.stax.XmlStreamCursor;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

public class AliasDefinitionsReader {
	private static final XMLValidationSchema aliasDefinitionsSchema;
	
	static {
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try {
			aliasDefinitionsSchema = schemaFactory.createSchema(SchemaConverter
				.convertToRng("org/bladerunnerjs/model/aliasing/aliasDefinitions.rnc"));
		} catch (XMLStreamException | SchemaCreationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static AliasDefinitionsData read(File aliasDefinitionsFile, AssetLocation assetLocation, String defaultFileCharacterEncoding) throws ContentFileProcessingException {
		AliasDefinitionsData data = new AliasDefinitionsData();
		data.aliasDefinitions = new ArrayList<>();
		data.scenarioAliases = new HashMap<>();
		data.groupAliases = new HashMap<>();
		
		if(aliasDefinitionsFile.exists()) {
			try(Reader fileReader = new UnicodeReader(aliasDefinitionsFile, defaultFileCharacterEncoding)) {
				XMLStreamReader2 streamReader = XmlStreamReaderFactory.createReader(fileReader, aliasDefinitionsSchema);
				XmlStreamCursor cursor = new XmlStreamCursor(streamReader);
				
				while(cursor.isWithinInitialNode()) {
					if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
						switch(streamReader.getLocalName()) {
							case "alias":
								processAlias(streamReader, data, aliasDefinitionsFile, assetLocation);
								break;
							
							case "group":
								processGroup(streamReader, data, assetLocation);
								break;
						}
					}
					
					cursor.nextTag();
				}
			}
			catch (XMLStreamException e) {
				Location location = e.getLocation();
				
				throw new ContentFileProcessingException(aliasDefinitionsFile, location.getLineNumber(), location.getColumnNumber(), e);
			}
			catch (IOException | NamespaceException | RequirePathException | AliasException e) {
				throw new ContentFileProcessingException(aliasDefinitionsFile, e);
			}
		}
		
		return data;
	}
	
	private static void processAlias(XMLStreamReader2 streamReader, AliasDefinitionsData data, File aliasDefinitionsFile, AssetLocation assetLocation) throws XMLStreamException, NamespaceException, RequirePathException, AliasException {
		String aliasName = streamReader.getAttributeValue(null, "name");
		String aliasClass = streamReader.getAttributeValue(null, "defaultClass");
		String aliasInterface = streamReader.getAttributeValue(null, "interface");
		
		if (aliasName.equals(aliasClass)) {
			throw new AliasNameIsTheSameAsTheClassException(aliasDefinitionsFile, aliasName);
		}
		
		assetLocation.assertIdentifierCorrectlyNamespaced(aliasName);
		
		data.aliasDefinitions.add(new AliasDefinition(aliasName, aliasClass, aliasInterface));
		
		XmlStreamCursor cursor = new XmlStreamCursor(streamReader);
		while(cursor.isWithinInitialNode()) {
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(streamReader.getLocalName()) {
					case "scenario":
						processScenario(aliasName, streamReader, data);
						break;
				}
			}
			
			cursor.nextTag();
		}
	}
	
	private static void processGroup(XMLStreamReader2 streamReader, AliasDefinitionsData data, AssetLocation assetLocation) throws XMLStreamException, NamespaceException, RequirePathException {
		String groupName = streamReader.getAttributeValue(null, "name");
		
		assetLocation.assertIdentifierCorrectlyNamespaced(groupName);
		
		XmlStreamCursor cursor = new XmlStreamCursor(streamReader);
		
		while(cursor.isWithinInitialNode()) {
			if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(streamReader.getLocalName()) {
					case "alias":
						processGroupAlias(groupName, streamReader, data);
						break;
				}
			}
			
			cursor.nextTag();
		}
	}
	
	private static void processGroupAlias(String groupName, XMLStreamReader2 streamReader, AliasDefinitionsData data) {
		String aliasName = streamReader.getAttributeValue(null, "name");
		String aliasClass = streamReader.getAttributeValue(null, "class");
		AliasOverride groupAlias = new AliasOverride(aliasName, aliasClass);
		
		data.getGroupAliases(groupName).add(groupAlias);
	}
	
	private static void processScenario(String aliasName, XMLStreamReader2 streamReader, AliasDefinitionsData data) {
		String scenarioName = streamReader.getAttributeValue(null, "name");
		String aliasClass = streamReader.getAttributeValue(null, "class");
		AliasOverride scenarioAlias = new AliasOverride(aliasName, aliasClass);
		
		data.getScenarioAliases(aliasName).put(scenarioName, scenarioAlias);
	}
}
