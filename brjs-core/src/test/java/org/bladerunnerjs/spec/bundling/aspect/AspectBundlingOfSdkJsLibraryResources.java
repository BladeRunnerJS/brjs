package org.bladerunnerjs.spec.bundling.aspect;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingOfSdkJsLibraryResources extends SpecTest {
	private App app;
	private Aspect aspect;
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
			sdkLib = brjs.sdkLib();
	}

	@Ignore // This test should pass to prove that the app can bundle sdk html resources
	@Test
	public void aspectCanBundleSdkLibHtmlResources() throws Exception {
		given(sdkLib).hasBeenCreated()
			.and(sdkLib).hasNamespacedJsPackageStyle()
			.and(sdkLib).containsFileWithContents("resources/html/view.html", "<div id='tree-view'></div>")
			.and(sdkLib).hasClass("br.workbench.ui.Workbench")
			.and(aspect).containsFileWithContents("resources/workbench-view.html", "<div id='appns.aspect-view'></div>")
			.and(aspect).indexPageRefersTo("br.workbench.ui.Workbench");
		when(app).requestReceived("/default-aspect/bundle.html", response);
		then(response).containsOrderedTextFragments("<div id='appns.workbench-view'></div>",
													"<div id='tree-view'></div>");
	}

}
