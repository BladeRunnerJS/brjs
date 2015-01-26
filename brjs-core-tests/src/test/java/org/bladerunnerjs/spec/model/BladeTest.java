package org.bladerunnerjs.spec.model;

import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.events.NodeReadyEvent;
import org.bladerunnerjs.api.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.api.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.junit.Before;
import org.junit.Test;


public class BladeTest extends SpecTest {
	private App app;
	private Bladeset bladeset;
	private Blade blade1;
	private Blade bladeWithInvalidName;
	private Blade bladeWithJSKeyWordName;
	private NamedDirNode bladeTemplate;
	private TemplateGroup templates;
	
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
			templates = brjs.sdkTemplateGroup("default");
			bladeTemplate = templates.template("blade");
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		when(blade1).create();
		then(blade1.parent()).isSameAs(bladeset);
	}
	
	@Test
	public void populatingABladeCausesBladesetObserversToBeNotified() throws Exception {
		given(observer).observing(brjs)
			.and(templates).templateGroupCreated();
		when(blade1).populate("default");
		then(observer).notified(NodeReadyEvent.class, blade1)
			.and(observer).notified(NodeReadyEvent.class, blade1.testType("unit").defaultTestTech())
			.and(observer).notified(NodeReadyEvent.class, blade1.workbench());
	}
	
	@Test
	public void invalidBladeNameSpaceThrowsException() throws Exception {
		when(bladeWithInvalidName).populate("default");
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Blade", bladeWithInvalidName.dir())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, bladeWithInvalidName.dir(), "_-=+");
	}
	
	@Test
	public void usingJSKeywordAsBladeNameSpaceThrowsException() throws Exception {
		when(bladeWithJSKeyWordName).populate("default");
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Blade", bladeWithJSKeyWordName.dir())
			.and(exceptions).verifyException(InvalidPackageNameException.class, bladeWithJSKeyWordName.dir(), "export");
	}
	
	@Test
	public void bladeIsBaselinedDuringPopulation() throws Exception {
		given(templates).templateGroupCreated()
			.and(bladeTemplate).containsFolder("@blade")
			.and(bladeTemplate).containsFileWithContents("MyClass.js", "@appns.@bladeset.@blade = function() {};");
		when(blade1).populate("default");
		then(blade1).hasDir(blade1.getName())
			.and(blade1).doesNotHaveDir("@blade")
			.and(blade1).fileHasContents("MyClass.js", "appns.bs.b1 = function() {};");
	}
	
	@Test
	public void bladeHasClassNameTranformAddedDuringPopulation() throws Exception {
		String expectedClassName = WordUtils.capitalize( blade1.getName() );
		given(templates).templateGroupCreated()
			.and(bladeTemplate).containsFolder("@blade")
			.and(bladeTemplate).containsFileWithContents("@bladeTitle.js", "function @bladeTitle(){}");
		when(blade1).populate("default");
		then(blade1).hasDir(blade1.getName())
			.and(blade1).fileHasContents(expectedClassName + ".js", "function " + expectedClassName + "(){}");
	}
	
	@Test
	public void populatingABladeIntoANamespacedJsBladesetCausesAJsStyleFileToBeCreated() throws Exception {
		given(bladeset).hasNamespacedJsPackageStyle()
			.and(templates).templateGroupCreated();
		when(blade1).populate("default");
		then(blade1).hasFile(".js-style");
	}
	
	@Test
	public void populatingABladeIntoACommonJsBladesetDoesNotCausesAJsStyleFileToBeCreated() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(templates).templateGroupCreated();
		when(blade1).populate("default");
		then(blade1).doesNotHaveFile(".js-style");
	}
}
