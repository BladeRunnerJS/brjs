package com.caplin.cutlass.bundler.js.aliasing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.SAXException;

import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.bundler.js.ClassDictionary;
import com.caplin.cutlass.exception.NamespaceException;
import com.ctc.wstx.exc.WstxValidationException;
import com.ctc.wstx.msv.RelaxNGSchemaFactory;
import com.ctc.wstx.stax.WstxInputFactory;
import com.google.common.io.Files;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.translate.Formats;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

public class AliasRegistry implements AliasContainer
{
	public static String DEFAULT_SCENARIO = "default";
	
	private static XMLInputFactory2 inputFactory;
	private static XMLValidationSchema aliasDefinitionsSchema;
	private static XMLValidationSchema aliasSchema;
	private String scenario = DEFAULT_SCENARIO;
	private Map<String, GroupDefinition> groupDefinitions = null;
	
	Aliases aliasScenarios = new Aliases();

	private Set<String> validClasses;
	
	{
		inputFactory = new WstxInputFactory();
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try
		{
			aliasDefinitionsSchema = schemaFactory.createSchema(createSchema("aliasDefinitions"));
			aliasSchema = schemaFactory.createSchema(createSchema("aliases"));
		}
		catch (XMLStreamException | SAXException | IOException | OutputFailedException | InvalidParamsException | InputFailedException | URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public AliasRegistry(File baseDir, File testDir, Set<String> validClasses) throws BundlerFileProcessingException
	{
		aliasScenarios.addScenario(DEFAULT_SCENARIO);
		this.validClasses = validClasses;
		
		try
		{
			processAliasFiles(new BladeRunnerSourceFileProvider(new AliasFileAppender()).getSourceFiles(baseDir, testDir), validClasses);
		}
		catch(BundlerFileProcessingException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String getScenario()
	{
		return scenario;
	}
	
	public void setScenario(String scenario)
	{
		this.scenario = scenario;
		
		if(!aliasScenarios.hasScenario(scenario))
		{
			aliasScenarios.addScenario(scenario);
		}
	}
	
	private File createSchema(String schemaName) throws SAXException, IOException, OutputFailedException, InvalidParamsException, InputFailedException, URISyntaxException
	{
		String rncInputFile = getClass().getClassLoader().getResource(schemaName + ".rnc").toString().replace('\\', '/').replaceAll(" ","%20");
		File rngOutputFile = new File(Files.createTempDir(), schemaName + ".rng");
		
		InputFormat inputFormat = Formats.createInputFormat("rnc");
		OutputFormat outputFormat = Formats.createOutputFormat("rng");
		
		String[] inputOptions = {};
		SchemaCollection schemaCollection = inputFormat.load(rncInputFile, inputOptions, "rng", new ErrorHandlerImpl(), null);
		OutputDirectory outputDirectory = new LocalOutputDirectory(schemaCollection.getMainUri(), rngOutputFile, "rng", "UTF-8", 72, 2);
		
		String[] outputOptions = {};
		outputFormat.output(schemaCollection, outputDirectory, outputOptions, "rnc", new ErrorHandlerImpl());
		
		return rngOutputFile;
	}
	
	public ScenarioAliases getAliases()
	{
		return aliasScenarios.getScenarioAliases(scenario);
	}
	
	public String getJson( ClassDictionary classDictionary, ScenarioAliases activeAliases )
	{
		StringBuilder jsonData = new StringBuilder();
		boolean firstAlias = true;
		
		jsonData.append("{");
		
		for(String aliasName : activeAliases.getAliasNames())
		{
			AliasDefinition aliasDefinition = activeAliases.getAlias(aliasName);
			
			if(firstAlias)
			{
				firstAlias = false;
			}
			else
			{
				jsonData.append(",");
			}
			
			jsonData.append("'" + aliasName + "':{");
			
			if(aliasDefinition.getClassName() != null)
			{
				jsonData.append("'class':" + aliasDefinition.getClassName() + ",'className':'" + aliasDefinition.getClassName() + "'");
			}
			
			if(aliasDefinition.getInterfaceName() != null)
			{
				if(aliasDefinition.getClassName() != null)
				{
					jsonData.append(",");
				}
				
				jsonData.append("'interface':" + aliasDefinition.getInterfaceName() + ",'interfaceName':'" + aliasDefinition.getInterfaceName() + "'");
			}
			
			jsonData.append("}");
		}
		
		jsonData.append("}");
		
		return jsonData.toString();
	}
	
	private void processAliasFiles(List<File> sourceFiles, Set<String> validClasses) throws BundlerFileProcessingException, XMLStreamException,
	FileNotFoundException, IOException, NamespaceException
	{
		for(File sourceFile : sourceFiles)
		{
			processAliasFile(sourceFile, validClasses);
		}
		
		if(!scenario.equals(DEFAULT_SCENARIO))
		{
			defaultMissingAliases();
		}
	}
	
	private void processAliasFile(File aliasesFile, Set<String> validClasses) throws BundlerFileProcessingException, XMLStreamException, 
	FileNotFoundException, IOException, NamespaceException
	{
		XMLStreamReader2 streamReader = null;
		
		try(FileReader fileReader = new FileReader(aliasesFile))
		{
			streamReader = (XMLStreamReader2) inputFactory.createXMLStreamReader(fileReader);
			AliasProcessor aliasProcessor = new AliasProcessor(this, streamReader, aliasesFile, validClasses);
			
			if(aliasesFile.getName().equals("aliasDefinitions.xml"))
			{
				streamReader.validateAgainst(aliasDefinitionsSchema);
				aliasProcessor.processAliasDefinitionsFile();
			}
			else
			{
				streamReader.validateAgainst(aliasSchema);
				aliasProcessor.processAliasesFile();
			}
		}
		catch(WstxValidationException e)
		{
			Location location = e.getLocation();
			
			throw new BundlerFileProcessingException(aliasesFile, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
		}
		finally
		{
			streamReader.close();
		}
	}
	
	private void defaultMissingAliases()
	{
		ScenarioAliases scenarioAliases = getAliases();
		ScenarioAliases defaultAliases = aliasScenarios.getScenarioAliases(DEFAULT_SCENARIO);
		
		for(String aliasName : defaultAliases.getAliasNames())
		{
			if(!scenarioAliases.hasAlias(aliasName))
			{
				AliasDefinition aliasDefinitions = defaultAliases.getAlias(aliasName);
				
				scenarioAliases.addAlias(aliasName, new AliasDefinition(aliasDefinitions));
			}
		}
	}

	public void addGroup(GroupDefinition groupDefinition) throws BundlerProcessingException {
		
		String groupName = groupDefinition.getName();
		if (groupDefinitions == null)
		{
			groupDefinitions = new HashMap<String, GroupDefinition>();
		}
		
		if(groupDefinitions.containsKey(groupName))
		{
			throw new BundlerProcessingException("The alias group " + groupName + " has already been defined");
		}
		groupDefinitions.put(groupName, groupDefinition);
	}
	
	public void useGroup(String groupName) throws BundlerProcessingException {
		if(groupDefinitions == null || !groupDefinitions.containsKey(groupName))
		{
			throw new BundlerProcessingException("The alias group " + groupName + " hasn't been defined");
		}
		GroupDefinition groupToUse = groupDefinitions.get(groupName);
		
		addGroupAliases(groupToUse, DEFAULT_SCENARIO);
		
		if(!this.scenario.equals(DEFAULT_SCENARIO))
		{
			addGroupAliases(groupToUse, this.scenario);
		}
	}

	public void addGroupAliases(GroupDefinition groupToUse, String scenario) throws BundlerProcessingException 
	{
		List<AliasDefinition> scenarioAliases = groupToUse.getAliasDefinitions(scenario);
		if(scenarioAliases != null)
		{
			for(AliasDefinition alias : scenarioAliases)
			{
				String groupName = groupToUse.getName();
				alias.setGroup(groupName);
				alias.setInterfaceName(getInterfaceForAlias(alias.getName()));
				addClassAlias(alias);
			}
		}
	}
	
	private void verifyAliasDoesNotClash(AliasDefinition newAlias, AliasDefinition existingAlias ) throws BundlerProcessingException 
	{
		String newGroup = newAlias.getGroup();
		String aliasName = newAlias.getName();

		if (existingAlias != null && existingAlias.getGroup() != null  && existingAlias.getName().equals(aliasName) && !existingAlias.getGroup().equals(newGroup))
		{
			throw new BundlerProcessingException("Alias " + newAlias.getName() + " has been defined in at least 2 groups: " + newGroup  + " and " + existingAlias.getGroup());
		}
	}

	public void addClassAlias( AliasDefinition alias) throws BundlerProcessingException
	{
		addClassAlias(alias, this.scenario);
	}

	/* (non-Javadoc)
	 * @see com.caplin.cutlass.bundler.js.aliasing.SomeSortOfInterface#addClassAlias(org.bladerunnerjs.model.AliasDefinition, java.lang.String)
	 */
	@Override
	public void addClassAlias(AliasDefinition alias, String scenario) throws BundlerProcessingException {
		ScenarioAliases scenarioAliases = getScenarioAliases(scenario);
		
		String className = alias.getClassName();
		
		if(className != null && ! validClasses.contains(className))
		{
			throw new BundlerProcessingException("There is no such class as '" + className + "'.");
		}
		
		verifyAliasDoesNotClash(alias, scenarioAliases.getAlias(alias.getName()));
		//TODO check for clashes
		scenarioAliases.addAlias(alias.getName(), alias);
	}
	
	private ScenarioAliases getScenarioAliases(String scenarioName)
	{
		if(!aliasScenarios.hasScenario(scenarioName))
		{
			aliasScenarios.addScenario(scenarioName);
		}
		
		return aliasScenarios.getScenarioAliases(scenarioName);
	}
	
	private String getInterfaceForAlias(String aliasName)
	{
		ScenarioAliases scenarioAliases = getAliases();
		
		if( scenarioAliases != null ) {
			AliasDefinition aliasDefinitions = scenarioAliases.getAlias( aliasName );
			
			if( aliasDefinitions != null ) {
				return aliasDefinitions.getInterfaceName();
			}
		}
		return null;
	}
}
