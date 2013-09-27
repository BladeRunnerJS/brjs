package org.bladerunnerjs.spec.blade;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BladeTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade1;
	private Blade blade2;
	private Blade bladeWithInvalidName;
	private Blade bladeWithJSKeyWordName;
	private NamedDirNode bladeTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
		app = brjs.app("app");
		aspect = app.aspect("default");
		bladeset = app.bladeset("bs");
		blade1 = bladeset.blade("b1");
		blade2 = bladeset.blade("b2");
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
			.and(observer).notified(NodeReadyEvent.class, blade1.theme("standard"))
			.and(observer).notified(NodeReadyEvent.class, blade1.workbench());
	}
	
	@Test
	public void invalidBladeNameSpaceThrowsException() throws Exception {
		when(bladeWithInvalidName).populate();
		then(exceptions).verifyException(InvalidDirectoryNameException.class, bladeWithInvalidName.dir(), "_-=+");
	}
	
	@Test
	public void usingJSKeywordAsBladeNameSpaceThrowsException() throws Exception {
		when(bladeWithJSKeyWordName).populate();
		then(exceptions).verifyException(InvalidPackageNameException.class, bladeWithJSKeyWordName.dir(), "export");
	}
	@Ignore //waiting for change to default appConf values, app namespace will be set to app name
	@Test
	public void bladeIsBaselinedDuringPopulation() throws Exception {
		given(bladeTemplate).containsFolder("@blade")
			.and(bladeTemplate).containsFileWithContents("MyClass.js", "@appns.@bladeset.@blade = function() {};");
		when(blade1).populate();
		then(blade1).hasDir(blade1.getName())
			.and(blade1).doesNotHaveDir("@blade")
			.and(blade1).fileHasContents("MyClass.js", "app.bs.b1 = function() {};");
	}
	
	//TODO: waiting for bundlers to be implemented
	@Ignore
	@Test
	public void classesWithinABladeCantReferenceClassesInOtherBlades() throws Exception {
		given(blade1).hasClass("blade.Class1")
			.and(blade2).classRefersTo("blade2.Class2", "blade.Class1")
			.and(aspect).indexPageRefersTo("blade2.Class2");
		when(aspect).getBundleInfo();
//		then(exceptions).verifyException(BundleSetException.class, blade2.getName() //some other information);
	}
}
