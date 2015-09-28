package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.io.IOException;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.TestLoggerFactory;
import org.bladerunnerjs.legacy.command.test.testrunner.JsTestDriverBundleCreator;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

public class JsTestDriverBundleCreatorTest {

	private BRJS brjs;
	private MemoizedFile memoizedConfigFile;
	public LogMessageStore logMessageStore = new LogMessageStore(true);
	private File aspectTestConfig;
	private File aspectTest;
	private File nestedAspectTest;
	private TypedTestPack aspectTestPack;
	
	@Before
	public void setup() throws InvalidSdkDirectoryException, IOException, InvalidNameException, ModelUpdateException {
		brjs = BRJSTestModelFactory.createModel(BRJSTestModelFactory.createRootTestDir(), new TestLoggerFactory(logMessageStore));
		FileUtils.write(brjs.app("app1").file("app.conf"), "");
		aspectTestPack = brjs.app("app1").aspect("default").testType("unit");
		aspectTestConfig = new File(aspectTestPack.dir(), "jsTestDriver.conf");
		aspectTest = new File(aspectTestPack.dir(), "tests/AppTest.js");
		nestedAspectTest = new File(aspectTestPack.dir(), "tests/foo/bar/AppTest.js");
		memoizedConfigFile = brjs.getMemoizedFile(aspectTestConfig);
		
		logMessageStore.enableLogging();
	}
	
	@Test
	public void logAWarningWhenCommonJsTestsAreNotWrappedWithinAnIIFE() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		FileUtils.writeStringToFile(aspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, memoizedConfigFile);
		
		// then
		logMessageStore.verifyWarnLogMessage("The CommonJS test '%s' is not wrapped within an IIFE (or doesn't have one in the first 5 lines), which may cause unreliability in tests.", "AppTest.js");
	}
	
	@Test
	public void iifeWarningIsNotLoggedForNamespacedJsTests() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");
		FileUtils.writeStringToFile(new File(aspectTestPack.dir(), ".js-style"), "namespaced-js");
		FileUtils.writeStringToFile(aspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, memoizedConfigFile);
		
		// then
		logMessageStore.verifyNoWarnLogMessage("The CommonJS test '%s' is not wrapped within an IIFE (or doesn't have one in the first 5 lines), which may cause unreliability in tests.", "AppTest.js");
	}
	
	@Test
	public void logAWarningWhenNestedCommonJsTestsAreNotWrappedWithinAnIIFE() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		nestedAspectTest.getParentFile().mkdirs();
		FileUtils.writeStringToFile(nestedAspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, memoizedConfigFile);
		
		// then
		logMessageStore.verifyWarnLogMessage("The CommonJS test '%s' is not wrapped within an IIFE (or doesn't have one in the first 5 lines), which may cause unreliability in tests.", "foo/bar/AppTest.js");
	}
	
	@Test
	public void doNotLogAWarningWhenCommonJsTestsAreWrappedWithinAnIIFE() throws Exception {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		FileUtils.writeStringToFile(aspectTest, "(function() {\n 'use strict'; \n require( 'jasmine' );\n}())", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, memoizedConfigFile);
		
		// then
		logMessageStore.verifyNoMoreWarnMessages();
	}
}
