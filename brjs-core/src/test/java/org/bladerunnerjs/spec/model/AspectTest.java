package org.bladerunnerjs.spec.model;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.DuplicateAssetContainerException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
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
	
	@Test
	public void aspectIsBaselinedDuringPopulation() throws Exception {
		given(aspectTemplate).containsFolder("@appns")
			.and(aspectTemplate).containsFileWithContents("index.jsp", "'<html>@appns</html>'");
    	when(aspect).populate();
    	then(aspect).dirExists()
    		.and(app).hasDir("default-aspect")
    		.and(aspect).hasDir("appns")
    		.and(aspect).doesNotHaveDir("@appns")
    		.and(aspect).fileHasContents("index.jsp", "'<html>appns</html>'");
	}
	
	
	@Test
	public void invalidAspectNameSpaceThrowsException() throws Exception {
		when(badAspect).create();
		then(logging).errorMessageReceived(AbstractNode.Messages.NODE_CREATION_FAILED_LOG_MSG, "Aspect", badAspect.dir())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, badAspect.dir(), unquoted("'!#*'") );
	}
	
	@Test
	public void defaultAspectIsCorrectlyIdentified() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFile("index.html");
		then(app.defaultAspect()).dirExists()
			.and(app.defaultAspect()).hasFile("index.html");
	}
	
	@Test
	public void defaultAspectCanHaveItsOwnDirectory() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFile("default-aspect/index.html");
		then(app.defaultAspect()).dirExists()
			.and(app.defaultAspect()).hasFile("index.html");
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTwoDefaultBladesets() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFile("index.html")
    		.and(app).containsFile("default-aspect/index.html");
		when(app).aspectsListed();
		then(exceptions).verifyException(DuplicateAssetContainerException.class, "default Aspect", "apps/app1", "apps/app1/default-aspect");
	}
	
	@Test
	public void aspectsCannotHaveTheSameFormatAsLanguageLocale() throws Exception {
		then( (NamedNode) app.aspect("my") ).nameIsInvalid();
	}
	
	@Test
	public void aspectsCannotHaveTheSameFormatAsLanguageAndCountryLocale() throws Exception {
		then( (NamedNode) app.aspect("my_AS") ).nameIsInvalid();
	}
	
	@Test
	public void multipleAspectsAreDiscoveredWhenThereAreOtherDirectories() throws Exception {
		given(app).hasBeenPopulated()		
			.and(app).hasDir("a-folder")
			.and(app).hasDir("another-aspect")
			.and(app).hasDir("default-aspect")
			.and(app).hasDir("aspect1-aspect")
			.and(app).hasDir("aspect2-aspect")
			.and(app).hasDir("some-other-folder");
		/* then */ assertEquals( Arrays.asList(app.aspect("default"), app.aspect("another"), app.aspect("aspect1"), app.aspect("aspect2")), app.aspects() );
	}
	
}