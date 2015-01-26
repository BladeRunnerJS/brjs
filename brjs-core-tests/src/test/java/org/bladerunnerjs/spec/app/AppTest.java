package org.bladerunnerjs.spec.app;

import static org.junit.Assert.assertEquals;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.model.events.AppDeployedEvent;
import org.bladerunnerjs.api.model.events.NodeReadyEvent;
import org.bladerunnerjs.api.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.TemplateGroup;
import org.junit.Before;
import org.junit.Test;


public class AppTest extends SpecTest {
	
	private JsLib sdkLib;
	private JsLib globalNonBladeRunnerLib;
	private JsLib appNonBladeRunnerLib;
	private JsLib globalOverriddenNonBRLib;
	private JsLib appOverriddenNonBRLib;
	private App app;
	private JsLib appLib;
	private NamedDirNode appTemplate;
	private DirNode appJars;
	private Aspect defaultAspect;
	private TemplateGroup templates;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			sdkLib = brjs.sdkLib("br");
			app = brjs.app("app1");
			appLib = app.jsLib("lib1");
			templates = brjs.sdkTemplateGroup("default");
			appTemplate = templates.template("app");
			appJars = brjs.appJars();
			defaultAspect = app.defaultAspect();
			globalNonBladeRunnerLib = brjs.sdkLib("legacy-thirdparty");
			appNonBladeRunnerLib = app.appJsLib("app-legacy-thirdparty");
			globalOverriddenNonBRLib = brjs.sdkLib("overridden-lib");
			appOverriddenNonBRLib = app.appJsLib("overridden-lib");
	}
	
	// TODO: does this add anything over the baselining test?
	@Test
	public void weCanCreateAnAppUsingATemplate() throws Exception {
		given(templates).templateGroupCreated()
			.and(appTemplate).containsFile("some-file.blah")
			.and(logging).enabled();
		when(app).populate("appx", "default");
		then(app).dirExists()
			.and(app).hasFile("some-file.blah")
			.and(logging).noMessagesLogged();
	}
	
	@Test
	public void weCanCreateAnAppUsingTheRealTemplate() throws Exception {
		given(brjs).usesProductionTemplates();
		when(app).populate("appxyz", "default");
		then(defaultAspect).hasFile("src/App.js");
	}
	
	@Test
	public void populatingAnAppCausesRootObserversToBeNotified() throws Exception {
		given(templates).templateGroupCreated()
			.and(observer).observing(brjs);
		when(app).populate("default");
		then(observer).notified(NodeReadyEvent.class, app)
			.and(observer).notified(NodeReadyEvent.class, defaultAspect);
	}
	
	@Test
	public void theAppConfIsWrittenOnPopulate() throws Exception {
		given(templates).templateGroupCreated();
		when(app).populate("appx", "default");
		then(app).fileHasContents("app.conf", "localeCookieName: BRJS.LOCALE\nlocales: en\nrequirePrefix: appx");
	}
	
	@Test
	public void theAppConfIsNotWrittenOnZeroArgPopulate() throws Exception {
		given(templates).templateGroupCreated();
		when(app).populate("default");
		then(app).doesNotHaveFile("app.conf");
	}
	
	@Test
	public void theAppConfCanBeManuallyWrittenOnZeroArgPopulate() throws Exception {
		given(templates).templateGroupCreated()
			.and(app).hasBeenPopulated("default");
		when(app).appConf().write();
		then(app).fileHasContents("app.conf", "localeCookieName: BRJS.LOCALE\nlocales: en\nrequirePrefix: appns");
	}
	
	@Test
	public void invalidAppNameSpaceThrowsException() throws Exception {
		given(appTemplate).containsFile("some-file.blah");
		when(app).populate("appX", "default");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, app.dir(), unquoted("'appX'"));
	}
	
	@Test
	public void usingJSKeywordAsAppNameSpaceThrowsException() throws Exception {
		given(appTemplate).containsFile("some-file.blah");
		when(app).populate("transient", "default");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, app.dir(), unquoted("'transient'"));
	}
	
	@Test
	public void usingReservedKeywordAsAppNameSpaceThrowsException() throws Exception {
		given(appTemplate).containsFile("some-file.blah");
		when(app).populate("caplinx", "default");
		then(exceptions).verifyException(InvalidRootPackageNameException.class, app.dir(), unquoted("'caplinx'"));
	}
	
	@Test
	public void appIsBaselinedDuringPopulation() throws Exception {
		given(templates).templateGroupCreated()
			.and(appTemplate).containsFolder("@appns");
		when(app).populate("appx", "default");
		then(app).dirExists()
			.and(app).hasDir("appx")
			.and(app).doesNotHaveDir("appns");
	}
	
	@Test
	public void deployedEventIsFiredWhenAppDeployed() throws Exception {
		given(app).hasBeenCreated()
			.and(appJars).containsFile("some-lib.jar")
			.and(observer).observing(brjs);
		when(app).deployApp();
		then(observer).notified(AppDeployedEvent.class, app);
	}
	
	@Test
	public void globalLibsAreWrappedSoTheCorrectAppIsReturned() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).hasBeenCreated()
			.and(sdkLib).hasBeenCreated();
		given(app).hasLibs(sdkLib, appLib);
		then(app).libsReturnCorrectApp();
	}
	
	@Test
	public void nonBladerunnerLibsAreWrappedSoTheCorrectAppIsReturned() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).hasBeenCreated()
			.and(sdkLib).hasBeenCreated()
			.and(globalNonBladeRunnerLib).hasBeenCreated()
			.and(appNonBladeRunnerLib).hasBeenCreated();
		given(app).hasLibs(sdkLib, globalNonBladeRunnerLib, appNonBladeRunnerLib, appLib);
		then(app).libsReturnCorrectApp();
	}
	
	@Test
	public void appLibsContainBothAppLibsAndSdkLibs() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).hasBeenCreated()
			.and(sdkLib).hasBeenCreated();
		then(app).hasLibs(sdkLib, appLib);
	}
	
	@Test
	public void appLibsContainBothAppLibsAndNonBladerunnerLibs() throws Exception {
		given(app).hasBeenCreated()
    		.and(appLib).hasBeenCreated()
    		.and(sdkLib).hasBeenCreated()
    		.and(globalNonBladeRunnerLib).hasBeenCreated()
    		.and(appNonBladeRunnerLib).hasBeenCreated();
		then(app).hasLibs(sdkLib, globalNonBladeRunnerLib, appNonBladeRunnerLib, appLib);
	}
	
	@Test
	public void overriddenLibsDontAppearTwiceInLibsList() throws Exception {
		given(app).hasBeenCreated()
    		.and(appLib).hasBeenCreated()
    		.and(sdkLib).hasBeenCreated()
    		.and(globalOverriddenNonBRLib).hasBeenCreated()
    		.and(appOverriddenNonBRLib).hasBeenCreated();
		then(app).hasLibs(sdkLib, appOverriddenNonBRLib, appLib);
	}
	
	@Test
	public void appNonBRLibsCanOverrideGlobalLibs() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).hasBeenCreated()
			.and(globalOverriddenNonBRLib).hasBeenCreated()
			.and(appOverriddenNonBRLib).hasBeenCreated();
		then(app).libWithNameIs("overridden-lib", appOverriddenNonBRLib);
	}
	
	@Test
	public void appIsAvailableImmediatelyAfterCreationSinceFileModificationServiceListensForReadyEvent() throws Exception {
		given(templates).templateGroupCreated()
			.and(brjs).hasBeenAuthenticallyCreated()
			.and(brjs).appsHaveBeeniterated()
			.and(brjs).hasBeenInactiveForOneMillisecond();
		when(brjs.app("app1")).populate("default");
		then(brjs).hasApps("app1");
	}
	
	@Test
	public void correctUnversionedBundleRequestsAreCreated() throws Exception
	{
		assertEquals("/mock-content-plugin/some file", app.requestHandler().createBundleRequest(app.defaultAspect(), "/mock-content-plugin/some file", "dev"));
		assertEquals("v/dev/mock-content-plugin/some file", app.requestHandler().createBundleRequest(app.defaultAspect(), "mock-content-plugin/some file", "dev"));
	}
	
}
