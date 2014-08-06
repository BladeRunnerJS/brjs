package org.bladerunnerjs.spec.model;

import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BladeTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private Blade blade1;
	private Blade bladeWithInvalidName;
	private Blade bladeWithJSKeyWordName;
	private NamedDirNode bladeTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsAssetLocationPlugins()
			.and(brjs).automaticallyFindsAssetPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladeset = app.bladeset("bs");
			blade1 = bladeset.blade("b1");
			bladeWithInvalidName = bladeset.blade("_-=+");
			bladeWithJSKeyWordName = bladeset.blade("export");
			bladeTemplate = brjs.template("blade");
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		when(blade1).create();
		then(blade1.parent()).isSameAs(bladeset);
	}
	
	@Test
	public void populatingABladeCausesBladesetObserversToBeNotified() throws Exception {
		given(observer).observing(brjs);
		when(blade1).populate();
		then(observer).notified(NodeReadyEvent.class, blade1)
			.and(observer).notified(NodeReadyEvent.class, blade1.testType("unit").testTech("js-test-driver"))
			.and(observer).notified(NodeReadyEvent.class, blade1.workbench());
	}
	
	@Test
	public void invalidBladeNameSpaceThrowsException() throws Exception {
		when(bladeWithInvalidName).populate();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Blade", bladeWithInvalidName.dir())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, bladeWithInvalidName.dir(), "_-=+");
	}
	
	@Test
	public void usingJSKeywordAsBladeNameSpaceThrowsException() throws Exception {
		when(bladeWithJSKeyWordName).populate();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Blade", bladeWithJSKeyWordName.dir())
			.and(exceptions).verifyException(InvalidPackageNameException.class, bladeWithJSKeyWordName.dir(), "export");
	}
	
	@Test
	public void bladeIsBaselinedDuringPopulation() throws Exception {
		given(bladeTemplate).containsFolder("@blade")
			.and(bladeTemplate).containsFileWithContents("MyClass.js", "@appns.@bladeset.@blade = function() {};");
		when(blade1).populate();
		then(blade1).hasDir(blade1.getName())
			.and(blade1).doesNotHaveDir("@blade")
			.and(blade1).fileHasContents("MyClass.js", "appns.bs.b1 = function() {};");
	}
	
	@Test
	public void bladeHasClassNameTranformAddedDuringPopulation() throws Exception {
		String expectedClassName = WordUtils.capitalize( blade1.getName() );
		given(bladeTemplate).containsFolder("@blade")
			.and(bladeTemplate).containsFileWithContents("@bladeTitle.js", "function @bladeTitle(){}");
		when(blade1).populate();
		then(blade1).hasDir(blade1.getName())
			.and(blade1).fileHasContents(expectedClassName + ".js", "function " + expectedClassName + "(){}");
	}
	
	@Test
	public void populatingABladeIntoANamespacedJsBladesetCausesAJsStyleFileToBeCreated() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle();
		when(blade1).populate();
		then(blade1).hasFile(".js-style");
	}
	
	@Test
	public void populatingABladeIntoACommonJsBladesetDoesNotCausesAJsStyleFileToBeCreated() throws Exception {
		given(bladeset).hasBeenCreated();
		when(blade1).populate();
		then(blade1).doesNotHaveFile(".js-style");
	}
}
