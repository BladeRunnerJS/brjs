package org.bladerunnerjs.jstestdriver;

import org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor;
import org.bladerunnerjs.jstestdriver.utility.BRJSBundleInjectorSpecTest;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.TestPack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BRJSBundleInjectorTest extends BRJSBundleInjectorSpecTest
{

	private App app;
	private Aspect aspect;
	private JsLib sdkLib;

	private StringBuffer response = new StringBuffer();
	private TestPack sdkLibTestPack;
	private TestPack aspectTestPack;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			aspectTestPack = aspect.testType("unit").testTech("techy");
			
			BRJSThreadSafeModelAccessor.initializeModel(brjs);
	}
	
	@After
	public void tearDownThreadSafeModelAccessor()
	{
		BRJSThreadSafeModelAccessor.destroy();
	}
	
	@Test
	public void legacyJsBundleUrlsCanBeUsedAsABundlePath() throws Exception
	{
		given(aspect).containsFileWithContents("src/appns/srcFile.js", "// some SDK src code")
			.and(aspectTestPack).containsFileWithContents("tests/test1.js", "require('appns/srcFile');");
		whenJstdTests(aspectTestPack).runWithPaths( "bundles/js.bundle" );
		thenJstdTests(aspectTestPack).testBundleContainsText(
					"bundles/js.bundle",
					"// some SDK src code" );
	}
	
	@Test
	public void legacyXmlBundleUrlsCanBeUsedAsABundlePath() throws Exception
	{
		given(aspect).containsFileWithContents("src/appns/srcFile.js", "// some SDK src code")
			.and(aspect).containsFileWithContents("resources/file.xml", "<some xml>")
    		.and(aspectTestPack).containsFileWithContents("tests/test1.js", "require('appns/srcFile');");
    	whenJstdTests(aspectTestPack).runWithPaths( "bundles/xml.bundle" );
    	thenJstdTests(aspectTestPack).testBundleContainsText(
    				"bundles/xml.bundle",
    				"<some xml>" );
	}
	
	@Test
	public void legacyHtmlBundleUrlsCanBeUsedAsABundlePath() throws Exception
	{
		given(aspect).containsFileWithContents("src/appns/srcFile.js", "// some SDK src code")
			.and(aspect).containsFileWithContents("resources/file.html", "<div id='appns.view'>TESTCONTENT</div>")
    		.and(aspectTestPack).containsFileWithContents("tests/test1.js", "require('appns/srcFile');");
    	whenJstdTests(aspectTestPack).runWithPaths( "bundles/html.bundle" );
    	thenJstdTests(aspectTestPack).testBundleContainsText(
    				"bundles/html.bundle",
    				"<div id='appns.view'>TESTCONTENT</div>" );
	}
	
	@Test
	public void legacyCssBundleUrlsCanBeUsedAsABundlePath() throws Exception
	{
		given(aspect).containsFileWithContents("src/appns/srcFile.js", "// some SDK src code")
			.and(aspect).containsFileWithContents("resources/file.css", "some.css.styles { }")
    		.and(aspectTestPack).containsFileWithContents("tests/test1.js", "require('appns/srcFile');");
    	whenJstdTests(aspectTestPack).runWithPaths( "bundles/css.bundle" );
    	thenJstdTests(aspectTestPack).testBundleContainsText(
    				"bundles/css.bundle",
    				"some.css.styles { }" );
	}
	
	@Test
	public void legacyI18nBundleUrlsCanBeUsedAsABundlePath() throws Exception
	{
		given(aspect).containsFileWithContents("src/appns/srcFile.js", "// some SDK src code")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "appns.prop = some prop")
    		.and(aspectTestPack).containsFileWithContents("tests/test1.js", "require('appns/srcFile');");
    	whenJstdTests(aspectTestPack).runWithPaths( "bundles/i18n.bundle" );
    	thenJstdTests(aspectTestPack).testBundleContainsText(
    				"bundles/i18n.bundle",
    				"\"appns.prop\":\"some prop\"" );
	}
	
}
