package org.bladerunnerjs.spec.app;

import static org.bladerunnerjs.yaml.YamlAppConf.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.TemplateGroup;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class AppConfTest extends SpecTest {
	private App app;
	private App JSKeyWordApp;
	@SuppressWarnings("unused")
	private App reseverWordApp;
	private TemplateGroup templates;
	
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			app = brjs.app("app1");
			JSKeyWordApp = brjs.app("if");
			reseverWordApp = brjs.app("caplin");
			templates = brjs.sdkTemplateGroup("default");
	}
	
	
	// TODO: add a test that shows the object updates if the conf file is modified
	@Test
	public void appConfWillHaveSensibleDefaultsIfItDoesntAlreadyExist() throws Exception {
		given(app).hasBeenCreated();
		when(app).appConf().write();
		then(app).fileHasContents("app.conf", "localeCookieName: BRJS.LOCALE\nlocales: en\nrequirePrefix: appns");
	}
	
	@Test
	public void requirePrefixCanAlsoBeSetUsingLegacyAppNamespaceProperty() throws Exception {
		given(app).hasBeenCreated()
			.and(logging).enabled()
			.and(app).containsFileWithContents("app.conf", "appNamespace: requireprefix");
		when(app).appConfHasBeenRead();
		then(app.appConf().getRequirePrefix().toString()).textEquals("requireprefix")
			.and(logging).warnMessageReceived(APP_NAMESPACE_PROPERTY_DEPRECATED);
	}
	
	@Test
	public void havingBothARequirePrefixAndAnAppNamespacePropertyCausesAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "appNamespace: requireprefix\nrequirePrefix: requireprefix");
		when(app).appConfHasBeenRead();
		then(exceptions).verifyException(ConfigException.class, "appNamespace", "requirePrefix");
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
		given(templates).templateGroupCreated()
			.and(app).hasBeenPopulated("appx", "default");
		when(app).appConf().setLocales("de").write();
		then(app).fileHasContents("app.conf", "localeCookieName: BRJS.LOCALE\nlocales: de\nrequirePrefix: appx");
	}
	@Test
	public void updateRequirePrefixInAppConf() throws Exception {
		given(templates).templateGroupCreated()
			.and(app).hasBeenPopulated("appx", "default");
		when(app).appConf().setRequirePrefix("newns").write();
		then(app).fileHasContents("app.conf", "localeCookieName: BRJS.LOCALE\nlocales: en\nrequirePrefix: newns");
	}
	
	@Test
	public void settingRequirePrefixToJSKeywordCausesException() throws Exception {
		when(app).appConf().setRequirePrefix("try");
		then(exceptions).verifyException(InvalidPackageNameException.class, "try", app.dir().getPath());
	}
	
	@Test
	public void settingRequirePrefixToReservedWordIsAllowedInConf() throws Exception {
		when(app).appConf().setRequirePrefix("caplin");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void readingAnAppConfFileWithMissingLocaleWillUseADefault() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: appns");
		then(app.appConf().getDefaultLocale().toString()).textEquals("en");
	}

	@Test
	public void readingAnAppConfFileWithMissingRequirePrefixWillUseADefault() throws Exception{
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
	public void readingAnAppConfFileWithEmptyRequirePrefixWillCauseAnException() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: \nlocales: en");
		when(app).appConf();
		then(exceptions).verifyException(ConfigException.class, unquoted("'requirePrefix' may not be empty"));
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
		then(exceptions).verifyException(IllegalArgumentException.class, unquoted("'123' is not a valid locale"))
			.whereTopLevelExceptionIs(ConfigException.class, unquoted(app.file("app.conf").getPath()));
	}
}
