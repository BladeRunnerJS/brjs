package com.caplin.cutlass.bundler.js.aliasing;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.bladerunnerjs.model.GroupDefinition;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.exception.NamespaceException;
import com.ctc.wstx.stax.WstxInputFactory;

public class AliasProcessorTest {
	
	private static final String TEST_BASE = "src/test/resources/js-bundler/class-aliasing";
	private static final String TEST_APP_PATH = TEST_BASE + "/apps/namespace-for-alias-definitions/";
	private static final String TEST_GROUPS_PATH = TEST_BASE + "/apps/group-alias/";
	
	private Aliases aliasScenarios;
	private AliasRegistry aliasRegistry;
	private AliasProcessor aliasProcessor;
	private ScenarioAliases scenarioAliases;
	private AliasDefinition aliasDefinitions;
	
	private String bladesetAliasDefinition = "example-bladeset/resources/aliasDefinitions.xml";
	private String bladeAliasDefinition = "example-bladeset/blades/blade1/resources/aliasDefinitions.xml";
	private String bladesetAliasDefinitionWithNamespacedAlias = "example2-bladeset/resources/aliasDefinitions.xml";
	private String bladesetAliasDefinitionWithNotNamespacedAlias = "example3-bladeset/resources/aliasDefinitions.xml";
	private String bladeAliases = "example-bladeset/blades/blade2/workbench/resources/aliases.xml";
	private String groupDefineAtTheBladesetLevel = "example-bladeset/resources/aliasDefinitions.xml";
	private String multipleGroupsDefineAtTheBladesetLevel = "example2-bladeset/resources/aliasDefinitions.xml";
	private String groupInsideAliases = "example-bladeset/blades/blade/workbench/resources/aliases.xml";
	
	private Set<String> validClasses = new HashSet<String>();
	private File bladeLevelAliasesFile = new File( TEST_APP_PATH + bladeAliasDefinition );
	private File bladesetLevelAliasesFile = new File( TEST_APP_PATH + bladesetAliasDefinition );
	private File bladesetLevelNamespacedAliasesFile = new File( TEST_APP_PATH + bladesetAliasDefinitionWithNamespacedAlias );
	private File bladesetLevelNotNamespacedAliasesFile = new File( TEST_APP_PATH + bladesetAliasDefinitionWithNotNamespacedAlias );
	private File bladeAliasesFile = new File( TEST_APP_PATH + bladeAliases );
	private File groupDefineAtTheBladesetLevelFile = new File( TEST_GROUPS_PATH + groupDefineAtTheBladesetLevel );
	private File multipleGroupsDefineAtTheBladesetLevelFile = new File( TEST_GROUPS_PATH + multipleGroupsDefineAtTheBladesetLevel );
	private File groupInsideAliasesFile = new File( TEST_GROUPS_PATH + groupInsideAliases );
	
	
	
	
	private XMLInputFactory2 inputFactory = new WstxInputFactory();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUp()	{
		validClasses.add( "myTestScenarioClass" );
		
		aliasScenarios = mock( Aliases.class );
		aliasRegistry = mock( AliasRegistry.class );
		scenarioAliases = mock( ScenarioAliases.class );
		aliasDefinitions = mock( AliasDefinition.class );
		
		
		aliasRegistry.aliasScenarios = aliasScenarios;
		
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(TEST_BASE)));
	}
	
	@Test
	public void testAnAliasNameDefinedInABladeIsNamespaced() throws BundlerFileProcessingException, XMLStreamException, NamespaceException, FileNotFoundException {
		//Given.
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(bladeLevelAliasesFile), bladeLevelAliasesFile, validClasses );
		
		thrown.expect(NamespaceException.class);
		thrown.expectMessage("The alias name 'alias.name' must be namespaced with the name of the blade, 'novox.example.blade1.'");
		
		//Doing.
		aliasProcessor.processAliasDefinitionsFile();
		
		//Should - thrown an exception.
	}
	
	@Test
	public void testAnAliasNameDefinedInABladesetIsNamespaced() throws BundlerFileProcessingException, XMLStreamException, NamespaceException, FileNotFoundException {
		//Given.
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(bladesetLevelNotNamespacedAliasesFile), bladesetLevelNotNamespacedAliasesFile, validClasses );
		
		thrown.expect(NamespaceException.class);
		thrown.expectMessage("The alias name 'alias.name' must be namespaced with the name of the bladeset, 'novox.example3.'");
		
		//Doing.
		aliasProcessor.processAliasDefinitionsFile();
		
		//Should - thrown an exception.
	}
	
	@Test
	public void testAnAliasDefinedInABladesetIsNamespacedWorks() throws XMLStreamException, NamespaceException, FileNotFoundException, BundlerProcessingException {
		//Given.
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(bladesetLevelNamespacedAliasesFile), bladesetLevelNamespacedAliasesFile, validClasses );
		
		when( aliasScenarios.hasScenario( AliasRegistry.DEFAULT_SCENARIO ) ).thenReturn( true );
		when( aliasScenarios.getScenarioAliases( AliasRegistry.DEFAULT_SCENARIO ) ).thenReturn( scenarioAliases );
		
		//Doing.
		aliasProcessor.processAliasDefinitionsFile();
		
		//Should.
		verify( aliasRegistry ).addClassAlias( any(AliasDefinition.class), eq(AliasRegistry.DEFAULT_SCENARIO) );
		
		
	}
	
	@Test //PCTCUT-667
	public void anAliasDefinitionThatIsInANonDefaultScenarioOneHasTheAliasInterfacePassedIntoIt() throws XMLStreamException, NamespaceException, FileNotFoundException, BundlerProcessingException {
		//Given.
		when( aliasScenarios.hasScenario( AliasRegistry.DEFAULT_SCENARIO ) ).thenReturn( true );
		when( aliasScenarios.getScenarioAliases( AliasRegistry.DEFAULT_SCENARIO ) ).thenReturn( scenarioAliases );
		when( aliasScenarios.hasScenario( "test" ) ).thenReturn( true );
		when( aliasScenarios.getScenarioAliases( "test" ) ).thenReturn( scenarioAliases );
		
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(bladesetLevelAliasesFile), bladesetLevelAliasesFile, validClasses );
		
		//Doing.
		aliasProcessor.processAliasDefinitionsFile();
		
		//Should.
		ArgumentCaptor<AliasDefinition> defaultAliasDefinitionCaptor = ArgumentCaptor.forClass( AliasDefinition.class );
		verify( aliasRegistry ).addClassAlias( defaultAliasDefinitionCaptor.capture(), eq(AliasRegistry.DEFAULT_SCENARIO) );
		
		ArgumentCaptor<AliasDefinition> testAliasDefinitionCaptor = ArgumentCaptor.forClass( AliasDefinition.class );
		verify( aliasRegistry ).addClassAlias( testAliasDefinitionCaptor.capture(), eq("test") );
		
		AliasDefinition defaultAliasDefinition = defaultAliasDefinitionCaptor.getValue();
		AliasDefinition testAliasDefinition = testAliasDefinitionCaptor.getValue();

		assertEquals( "novox.example.myAlias", defaultAliasDefinition.getName() );
		assertEquals( "myInterface", defaultAliasDefinition.getInterfaceName() );
		assertEquals( null, defaultAliasDefinition.getClassName() );

		assertEquals( "novox.example.myAlias", testAliasDefinition.getName() );
		assertEquals( "myInterface", testAliasDefinition.getInterfaceName() );
		assertEquals( "myTestScenarioClass", testAliasDefinition.getClassName() );
	}
	
	@Test //PCTCUT-686
	public void anAssignedAliasHasItsAliasDefinitionInterfacePassedIntoIt() throws XMLStreamException, NamespaceException, FileNotFoundException, BundlerProcessingException {
		//Given.
		when( aliasRegistry.getAliases() ).thenReturn( scenarioAliases );
		when( scenarioAliases.getAlias( "myAlias" ) ).thenReturn( aliasDefinitions );
		when( aliasDefinitions.getInterfaceName() ).thenReturn( "myInterface" );
		when( aliasRegistry.getScenario() ).thenReturn( AliasRegistry.DEFAULT_SCENARIO );
		
		when( aliasScenarios.hasScenario( AliasRegistry.DEFAULT_SCENARIO ) ).thenReturn( true );
		when( aliasScenarios.getScenarioAliases( AliasRegistry.DEFAULT_SCENARIO ) ).thenReturn( scenarioAliases );
		
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(bladeAliasesFile), bladeAliasesFile , validClasses );
		
		//Doing.
		aliasProcessor.processAliasesFile();
		
		//Should.
		ArgumentCaptor<AliasDefinition> aliasDefinitionCaptor = ArgumentCaptor.forClass( AliasDefinition.class );
		verify( aliasRegistry ).addClassAlias( aliasDefinitionCaptor.capture() );
		
		AliasDefinition scenarioDefinition = aliasDefinitionCaptor.getValue();
		
		assertEquals( "myAlias", scenarioDefinition.getName() );
		assertEquals( "myInterface", scenarioDefinition.getInterfaceName() );
		assertEquals( "myTestScenarioClass", scenarioDefinition.getClassName() );
	}
	
	@Test
	public void simpleGroupGetsDefined() throws XMLStreamException, NamespaceException, FileNotFoundException, BundlerProcessingException {
		//Given.
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(groupDefineAtTheBladesetLevelFile), groupDefineAtTheBladesetLevelFile , validClasses );
		
		//Doing.
		aliasProcessor.processAliasDefinitionsFile();
		
		//Should.
		ArgumentCaptor<GroupDefinition> groupDefinitionCaptor = ArgumentCaptor.forClass( GroupDefinition.class );
		verify( aliasRegistry ).addGroup( groupDefinitionCaptor.capture() );
		
		GroupDefinition groupDefinition = groupDefinitionCaptor.getValue();
		
		assertEquals( "novox.myGroup", groupDefinition.getName() );
		assertEquals( "novox.example.myAlias", groupDefinition.getAliasDefinitions(AliasRegistry.DEFAULT_SCENARIO).get(0).getName() );
		assertEquals( "novox.exampleClass", groupDefinition.getAliasDefinitions(AliasRegistry.DEFAULT_SCENARIO).get(0).getClassName() );
	}
	
	@Test
	public void multipleGroupsDefineAtTheBladesetLevelFile() throws XMLStreamException, NamespaceException, FileNotFoundException, BundlerProcessingException {
		//Given.
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(multipleGroupsDefineAtTheBladesetLevelFile), multipleGroupsDefineAtTheBladesetLevelFile , validClasses );
		
		//Doing.
		aliasProcessor.processAliasDefinitionsFile();
		
		//Should.
		ArgumentCaptor<GroupDefinition> groupDefinitionCaptor = ArgumentCaptor.forClass( GroupDefinition.class );
		verify( aliasRegistry , times(2)).addGroup( groupDefinitionCaptor.capture() );
		
		GroupDefinition groupDefinition = groupDefinitionCaptor.getAllValues().get(0);
		
		assertEquals( "novox.myGroup", groupDefinition.getName() );
		assertEquals( "novox.example.myAlias1", groupDefinition.getAliasDefinitions(AliasRegistry.DEFAULT_SCENARIO).get(0).getName() );
		assertEquals( "novox.exampleClass1", groupDefinition.getAliasDefinitions(AliasRegistry.DEFAULT_SCENARIO).get(0).getClassName() );
		assertEquals( "novox.example.myAlias2", groupDefinition.getAliasDefinitions(AliasRegistry.DEFAULT_SCENARIO).get(1).getName() );
		assertEquals( "novox.exampleClass2", groupDefinition.getAliasDefinitions(AliasRegistry.DEFAULT_SCENARIO).get(1).getClassName() );
	}
	
	@Test
	public void aliasRegistryUsesTheGroupsSpecifiedOnAliasesFile() throws XMLStreamException, NamespaceException, FileNotFoundException, BundlerProcessingException {
		//Given.
		aliasProcessor = new AliasProcessor( aliasRegistry, createStreamReader(groupInsideAliasesFile), groupInsideAliasesFile , validClasses );
		
		//Doing.
		aliasProcessor.processAliasesFile();
		
		//Should.
		verify( aliasRegistry ).useGroup( "group1" );
		verify( aliasRegistry ).useGroup( "group2" );
	}
	
	
	

	private XMLStreamReader2 createStreamReader(File file) throws FileNotFoundException, XMLStreamException {
		FileReader fileReader = new FileReader(file);
		XMLStreamReader2 streamReader = (XMLStreamReader2) inputFactory.createXMLStreamReader(fileReader); 
		return streamReader;
	}
	
}
