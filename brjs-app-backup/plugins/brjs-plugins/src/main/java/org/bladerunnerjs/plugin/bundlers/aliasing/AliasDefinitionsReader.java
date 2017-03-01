package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.utility.RequirePathUtility;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.utility.stax.XmlStreamCursor;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.*;

public class AliasDefinitionsReader {
	private static final XMLValidationSchema aliasDefinitionsSchema;
	private static final XMLValidationSchema legacyAliasDefinitionsSchema;
	public static final String LEGACY_XMLNS_WARN_MSG = "The file '%s' is using the deprecated xmlns attribute and should be updated to use http://schema.bladerunnerjs.org/aliasDefinitions.";
	
	static {
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try {
			aliasDefinitionsSchema = schemaFactory.createSchema(SchemaConverter
				.convertToRng("org/bladerunnerjs/model/aliasing/aliasDefinitions.rnc"));
			legacyAliasDefinitionsSchema = schemaFactory.createSchema(SchemaConverter
					.convertToRng("org/bladerunnerjs/model/aliasing/aliasDefinitions-legacy.rnc"));
		} catch (XMLStreamException | SchemaCreationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static AliasDefinitionsData read(BRJS brjs, MemoizedFile aliasDefinitionsFile, AssetContainer assetContainer, String defaultFileCharacterEncoding) throws ContentFileProcessingException {
		AliasDefinitionsData data = new AliasDefinitionsData();
		data.aliasDefinitions = new ArrayList<>();
		data.scenarioAliases = new LinkedHashMap<>();
		data.groupAliases = new LinkedHashMap<>();
		
		if(aliasDefinitionsFile.exists()) {
			try(Reader fileReader = new UnicodeReader(aliasDefinitionsFile, defaultFileCharacterEncoding)) {
				XMLValidationSchema schema;
				if (usesLegacySchema(aliasDefinitionsFile, defaultFileCharacterEncoding)) {
					schema = legacyAliasDefinitionsSchema;
					if (!xmlnsWarningLogged(brjs, aliasDefinitionsFile)) {
						 brjs.logger(AliasDefinitionsReader.class).warn(LEGACY_XMLNS_WARN_MSG, brjs.dir().getRelativePath(aliasDefinitionsFile));
					 }
				} else {
					schema = aliasDefinitionsSchema;
				}
				XMLStreamReader2 streamReader = XmlStreamReaderFactory.createReader(fileReader, schema);
				XmlStreamCursor cursor = new XmlStreamCursor(streamReader);
				
				while(cursor.isWithinInitialNode()) {
					if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
						switch(streamReader.getLocalName()) {
							case "alias":
								processAlias(streamReader, data, aliasDefinitionsFile, assetContainer);
								break;
							
							case "group":
								processGroup(streamReader, data, assetContainer);
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
	
	private static void processAlias(XMLStreamReader2 streamReader, AliasDefinitionsData data, MemoizedFile aliasDefinitionsFile, AssetContainer assetContainer) throws XMLStreamException, NamespaceException, RequirePathException, AliasException {
		String aliasName = streamReader.getAttributeValue(null, "name");
		String aliasClass = streamReader.getAttributeValue(null, "defaultClass");
		String aliasInterface = streamReader.getAttributeValue(null, "interface");
		
		if (aliasName.equals(aliasClass)) {
			throw new AliasNameIsTheSameAsTheClassException(aliasDefinitionsFile, aliasName);
		}
		
		RequirePathUtility.assertIdentifierCorrectlyNamespaced(assetContainer, aliasName);
		
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
	
	private static void processGroup(XMLStreamReader2 streamReader, AliasDefinitionsData data, AssetContainer assetContainer) throws XMLStreamException, NamespaceException, RequirePathException {
		String groupName = streamReader.getAttributeValue(null, "name");
		
		RequirePathUtility.assertIdentifierCorrectlyNamespaced(assetContainer, groupName);
		
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
	
	private static boolean xmlnsWarningLogged(BRJS brjs, MemoizedFile file) {
		@SuppressWarnings("unchecked")
		Set<MemoizedFile> loggedWarnings = getNodeProperty(brjs, AliasDefinitionsReader.class.getSimpleName(), Set.class, 
				() -> { return new LinkedHashSet<MemoizedFile>(); });
		if (loggedWarnings.contains(file)) {
			return true;
		}
		loggedWarnings.add(file);
		return false;
	}
}
