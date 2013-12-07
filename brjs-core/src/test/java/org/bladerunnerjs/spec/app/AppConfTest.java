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
	@Ignore
	@Test
	public void appConfWillHaveSensibleDefaultsIfItDoesntAlreadyExist() throws Exception {
		given(app).hasBeenCreated();
		when(app).appConf().write();
		then(app).fileHasContents("app.conf", "appNamespace: app1\nlocales: en");
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
		then(app).fileHasContents("app.conf", "appNamespace: appx\nlocales: de");
	}
	
	@Test
	public void updateAppNamespaceInAppConf() throws Exception {
		given(app).hasBeenPopulated("appx");
		when(app).appConf().setAppNamespace("newns").write();
		then(app).fileHasContents("app.conf", "appNamespace: newns\nlocales: en");
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
	public void readingAnAppConfFileWithMissingLocaleWillCauseAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "appNamespace: appns");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'locales' may not be null"));
	}

	@Test
	public void readingAnAppConfFileWithMissingAppNamespaceWillCauseAnException() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "\nlocales: en");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'appNamespace' may not be null"));
	}
	
	@Test
	public void readingAnAppConfFileWithEmptyLocaleWillCauseAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "appNamespace: appns\nlocales:");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'locales' may not be empty"));
	}
	
	@Test
	public void readingAnAppConfFileWithEmptyAppNamespaceWillCauseAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "appNamespace: \nlocales: en");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'appNamespace' may not be empty"));
	}
	
	@Test
	public void readingAnAppConfFileWithInvalidProperty() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "appNamespace: appns\nlocales: en\nblah: me");
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
	public void readingAnEmptyAppConfFileWillCauseAnException() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsFile("app.conf");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("is empty"));
	}
	
	@Test
	public void AppConfFileWithInvalidLocaleCauseExceptions() throws Exception{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "appNamespace: app\nlocales: en, en_GB, 123, de");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, app.file("app.conf").getPath(), unquoted("'123' not a valid locale"));
	}
}
