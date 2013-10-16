package org.bladerunnerjs.spec.app;

import static org.bladerunnerjs.model.App.Messages.APP_DEPLOYED_LOG_MSG;
import static org.bladerunnerjs.model.App.Messages.APP_DEPLOYMENT_FAILED_LOG_MSG;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.events.AppDeployedEvent;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AppTest extends SpecTest {
	private App app;
	private NamedDirNode appTemplate;
	private DirNode appJars;
	private Aspect aspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appTemplate = brjs.template("app");
			appJars = brjs.appJars();
			aspect = app.aspect("default");
	}
	
	// TODO: does this add anything over the baselining test?
	@Test
	public void weCanCreateAnAppUsingATemplate() throws Exception {
		given(appTemplate).containsFile("some-file.blah")
			.and(logging).enabled();
		when(app).populate("appx");
		then(app).dirExists()
			.and(app).hasFile("some-file.blah")
			.and(logging).noMessagesLogged();
	}
	
	@Test
	public void populatingAnAppCausesRootObserversToBeNotified() throws Exception {
		given(observer).observing(brjs);
		when(app).populate();
		then(observer).notified(NodeReadyEvent.class, app)
			.and(observer).notified(NodeReadyEvent.class, aspect)
			.and(observer).notified(NodeReadyEvent.class, aspect.theme("standard"));
	}
	
	@Test
	public void theAppConfIsWrittenOnPopulate() throws Exception {
		given(appTemplate).hasBeenCreated();
		when(app).populate("appx");
		then(app).fileHasContents("app.conf", "appNamespace: appx\nlocales: en");
	}
	
	@Test
	public void theAppConfIsNotWrittenOnZeroArgPopulate() throws Exception {
		given(appTemplate).hasBeenCreated();
		when(app).populate();
		then(app).doesNotHaveFile("app.conf");
	}
	@Ignore
	@Test
	public void theAppConfCanBeManuallyWrittenOnZeroArgPopulate() throws Exception {
		given(appTemplate).hasBeenCreated()
			.and(app).hasBeenPopulated();
		when(app).appConf().write();
		then(app).fileHasContents("app.conf", "appNamespace: app1\nlocales: en");
	}
	
	@Test
	public void invalidAppNameSpaceThrowsException() throws Exception {
		given(appTemplate).containsFile("some-file.blah");
		when(app).populate("appX");
		then(exceptions).verifyException(InvalidPackageNameException.class, app.dir(), unquoted("'appX'"));
	}
	
	@Test
	public void usingJSKeywordAsAppNameSpaceThrowsException() throws Exception {
		given(appTemplate).containsFile("some-file.blah");
		when(app).populate("transient");
		then(exceptions).verifyException(InvalidPackageNameException.class, app.dir(), unquoted("'transient'"));
	}
	
	@Test
	public void usingReservedKeywordAsAppNameSpaceThrowsException() throws Exception {
		given(appTemplate).containsFile("some-file.blah");
		when(app).populate("caplinx");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, app.dir(), unquoted("'caplinx'"));
	}
	
	@Test
	public void appIsBaselinedDuringPopulation() throws Exception {
		given(appTemplate).containsFolder("@appns");
		when(app).populate("appx");
		then(app).dirExists()
			.and(app).hasDir("appx")
			.and(app).doesNotHaveDir("appns");
	}
	
	@Test
	public void appLibsAreInstalledWhenAppIsDeployed() throws Exception {
		given(app).hasBeenCreated()
			.and(appJars).containsFile("some-lib.jar")
			.and(logging).enabled();
		when(app).deployApp();
		then(app).hasFile("WEB-INF/lib/some-lib.jar")
			.and(logging).infoMessageReceived(APP_DEPLOYED_LOG_MSG, "app1", app.dir().getPath());
	}
	
	@Test
	public void deployedEventIsFiredWhenAppDeployed() throws Exception {
		given(app).hasBeenCreated()
			.and(appJars).containsFile("some-lib.jar")
			.and(observer).observing(brjs);
		when(app).deployApp();
		then(observer).notified(AppDeployedEvent.class, app);
	}
	
	// TODO: use this test (or a test like it) to verify that we log about population before logging about app deployment, so if it fails you
	// have a better idea what's going on
	@Test
	public void exceptionIsThrownIfThereAreNoAppLibs() throws Exception {
		given(app).hasBeenCreated()
			.and(logging).enabled();
		when(app).deployApp();
		then(logging).errorMessageReceived(APP_DEPLOYMENT_FAILED_LOG_MSG, app.getName(), app.dir().getPath())
			.and(exceptions).verifyException(IllegalStateException.class, appJars.dir().getPath());
	}
}
