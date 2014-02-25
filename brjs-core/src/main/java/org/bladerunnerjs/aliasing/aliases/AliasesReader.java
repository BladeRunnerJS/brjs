package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.SchemaConverter;
import org.bladerunnerjs.aliasing.SchemaCreationException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.utility.stax.XmlStreamReader;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

public class AliasesReader {
	private static final XMLValidationSchema aliasesSchema;
	
	private AliasesData aliasesData;
	private File aliasesFile;

	private String defaultFileCharacterEncoding;
	
	static {
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try
		{
			aliasesSchema = schemaFactory.createSchema(SchemaConverter.convertToRng("org/bladerunnerjs/model/aliasing/aliases.rnc"));
		}
		catch (XMLStreamException | SchemaCreationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public AliasesReader(AliasesData aliasesData, File aliasesFile, String defaultFileCharacterEncoding) {
		this.aliasesData = aliasesData;
		this.aliasesFile = aliasesFile;
		this.defaultFileCharacterEncoding = defaultFileCharacterEncoding;
	}
	
	public void read() throws ContentFileProcessingException {
		aliasesData.aliasOverrides = new ArrayList<>();
		aliasesData.groupNames = new ArrayList<>();
		
		if(aliasesFile.exists()) {
			try(XmlStreamReader streamReader = XmlStreamReaderFactory.createReader(aliasesFile, defaultFileCharacterEncoding, aliasesSchema)) {
				while(streamReader.hasNextTag()) {
					streamReader.nextTag();
					
					if(streamReader.getEventType() == XMLStreamReader.START_ELEMENT) {
						switch(streamReader.getLocalName()) {
							case "aliases":
								processAliases(streamReader);
								break;
							
							case "alias":
								processAlias(streamReader);
								break;
						}
					}
				}
			}
			catch (XMLStreamException e) {
				Location location = e.getLocation();
				
				throw new ContentFileProcessingException(aliasesFile, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
			}
			catch (IOException e) {
				throw new ContentFileProcessingException(aliasesFile, e);
			}
		}
	}
	
	private void processAliases(XmlStreamReader streamReader) {
		aliasesData.scenario = streamReader.getAttributeValue("useScenario");
		
		String useGroups = streamReader.getAttributeValue("useGroups");
		if(useGroups != null) {
			aliasesData.groupNames = Arrays.asList(useGroups.split(" "));
		}
	}
	
	private void processAlias(XmlStreamReader streamReader) {
		String aliasName = streamReader.getAttributeValue("name");
		String aliasClass = streamReader.getAttributeValue("class");
		
		aliasesData.aliasOverrides.add(new AliasOverride(aliasName, aliasClass));
	}
}
