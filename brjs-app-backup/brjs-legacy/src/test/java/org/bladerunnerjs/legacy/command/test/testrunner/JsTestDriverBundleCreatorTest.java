package org.bladerunnerjs.legacy.command.test.testrunner;

import static org.junit.Assert.*;

import java.io.File;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.legacy.command.test.testrunner.JsTestDriverBundleCreator;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

public class JsTestDriverBundleCreatorTest extends SpecTest {

	private Aspect aspect;
	private MemoizedFile aspectTestConfig;
	private File aspectClass;
	private File aspectTest;
	private File nestedAspectTest;
	private TypedTestPack aspectTestPack;
	
	@Before
	public void setup() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated();
		
		FileUtils.write(brjs.app("app1").file("app.conf"), "");
		aspect = brjs.app("app1").aspect("default");
		aspectTestPack = aspect.testType("unit");
		aspectTestConfig = aspectTestPack.file("jsTestDriver.conf");
		aspectClass = new File(aspect.dir(), "src/AspectClass.js");
		aspectTest = new File(aspectTestPack.dir(), "tests/AppTest.js");
		nestedAspectTest = new File(aspectTestPack.dir(), "tests/foo/bar/AppTest.js");
		
		logging.enableLogging();
		logging.enableStoringLogs();
		logging.enableStoringConsoleLogs();
	}
	
	@Test
	public void logAWarningWhenCommonJsTestsAreNotWrappedWithinAnIIFE() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		FileUtils.writeStringToFile(aspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, aspectTestConfig);
		
		// then
		logging.verifyWarnLogMessage("The CommonJS test '%s' is not wrapped within an IIFE (or doesn't have one in the first 5 lines), which may cause unreliability in tests.", "AppTest.js");
	}
	
	@Test
	public void iifeWarningIsNotLoggedForNamespacedJsTests() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");
		FileUtils.writeStringToFile(new File(aspectTestPack.dir(), ".js-style"), "namespaced-js");
		FileUtils.writeStringToFile(aspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, aspectTestConfig);
		
		// then
		logging.verifyNoWarnLogMessage("The CommonJS test '%s' is not wrapped within an IIFE (or doesn't have one in the first 5 lines), which may cause unreliability in tests.", "AppTest.js");
	}
	
	@Test
	public void logAWarningWhenNestedCommonJsTestsAreNotWrappedWithinAnIIFE() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		nestedAspectTest.getParentFile().mkdirs();
		FileUtils.writeStringToFile(nestedAspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, aspectTestConfig);
		
		// then
		logging.verifyWarnLogMessage("The CommonJS test '%s' is not wrapped within an IIFE (or doesn't have one in the first 5 lines), which may cause unreliability in tests.", "foo/bar/AppTest.js");
	}
	
	@Test
	public void doNotLogAWarningWhenCommonJsTestsAreWrappedWithinAnIIFE() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		FileUtils.writeStringToFile(aspectTest, "(function() {\n 'use strict'; \n require( 'jasmine' );\n}())", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, aspectTestConfig);
		
		// then
		logging.verifyNoMoreWarnMessages();
	}
	
	@Test
	public void minifierLevelCanBeSet() throws Exception {
		logging.disableLogging();
		logging.disableStoringLogs();
		
		// given
		FileUtils.writeStringToFile(aspectTestConfig, 
			"basepath: .\n"+"load:\n"+" - bundles/js.bundle",
			"UTF-8");
		FileUtils.writeStringToFile(aspectTest, "require('appns/AspectClass');");
		FileUtils.writeStringToFile(aspectClass, "var foo={}; foo.publicProperty=1; foo._privateProperty1=2; foo['_privateProperty2']=3; alert(foo);");
		 
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, aspectTestConfig, "closure-medium");
		
		// then
		assertTrue( FileUtils.readFileToString(aspectTestPack.file("bundles/js.bundle").getUnderlyingFile()).contains("alert({publicProperty:1,a:2,_privateProperty2:3})})") );
	}
	
}
