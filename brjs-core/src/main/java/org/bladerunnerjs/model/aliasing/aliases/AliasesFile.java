package org.bladerunnerjs.model.aliasing.aliases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.aliasing.AliasOverride;
import org.bladerunnerjs.model.aliasing.SchemaConverter;
import org.bladerunnerjs.model.aliasing.SchemaCreationException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.model.utility.stax.XmlStreamReader;
import org.bladerunnerjs.specutil.XmlBuilderSerializer;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;
import com.esotericsoftware.yamlbeans.parser.Parser.ParserException;
import com.google.common.base.Joiner;
import com.jamesmurty.utils.XMLBuilder;

public class AliasesFile {
	private static XMLValidationSchema aliasesSchema;
	
	private final AliasesData data = new AliasesData();
	private final FileModifiedChecker fileModifiedChecker;
	
	private File underlyingFile;
	
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
	
	public AliasesFile(File parent, String child) {
		underlyingFile = new File(parent, child);
		fileModifiedChecker = new FileModifiedChecker(underlyingFile);
	}
	
	public File getUnderlyingFile() {
		return underlyingFile;
	}
	
	public String scenarioName() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return data.scenario;
	}
	
	public void setScenarioName(String scenarioName) {
		data.scenario = scenarioName;
	}
	
	public List<String> groupNames() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return data.groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) {
		data.groupNames = groupNames;
	}
	
	public List<AliasOverride> aliasOverrides() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return data.aliasOverrides;
	}
	
	public void addAlias(AliasOverride aliasOverride) {
		data.aliasOverrides.add(aliasOverride);
	}
	
	public AliasOverride getAlias(String aliasName) throws BundlerFileProcessingException {
		AliasOverride aliasOverride = null;
		
		for(AliasOverride nextAliasOverride : aliasOverrides()) {
			if(nextAliasOverride.getName().equals(aliasName)) {
				aliasOverride = nextAliasOverride;
				break;
			}
		}
		
		return aliasOverride;
	}
	
	public void write() throws IOException {
		try {
			XMLBuilder builder = XMLBuilder.create("aliases").ns("http://schema.caplin.com/CaplinTrader/aliases");
			
			if(data.scenario != null) {
				builder.a("useScenario", data.scenario);
			}
			
			if(!data.groupNames.isEmpty()) {
				builder.a("useGroups", Joiner.on(" ").join(data.groupNames));
			}
			
			for(AliasOverride aliasOverride : data.aliasOverrides) {
				builder.e("alias").a("name", aliasOverride.getName()).a("class", aliasOverride.getClassName());
			}
			
			FileUtils.write(underlyingFile, XmlBuilderSerializer.serialize(builder));
		}
		catch(ParserException | TransformerException | ParserConfigurationException | FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}
	
	private void reparseFile() throws BundlerFileProcessingException {
		data.aliasOverrides = new ArrayList<>();
		data.groupNames = new ArrayList<>();
		
		if(underlyingFile.exists()) {
			try(XmlStreamReader streamReader = XmlStreamReaderFactory.createReader(underlyingFile, aliasesSchema)) {
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
				
				throw new BundlerFileProcessingException(underlyingFile, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
			}
			catch (FileNotFoundException e) {
				throw new BundlerFileProcessingException(underlyingFile, e);
			}
		}
	}
	
	private void processAliases(XmlStreamReader streamReader) {
		data.scenario = streamReader.getAttributeValue("useScenario");
		
		String useGroups = streamReader.getAttributeValue("useGroups");
		if(useGroups != null) {
			data.groupNames = Arrays.asList(useGroups.split(" "));
		}
	}
	
	private void processAlias(XmlStreamReader streamReader) {
		String aliasName = streamReader.getAttributeValue("name");
		String aliasClass = streamReader.getAttributeValue("class");
		
		data.aliasOverrides.add(new AliasOverride(aliasName, aliasClass));
	}
}
