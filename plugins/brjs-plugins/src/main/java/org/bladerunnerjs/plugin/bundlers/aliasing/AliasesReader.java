package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.XmlStreamReaderFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

import static org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility.*;

public class AliasesReader {
	private static final XMLValidationSchema aliasesSchema;
	private static final XMLValidationSchema legacyAliasesSchema;
	
	public static final String LEGACY_XMLNS_WARN_MSG = "The file '%s' is using the deprecated xmlns attribute and should be updated to use http://schema.bladerunnerjs.org/aliases.";
	
	static {
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try
		{
			aliasesSchema = schemaFactory.createSchema(SchemaConverter.convertToRng("org/bladerunnerjs/model/aliasing/aliases.rnc"));
			legacyAliasesSchema = schemaFactory.createSchema(SchemaConverter.convertToRng("org/bladerunnerjs/model/aliasing/aliases-legacy.rnc"));
		}
		catch (XMLStreamException | SchemaCreationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static AliasesData read(BRJS brjs, MemoizedFile aliasesFile, String defaultFileCharacterEncoding) throws ContentFileProcessingException {
		AliasesData aliasesData = new AliasesData();
		aliasesData.aliasOverrides = new ArrayList<>();
		aliasesData.groupNames = new ArrayList<>();
		
		if(aliasesFile.exists()) {
			try(Reader fileReader = new UnicodeReader(aliasesFile, defaultFileCharacterEncoding)) {
				XMLValidationSchema schema;
				 if (usesLegacySchema(aliasesFile, defaultFileCharacterEncoding)) {
					 schema = legacyAliasesSchema;
					 if (!xmlnsWarningLogged(brjs, aliasesFile)) {
						 brjs.logger(AliasesReader.class).warn(LEGACY_XMLNS_WARN_MSG, brjs.dir().getRelativePath(aliasesFile));
					 }
				 } else {
					 schema = aliasesSchema;
				 }
				XMLStreamReader2 streamReader = XmlStreamReaderFactory.createReader(fileReader, schema);
				
				while(streamReader.hasNext()) {
					if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
						switch(streamReader.getLocalName()) {
							case "aliases":
								processAliases(streamReader, aliasesData);
								break;
							
							case "alias":
								processAlias(streamReader, aliasesData, aliasesFile);
								break;
						}
					}
					
					streamReader.next();
				}
			}
			catch (XMLStreamException e) {
				Location location = e.getLocation();
				
				throw new ContentFileProcessingException(aliasesFile, location.getLineNumber(), location.getColumnNumber(), e);
			}
			catch (IOException | AliasException e) {
				throw new ContentFileProcessingException(aliasesFile, e);
			}
		}
		
		return aliasesData;
	}
	
	private static void processAliases(XMLStreamReader2 streamReader, AliasesData aliasesData) {
		aliasesData.scenario = streamReader.getAttributeValue(null, "useScenario");
		
		String useGroups = streamReader.getAttributeValue(null, "useGroups");
		if(useGroups != null) {
			aliasesData.groupNames = Arrays.asList(useGroups.split(" "));
		}
	}
	
	private static void processAlias(XMLStreamReader2 streamReader, AliasesData aliasesData, MemoizedFile aliasesFile) throws AliasException {
		String aliasName = streamReader.getAttributeValue(null, "name");
		String aliasClass = streamReader.getAttributeValue(null, "class");
		
		if (aliasName.equals(aliasClass)) {
			throw new AliasNameIsTheSameAsTheClassException(aliasesFile, aliasName);
		}
		
		if((aliasClass == null) || (aliasClass.equals(""))) {
			throw new IncompleteAliasException(aliasesFile, aliasName);
		}
		
		aliasesData.aliasOverrides.add(new AliasOverride(aliasName, aliasClass));
	}
	
	private static boolean xmlnsWarningLogged(BRJS brjs, MemoizedFile file) {
		@SuppressWarnings("unchecked")
		Set<MemoizedFile> loggedWarnings = getNodeProperty(brjs, AliasesReader.class.getSimpleName(), Set.class, 
				() -> { return new LinkedHashSet<MemoizedFile>(); });
		if (loggedWarnings.contains(file)) {
			return true;
		}
		loggedWarnings.add(file);
		return false;
	}
	
}
