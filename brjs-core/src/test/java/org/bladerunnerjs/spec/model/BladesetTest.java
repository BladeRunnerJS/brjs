package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BladesetTest extends SpecTest {
	App app;
	Bladeset bladeset;
	Bladeset invalidBladesetName;
	Bladeset JSKeywordBladesetName;
	Bladeset invalidPackageName;
	private NamedDirNode bladesetTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladesetTemplate = brjs.template("bladeset");
			bladeset = app.bladeset("bs");
			invalidBladesetName = app.bladeset("#Invalid");
			JSKeywordBladesetName = app.bladeset("else");
			invalidPackageName = app.bladeset("_invalid");
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		given(bladeset).hasBeenCreated();
		then(bladeset.parent()).isSameAs(app);
	}
	
	@Test
	public void dashBladesetIsApendedToBladeSetNode() throws Exception {
		when(bladeset).create();
		then(app).hasDir("bs-bladeset");
	}
	@Ignore //waiting for change to default appConf values, app namespace will be set to app name
	@Test
	public void bladesetIsBaselinedDuringPopulation() throws Exception {
		given(bladesetTemplate).containsFolder("@bladeset")
			.and(bladesetTemplate).containsFileWithContents("class1.js", "@appns.@bladeset = function() {};");
		when(bladeset).populate();
		then(bladeset).hasDir(bladeset.getName())
			.and(bladeset).doesNotHaveDir("@bladeset")
			.and(bladeset).fileHasContents("class1.js", "app.bs = function() {};");
	}
	
	@Test
	public void populatingABladesetCausesAppObserversToBeNotified() throws Exception
	{
		given(app).hasBeenPopulated()
			.and(observer).observing(app)
			.and(observer).allNotificationsHandled();
		when(bladeset).populate();
		then(observer).notified(NodeReadyEvent.class, bladeset)
			.and(observer).notified(NodeReadyEvent.class, bladeset.testType("unit").testTech("js-test-driver"));
	}
	
	@Test
	public void invalidBladesetDirectoryNameSpaceThrowsException() throws Exception {
		when(invalidBladesetName).populate();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Bladeset", invalidBladesetName.dir())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class,invalidBladesetName.dir(), "#Invalid");
	}
	
	@Test
	public void usingJSKeywordAsBladesetNameSpaceThrowsException() throws Exception {
		when(JSKeywordBladesetName).populate();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Bladeset", JSKeywordBladesetName.dir())
			.and(exceptions).verifyException(InvalidPackageNameException.class,JSKeywordBladesetName.dir(), "else");
	}
	
	@Test
	public void invalidBladesetPackageNameSpaceThrowsException() throws Exception {
		when(invalidPackageName).populate();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Bladeset", invalidPackageName.dir())
			.and(exceptions).verifyException(InvalidPackageNameException.class,invalidPackageName.dir(), "_invalid");
	}
}