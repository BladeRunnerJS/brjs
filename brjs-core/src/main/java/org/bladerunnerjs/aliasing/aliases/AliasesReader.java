package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.aliasing.SchemaConverter;
import org.bladerunnerjs.aliasing.SchemaCreationException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.utility.XmlStreamReaderFactory;
import org.codehaus.stax2.XMLStreamReader2;
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
			try(Reader fileReader = new UnicodeReader(aliasesFile, defaultFileCharacterEncoding)) {
				XMLStreamReader2 streamReader = XmlStreamReaderFactory.createReader(fileReader, aliasesSchema);
				
				while(streamReader.hasNext()) {
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
					
					streamReader.next();
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
	
	private void processAliases(XMLStreamReader2 streamReader) {
		aliasesData.scenario = streamReader.getAttributeValue(null, "useScenario");
		
		String useGroups = streamReader.getAttributeValue(null, "useGroups");
		if(useGroups != null) {
			aliasesData.groupNames = Arrays.asList(useGroups.split(" "));
		}
	}
	
	private void processAlias(XMLStreamReader2 streamReader) {
		String aliasName = streamReader.getAttributeValue(null, "name");
		String aliasClass = streamReader.getAttributeValue(null, "class");
		
		aliasesData.aliasOverrides.add(new AliasOverride(aliasName, aliasClass));
	}
}
