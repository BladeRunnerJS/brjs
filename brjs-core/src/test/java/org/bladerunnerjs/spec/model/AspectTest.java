package org.bladerunnerjs.spec.model;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AspectTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Aspect badAspect;
	private NamedDirNode aspectTemplate;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspectTemplate = brjs.template("aspect");
			aspect = app.aspect("default");
			badAspect = app.aspect("!#*");
	}
	
	@Test
	public void parentGivesTheCorrectNode() throws Exception {
		when(aspect).create();
		then(aspect.parent()).isSameAs(app);
	}
	
	@Test
	public void dashAspectIsApendedToAspectNode() throws Exception {
		when(aspect).create();
		then(app).hasDir("default-aspect");
	}
	@Ignore //waiting for change to default appConf values, app namespace will be set to app name
	@Test
	public void aspectIsBaselinedDuringPopulation() throws Exception {
		given(aspectTemplate).containsFolder("@appns")
		.and(aspectTemplate).containsFileWithContents("index.jsp", "'<html>@appns</html>'");
	when(aspect).populate();
	then(aspect).dirExists()
		.and(app).hasDir("default-aspect")
		.and(aspect).hasDir("app1")
		.and(aspect).doesNotHaveDir("@appns")
		.and(aspect).fileHasContents("index.jsp", "'<html>app1</html>'");
	}
	
	
	@Test
	public void invalidAspectNameSpaceThrowsException() throws Exception {
		when(badAspect).create();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Aspect", badAspect.dir())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, badAspect.dir(), unquoted("'!#*'") );
	}
}