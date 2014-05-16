package org.bladerunnerjs.spec.app;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AppConfTest extends SpecTest {
	private App app;
	private App JSKeyWordApp;
	@SuppressWarnings("unused")
	private App reseverWordApp;
	
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			JSKeyWordApp = brjs.app("if");
			reseverWordApp = brjs.app("caplin");
	}
	
	
	// TODO: add a test that shows the object updates if the conf file is modified
	@Test
	public void appConfWillHaveSensibleDefaultsIfItDoesntAlreadyExist() throws Exception {
		given(app).hasBeenCreated();
		when(app).appConf().write();
		then(app).fileHasContents("app.conf", "locales: en\nrequirePrefix: appns");
	}
	
	@Ignore
	@Test
	public void exceptionThrownWhenSettingInvalidAppNameAsDefaultNamespace() throws Exception {
		given(JSKeyWordApp).hasBeenCreated();
		when(JSKeyWordApp).appConf().write();
		then(exceptions).verifyException(InvalidPackageNameException.class, "if", JSKeyWordApp.dir().getPath());
	}
	
	@Test
	public void updateLocaleInAppConf() throws Exception {
		given(app).hasBeenPopulated("appx");
		when(app).appConf().setLocales("de").write();
		then(app).fileHasContents("app.conf", "locales: de\nrequirePrefix: appx");
	}
	
	@Test
	public void updateAppNamespaceInAppConf() throws Exception {
		given(app).hasBeenPopulated("appx");
		when(app).appConf().setAppNamespace("newns").write();
		then(app).fileHasContents("app.conf", "locales: en\nrequirePrefix: newns");
	}
	
	@Test
	public void settingAppNamespaceToJSKeywordCausesException() throws Exception {
		when(app).appConf().setAppNamespace("try");
		then(exceptions).verifyException(InvalidPackageNameException.class, "try", app.dir().getPath());
	}
	
	@Test
	public void settingAppNamespaceToReservedWordIsAllowedInConf() throws Exception {
		when(app).appConf().setAppNamespace("caplin");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void readingAnAppConfFileWithMissingLocaleWillUseADefault() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: appns");
		then(app.appConf().getLocales()).textEquals("en");
	}

	@Test
	public void readingAnAppConfFileWithMissingAppNamespaceWillUseADefault() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "\nlocales: en");
		then(app.appConf().getRequirePrefix()).textEquals("appns");
	}
	
	@Test
	public void readingAnAppConfFileWithEmptyLocaleWillCauseAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: appns\nlocales:");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'locales' may not be empty"));
	}
	
	@Test
	public void readingAnAppConfFileWithEmptyAppNamespaceWillCauseAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: \nlocales: en");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'requirePrefix' may not be empty"));
	}
	
	@Test
	public void readingAnAppConfFileWithInvalidProperty() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: appns\nlocales: en\nblah: me");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("Unable to find property 'blah'"));
	}
	
	@Test
	public void malformedAppConfCausesException() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "blah");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("Line 0, column 4"));
	}
	
	@Test
	public void readingAnEmptyAppConfFileWillUseDefaults() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsEmptyFile("app.conf");
		then(app.appConf().getRequirePrefix()).textEquals("appns");
	}
	
	@Test
	public void AppConfFileWithInvalidLocaleCauseExceptions() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: app\nlocales: en, en_GB, 123, de");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'123' not a valid locale"));
	}
}
