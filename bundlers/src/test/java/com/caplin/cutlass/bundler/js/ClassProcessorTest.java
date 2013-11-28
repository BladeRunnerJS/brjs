package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.bundler.js.aliasing.AliasDefinition;
import com.caplin.cutlass.bundler.js.aliasing.AliasInformation;
import com.caplin.cutlass.bundler.js.aliasing.ScenarioAliases;

public class ClassProcessorTest {
	
	private final ClassesTrie classTrie = new ClassesTrie();
	private final ClassDictionary classDictionary = new ClassDictionary();
	private final ClassDictionary patchDictionary = new ClassDictionary();
	private ClassProcessor classProcessor;
	private final File classContainingAliases = new File( "src/test/resources/js-bundler/class-processor/Class1.js" );
	
	private final AliasDefinition aliasDefinition = new AliasDefinition( null, "a", "i" );
	private final ScenarioAliases scenarioAliases = new ScenarioAliases();
	private final AliasInformation aliasInformation = new AliasInformation( "alias", aliasDefinition, scenarioAliases );
	private final AliasInformation notpresentAliasInformation = new AliasInformation( "unused-alias", aliasDefinition, scenarioAliases );
	private final AliasInformation namespacedAliasInformation = new AliasInformation( "namespaced.alias", aliasDefinition, scenarioAliases );
	
	@Before
	public void setUp() {
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(classContainingAliases));
		classProcessor = new ClassProcessor( classTrie, classDictionary, patchDictionary );
	}
	
	@Test
	public void getAliasesDefinedInDependency() throws BundlerProcessingException {
		//Given.
		classTrie.addAlias( "alias", aliasInformation );
		classTrie.addAlias( "unused-alias", notpresentAliasInformation );
		classTrie.addAlias( "namespaced.alias", namespacedAliasInformation );
		
		//Doing.
		classProcessor.getClassDependencies( classContainingAliases );
		ScenarioAliases activeAliases = classProcessor.getActiveAliases();
		
		//Should.
		Set<String> aliasNames = activeAliases.getAliasNames();
		
		assertEquals( 2, aliasNames.size() );
		assertThat( aliasNames, hasItem( "alias" ) );
		assertThat( aliasNames, hasItem( "namespaced.alias" ) );
	}
}
