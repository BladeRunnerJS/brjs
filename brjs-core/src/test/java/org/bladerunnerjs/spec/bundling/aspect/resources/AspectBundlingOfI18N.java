package org.bladerunnerjs.spec.bundling.aspect.resources;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfI18N extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	private JsLib sdkLib, userLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			sdkLib = brjs.sdkLib("br");
			userLib = app.jsLib("userLib");
	}
	
	// Aspect
	@Test
	public void aspectI18NFilesAreBundledWhenAspectSrcAreReferenced() throws Exception {
		given(aspect).hasClass("appns/Class1")
			.and(aspect).containsResourceFileWithContents("i18n/en/en.properties", "appns.token=aspect token")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).containsText("\"appns.token\": \"aspect token\"");
	}

	@Test
	public void appCanHaveMultipleLocales() throws Exception {
		
		StringBuffer enResponse = new StringBuffer();
		StringBuffer deResponse = new StringBuffer();
		
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: app1\nlocales: en_EN, de_DE")
			.and(aspect).containsResourceFileWithContents("i18n/en/en.properties", "app1.token = english")
			.and(aspect).containsResourceFileWithContents("i18n/de/de.properties", "app1.token = german")
			.and(aspect).hasClass("Class1")
			.and(aspect).indexPageHasContent("default aspect");
		when(aspect).requestReceivedInDev("i18n/en.js", enResponse)
			.and(aspect).requestReceivedInDev("i18n/de.js", deResponse);
		then(enResponse).containsText("app1.token\": \"english")
			.and(enResponse).doesNotContainText("german")
			.and(deResponse).containsText("app1.token\": \"german")
			.and(deResponse).doesNotContainText("english");
	}
	
//	// Bladeset
	@Test
	public void bladesetI18NFilesAreBundledWhenBladesetSrcAreReferenced() throws Exception {
		given(bladeset).hasClasses("appns/bs/Class1")
			.and(bladeset).containsResourceFileWithContents("i18n/en/en.properties", "appns.bs.token=bladeset token")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).containsText("\"appns.bs.token\": \"bladeset token\"");
	}
	
	// Blade
	@Test
	public void bladeI18NFilesAreBundledWhenBladeSrcIsReferenced() throws Exception {
		given(blade).hasClasses("appns/bs/b1/Class1")
			.and(blade).containsResourceFileWithContents("i18n/en/en.properties", "appns.bs.b1.token=blade token")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).containsText("\"appns.bs.b1.token\": \"blade token\"");
	}
	
	// SDK BRJS Lib
	@Test
	public void aspectCanBundleSdkLibHtmlResources() throws Exception {
		given(sdkLib).hasBeenCreated()
			.and(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).containsResourceFileWithContents("i18n/en/en.properties", "br.sdktoken=library token")
			.and(sdkLib).hasClass("br.workbench.ui.Workbench")
			.and(aspect).indexPageRefersTo("br.workbench.ui.Workbench");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).containsText("\"br.sdktoken\": \"library token\"");
	}
	
	// User library (specific to an app)
	@Test
	public void aspectCanBundleUserLibraries() throws Exception {
		given(userLib).hasBeenCreated()
			.and(userLib).hasNamespacedJsPackageStyle()
			.and(userLib).containsResourceFileWithContents("i18n/en/en.properties", "userLib.token=userLib token")
			.and(userLib).hasClass("userLib.Class1")
			.and(aspect).indexPageRefersTo("userLib.Class1");
		when(aspect).requestReceivedInDev("i18n/en.js", response);
		then(response).containsText("\"userLib.token\": \"userLib token\"");
			
	}
	
}
