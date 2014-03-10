package org.bladerunnerjs.spec.plugin.bundler.unbundledresources;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.TypedTestPack;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class UnbundledResourcesContentPluginTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	private JsLib thirdpartyLib;
	private JsLib sdkJsLib;
	private BladerunnerConf bladerunnerConf;
	private Bladeset bladeset;
	private Blade blade;
	private TypedTestPack bladeTestPack, sdkJsLibTestPack;
	private TestPack bladeTests, sdkJsLibTests;
	
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
			bladeTestPack = blade.testType("test");
			bladeTests = bladeTestPack.testTech("techy");
			thirdpartyLib = app.jsLib("lib1");
			sdkJsLib = brjs.sdkLib("sdkLib");
			bladerunnerConf = brjs.bladerunnerConf();
			sdkJsLibTestPack = sdkJsLib.testType("test");
			sdkJsLibTests = sdkJsLibTestPack.testTech("jsTestDriver");
	}
	
	@Test
	public void requestsCanBeMadeForAFileInUnbundledResources() throws Exception
	{
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("unbundled-resources/someFile.txt", "some file contents");
		when(app).requestReceived("/default-aspect/unbundled-resources/someFile.txt", response);
		then(response).textEquals("some file contents");
	}
	
}
