package com.caplin.cutlass.bundler.js.aliasing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

import com.caplin.cutlass.bundler.js.ClassDictionary;
import com.caplin.cutlass.bundler.js.SourceFileLocator;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class AliasRegistryTest
{
	private ClassDictionary classDictionary = new ClassDictionary();
	
	private static final String testBase = "src/test/resources/js-bundler/class-aliasing/apps";
	private static final String sdkDefinition = "'bar':{'class':novox.LibClass,'className':'novox.LibClass','interface':novox.TheAliasInterface,'interfaceName':'novox.TheAliasInterface'},";
	
	private static final String defaultScenarioJson =
		"{" +
			sdkDefinition +
			"'alias #1':{'class':novox.g1.AliasClass1,'className':'novox.g1.AliasClass1'}," +
			"'alias #2':{'class':novox.g1.AliasClass2,'className':'novox.g1.AliasClass2'}" +
		"}";
	
	private static final String nullClassJson =
			"{" +
				sdkDefinition +
				"'alias #1':{}" +
			"}";
	
	private static final String testScenarioJson =
		"{" +
			sdkDefinition +
			"'novox.example.blade1.alias #1':{'class':novox.g1.FakeAliasClass1,'className':'novox.g1.FakeAliasClass1'}," +
			"'novox.example.blade1.alias #2':{'class':novox.g1.AliasClass2,'className':'novox.g1.AliasClass2'}" +
		"}";
	
	private static final String overriddenJson =
		"{" +
			sdkDefinition +
			"'novox.example.blade1.alias #1':{'class':novox.g1.AliasClass1,'className':'novox.g1.AliasClass1'}," +
			"'novox.example.blade1.alias #2':{'class':novox.OtherAliasClass,'className':'novox.OtherAliasClass'}" +
		"}";
	
	private static final String aliasGroupsJson =
		"{" +
			"'foo-bar':{'class':novox.LibClass,'className':'novox.LibClass','interface':novox.TheAliasInterface,'interfaceName':'novox.TheAliasInterface'}," +
			"'aliasWithNoDefaultClass':{'interface':novox.MyInterface,'interfaceName':'novox.MyInterface'}," +	
			"'novox.example.myAlias':{'class':novox.LibClass,'className':'novox.LibClass'}" +
		"}";
			
	private ScenarioAliases activeAliases;
	private AliasDefinition sdkFooBar = new AliasDefinition( null, "novox.LibClass", "novox.TheAliasInterface" );
	private AliasDefinition alias1 = new AliasDefinition( null, "novox.g1.AliasClass1", null );
	private AliasDefinition alias2 = new AliasDefinition( null, "novox.g1.AliasClass2", null );
	private AliasDefinition group1blade1alias1 = new AliasDefinition( null, "novox.g1.AliasClass1", null );
	private AliasDefinition group1blade1alias2 = new AliasDefinition( null, "novox.OtherAliasClass", null );
	private AliasDefinition group1blade1alias1test = new AliasDefinition( null, "novox.g1.FakeAliasClass1", null );
	private AliasDefinition group1blade1alias2test = new AliasDefinition( null, "novox.g1.AliasClass2", null );

	private File workbenchPathUsingGroups = new File(testBase + "/app-with-alias-groups/example-bladeset/blades/blade/workbench");
	private File workbenchPathhWithANonExistingGroup = new File(testBase + "/app-with-alias-groups/example2-bladeset/blades/blade/workbench");
	private File workbenchPathWithGroupsDefinedTwice = new File(testBase + "/app-with-alias-groups/example3-bladeset/blades/blade/workbench");
	private File workbenchPathChashingAliases = new File(testBase + "/app-with-alias-groups/example4-bladeset/blades/blade/workbench");
	private File appBaseFile = new File(testBase + "/app-with-alias-groups");

	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
		
		activeAliases = new ScenarioAliases();
		
		activeAliases.addAlias( "bar", sdkFooBar );
	}
	
	@Test @Ignore
	public void verifySingleAliasFile() throws ContentProcessingException
	{
		activeAliases.addAlias( "alias #1", alias1 );
		activeAliases.addAlias( "alias #2", alias2 );
		
		File testDir = new File(testBase, "single-alias-file/default-aspect");
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(testDir, null));
		AliasRegistry aliasRegistry = new AliasRegistry(testDir, null, validClasses);
		
		assertEquals(defaultScenarioJson, aliasRegistry.getJson(classDictionary, activeAliases));
	}
	
	@Test @Ignore
	public void verifyAliasFileCanOverrideAnAliasDefinitionsFile() throws ContentProcessingException
	{
		activeAliases.addAlias( "novox.example.blade1.alias #1", group1blade1alias1 );
		activeAliases.addAlias( "novox.example.blade1.alias #2", group1blade1alias2 );
		
		File testDir = new File(testBase, "alias-file-overriding-an-alias-definitions-file/default-aspect");
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(testDir, null));
		AliasRegistry aliasRegistry = new AliasRegistry(testDir, null, validClasses);
		
		assertEquals(overriddenJson, aliasRegistry.getJson(classDictionary, activeAliases));
	}
	
	@Test @Ignore
	public void verifyAliasFileCanSwitchScenario() throws ContentProcessingException
	{
		activeAliases.addAlias( "novox.example.blade1.alias #1", group1blade1alias1test );
		activeAliases.addAlias( "novox.example.blade1.alias #2", group1blade1alias2test );
		
		File testDir = new File(testBase, "alias-file-switching-scenario/default-aspect");
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(testDir, null));
		AliasRegistry aliasRegistry = new AliasRegistry(testDir, null, validClasses);
		
		assertEquals(testScenarioJson, aliasRegistry.getJson(classDictionary, activeAliases));
	}
	
	@Test @Ignore
	public void verifyAliasWithNoClassReturnsNull() throws ContentProcessingException
	{
		activeAliases.addAlias( "alias #1", new AliasDefinition(null, null, null));
		
		File testDir = new File(testBase, "single-alias-file/default-aspect");
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(testDir, null));
		AliasRegistry aliasRegistry = new AliasRegistry(testDir, null, validClasses);
		
		assertEquals(nullClassJson, aliasRegistry.getJson(classDictionary, activeAliases));
	}
	
	@Test @Ignore
	public void verifyAnAliasGetsAddedToGivenScenario() throws Exception {
		File testDir = new File(testBase, "single-alias-file/default-aspect");
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(testDir, null));
		AliasRegistry aliasRegistry = new AliasRegistry(testDir, null, validClasses);
		
		Aliases aliases = mock(Aliases.class);
		ScenarioAliases scenarioAliases = mock(ScenarioAliases.class);
		when(aliases.hasScenario("aScenario")).thenReturn(false);
		when(aliases.getScenarioAliases("aScenario")).thenReturn(scenarioAliases);
		
		aliasRegistry.aliasScenarios = aliases;
		
		AliasDefinition alias = new AliasDefinition("myAlias", "novox.g1.AliasClass1", "myInterface");
		
		aliasRegistry.addClassAlias(alias, "aScenario");
		
		verify(scenarioAliases).addAlias(eq("myAlias"), eq(alias));
	}
	
	@Test @Ignore
	public void verifyAliasesWithinAGroupGetAdded() throws ContentProcessingException
	{
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(workbenchPathUsingGroups, null));
		AliasRegistry aliasRegistry = new AliasRegistry(workbenchPathUsingGroups, appBaseFile, validClasses);
		
		assertEquals(aliasGroupsJson, aliasRegistry.getJson(classDictionary, aliasRegistry.getAliases()));
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void anExceptionIsThrownWhenTryingToUseANotDefinedGroup() throws ContentProcessingException
	{
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(workbenchPathhWithANonExistingGroup, null));
		new AliasRegistry(workbenchPathhWithANonExistingGroup, appBaseFile, validClasses);
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void anExceptionIsThrownIfAGroupIsDefinedTwice() throws ContentProcessingException
	{
		Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(workbenchPathWithGroupsDefinedTwice, null));
		new AliasRegistry(workbenchPathWithGroupsDefinedTwice, appBaseFile, validClasses);
	}
	
	@Test @Ignore
	public void anExceptionIsThrownIfAnAliasClashesWithTheSameAliasInAnotherGroup() throws ContentProcessingException
	{
		try {
			Set<String> validClasses = SourceFileLocator.createValidClasses(SourceFileLocator.getAllSourceFiles(workbenchPathChashingAliases, null));
			new AliasRegistry(workbenchPathChashingAliases, appBaseFile, validClasses);
			fail("exception expected");
		}
		catch(Exception e) {
			assertEquals(e.getMessage(), "Alias novox.example.myAlias has been defined in at least 2 groups: novox.myGroup1 and novox.myGroup");
		}
	}
	
}
