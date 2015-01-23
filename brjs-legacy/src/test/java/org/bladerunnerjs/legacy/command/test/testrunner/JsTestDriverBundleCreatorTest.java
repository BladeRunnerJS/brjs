package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.legacy.command.test.testrunner.JsTestDriverBundleCreator;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

import com.esotericsoftware.yamlbeans.YamlException;

public class JsTestDriverBundleCreatorTest {

	private BRJS brjs;
	private MemoizedFile memoizedConfigFile;
	public LogMessageStore logMessageStore = new LogMessageStore(true);
	private File aspectTestConfig;
	private File aspectTest;
	
	@Before
	public void setup() throws InvalidSdkDirectoryException, IOException, InvalidNameException, ModelUpdateException {
		brjs = BRJSTestModelFactory.createModel(BRJSTestModelFactory.createTestSdkDirectory(), new TestLoggerFactory(logMessageStore));
		TypedTestPack aspectTestPack = brjs.app("app1").aspect("default").testType("unit");
		aspectTestConfig = new File(aspectTestPack.dir(), "jsTestDriver.conf");
		aspectTest = new File(aspectTestPack.dir(), "tests/AppTest.js");
		memoizedConfigFile = brjs.getMemoizedFile(aspectTestConfig);
		
		logMessageStore.enableLogging();
	}
	
	@Test
	public void logAWarningWhenCommonJsTestsAreNotWrappedWithinAnIIFE() throws FileNotFoundException, YamlException, MalformedRequestException, ResourceNotFoundException, ContentProcessingException, IOException, ModelOperationException {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		FileUtils.writeStringToFile(aspectTest, "var foo = function(){ /* code */ }", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, memoizedConfigFile);
		
		// then
		logMessageStore.verifyWarnLogMessage("The CommonJS test 'AppTest.js' is not wrapped within an IIFE, which may cause unreliability in tests.");
	}
	
	@Test
	public void doNotLogAWarningWhenCommonJsTestsAreWrappedWithinAnIIFE() throws FileNotFoundException, YamlException, MalformedRequestException, ResourceNotFoundException, ContentProcessingException, IOException, ModelOperationException {
		// given
		FileUtils.writeStringToFile(aspectTestConfig, "basepath: .", "UTF-8");		
		FileUtils.writeStringToFile(aspectTest, "(function() {\n 'use strict'; \n require( 'jasmine' );\n}())", "UTF-8");
		
		// when
		JsTestDriverBundleCreator.createRequiredBundles(brjs, memoizedConfigFile);
		
		// then
		logMessageStore.verifyNoMoreWarnMessages();
	}
}
