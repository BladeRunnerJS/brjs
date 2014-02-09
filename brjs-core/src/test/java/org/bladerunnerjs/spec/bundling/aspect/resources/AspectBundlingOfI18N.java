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
	private JsLib sdkLib;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
		
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
			sdkLib = brjs.sdkLib();
	}
	
	// Aspect
	@Test
	public void aspectI18NFilesAreBundledWhenAspectSrcAreReferenced() throws Exception {
		given(aspect).hasClass("appns.Class1")
			.and(aspect).containsFileWithContents("resources/i18n/en/en.properties", "appns.token=aspect token")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(app).requestReceived("/default-aspect/i18n/en.js", response);
		then(response).containsText("\"appns.token\":\"aspect token\"");
	}

//	// Bladeset
	@Test
	public void bladesetI18NFilesAreBundledWhenBladesetSrcAreReferenced() throws Exception {
		given(bladeset).hasClasses("appns.bs.Class1")
			.and(bladeset).containsFileWithContents("resources/i18n/en/en.properties", "appns.bs.token=bladeset token")
			.and(aspect).indexPageRefersTo("appns.bs.Class1");
		when(app).requestReceived("/default-aspect/i18n/en.js", response);
		then(response).containsText("\"appns.bs.token\":\"bladeset token\"");
	}
	
	// Blade
	@Test
	public void bladeI18NFilesAreBundledWhenBladeSrcIsReferenced() throws Exception {
		given(blade).hasClasses("appns.bs.b1.Class1")
			.and(blade).containsFileWithContents("resources/i18n/en/en.properties", "appns.bs.b1.token=blade token")
			.and(aspect).indexPageRefersTo("appns.bs.b1.Class1");
		when(app).requestReceived("/default-aspect/i18n/en.js", response);
		then(response).containsText("\"appns.bs.b1.token\":\"blade token\"");
	}
	
	// SDK BRJS Lib
	@Test
	public void aspectCanBundleSdkLibHtmlResources() throws Exception {
		given(sdkLib).hasBeenCreated()
			.and(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).containsFileWithContents("resources/i18n/en/en.properties", "br.sdktoken=library token")
			.and(sdkLib).hasClass("br.workbench.ui.Workbench")
			.and(aspect).indexPageRefersTo("br.workbench.ui.Workbench");
		when(app).requestReceived("/default-aspect/i18n/en.js", response);
		then(response).containsText("\"br.sdktoken\":\"library token\"");
	}
}
