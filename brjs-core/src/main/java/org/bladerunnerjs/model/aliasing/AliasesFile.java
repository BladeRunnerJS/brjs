package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.XmlStreamReaderFactory;
import org.bladerunnerjs.model.utility.XmlStreamReader;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

public class AliasesFile extends File {
	private static final long serialVersionUID = -3607791607132062852L;
	
	private static XMLValidationSchema aliasesSchema;
	private final FileModifiedChecker fileModifiedChecker;
	private List<AliasDefinition> aliasDefinitions;
	private List<String> groupNames;
	
	{
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
		super(parent, child);
		fileModifiedChecker = new FileModifiedChecker(this);
	}
	
	public AliasDefinition getAlias(AliasName aliasName, String scenarioName) throws BundlerFileProcessingException {
		AliasDefinition aliasDefinition = null;
		
		for(AliasDefinition nextAliasDefinition : aliasDefinitions()) {
			if(nextAliasDefinition.getScenario().equals(scenarioName) && nextAliasDefinition.getName().equals(aliasName.getName())) {
				aliasDefinition = nextAliasDefinition;
				break;
			}
		}
		
		return aliasDefinition;
	}
	
	public List<String> groupNames() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return groupNames;
	}
	
	public List<AliasDefinition> aliasDefinitions() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return aliasDefinitions;
	}
	
	private void reparseFile() throws BundlerFileProcessingException {
		aliasDefinitions = new ArrayList<>();
		groupNames = new ArrayList<>();
		
		if(this.exists()) {
			try(XmlStreamReader streamReader = XmlStreamReaderFactory.createReader(this, aliasesSchema)) {
				// TODO: bring more aliasing code over from the 'bundlers' project
			}
			catch (XMLStreamException e) {
				Location location = e.getLocation();
				
				throw new BundlerFileProcessingException(this, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
			}
			catch (FileNotFoundException e) {
				throw new BundlerFileProcessingException(this, e);
			}
		}
	}
}
