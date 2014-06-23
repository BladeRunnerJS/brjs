package com.caplin.cutlass.command.test.testrunner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.caplin.cutlass.CutlassConfig;

import org.bladerunnerjs.model.TestModelAccessor;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import com.caplin.cutlass.conf.TestRunnerConfiguration;

public class TestRunnerTest extends TestModelAccessor {

	TestRunnerConfiguration config;
	File configFile = new File("src/test/resources/TestCommand/ct-runner-resources/test-runner.conf");
	File resultDir = new File(".build/test-results");
	boolean verbose, debug;
	
	private List<String> browserList(String browsers) {
		return Arrays.asList(browsers.split(", *"));
	}
	
	
	@Before
	public void beforeTest() throws Exception {
		// we're cheekily using another tests sdk structure so the test can work
		File sdkBaseDir = new File("src/test/resources/AnalyseApplicationCommandTest/structure-tests/" + CutlassConfig.SDK_DIR);
		ThreadSafeStaticBRJSAccessor.initializeModel(createModel(sdkBaseDir));
		
		config = TestRunnerConfiguration.getConfiguration(new File("src/test/resources/TestCommand/ct-runner-resources/test-runner.conf"), browserList("ff5"));
		config.setOperatingSystem("OS1");
		TestRunner.disableLogging = true;
	}
	
	@Test
	public void runATsWhenTestAcceptanceDirDoesNotExist() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/dir-with-no-tests");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ATs);
		
		assertFalse(testRunner.hasTestRun());
	}
	
	@Test
	public void runUTsWhenTestUnitDirExists() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());	
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		TestRunResult specificTestRun = testRunner.getTestResultList().get(0);
		
		assertTrue(testRunner.hasTestRun());
		assertEquals(TestRunner.TestType.UTs, specificTestRun.getTestType());
	}
	
	@Test
	public void runUTsWithValidBaseDir() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		
		assertEquals(1, testRunner.getTestResultList().size());
	}
	
	@Test
	public void runUTsWithSameBaseDirAsTestDir() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory, directory, TestRunner.TestType.UTs);
		
		assertEquals(1, testRunner.getTestResultList().size());
	}
	
	@Test
	public void runUTsWithInvalidBaseDir() throws Exception {
		File directory = new File ("src/test/resources/DOESNOTEXIST");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		
		try {
			testRunner.runAllTestsInDirectory(directory, directory, TestRunner.TestType.UTs);	
		} catch (IOException expected) {
			// expected
		}
		assertEquals(0, testRunner.getTestResultList().size());
	}	


	@Test
	public void runUTsAndCheckThatTheTestDirForATestRunIsStoredCorrectlyTest() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ALL);
		TestRunResult specificUTsTestRun = testRunner.getTestResultList().get(0);
		
		assertEquals("src/test/resources/TestCommand/ct-runner-resources/test-unit",specificUTsTestRun.getTestDirectory().toString().replace("\\","/"));
	}
	
	@Test
	public void runUTsAndATs() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTsAndATs);
		TestRunResult specificUTsTestRun = testRunner.getTestResultList().get(0);
		TestRunResult specificATsTestRun = testRunner.getTestResultList().get(1);
		
		assertTrue(testRunner.getTestResultList().size() == 2);
		assertEquals("src/test/resources/TestCommand/ct-runner-resources/test-unit",specificUTsTestRun.getTestDirectory().toString().replace("\\","/"));
		assertEquals("src/test/resources/TestCommand/ct-runner-resources/test-acceptance",specificATsTestRun.getTestDirectory().toString().replace("\\","/"));
	}
	
	@Test
	public void runITs() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ITs);
		TestRunResult specificITsTestRun = testRunner.getTestResultList().get(0);
		
		assertTrue(testRunner.getTestResultList().size() == 1);
		assertEquals("src/test/resources/TestCommand/ct-runner-resources/test-integration",specificITsTestRun.getTestDirectory().toString().replace("\\","/"));
	}

	@Test
	public void getDurationDoesNotThrowException() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		TestRunResult specificTestRun = testRunner.getTestResultList().get(0);
				
		specificTestRun.getDurationInSeconds();
	}
	
	@Test
	public void getDurationAfterSettingStartTime() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		TestRunResult specificTestRun = testRunner.getTestResultList().get(0);
		
		specificTestRun.setStartTime(System.currentTimeMillis());
		assertTrue(specificTestRun.getDurationInSeconds() >= 0);
	}
	
	@Test
	public void runALLWhenThereIsAreAllTestDirTypes() throws Exception {
		File directory = new File ("src/test/resources/TestCommand/ct-runner-resources");
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ALL);
		TestRunResult testRunA = testRunner.getTestResultList().get(0);
		TestRunResult testRunB = testRunner.getTestResultList().get(1);
		TestRunResult testRunC = testRunner.getTestResultList().get(2);
		
		assertEquals(3, testRunner.getTestResultList().size());
		assertEquals(TestRunner.TestType.UTs, testRunA.getTestType());	
		assertEquals(TestRunner.TestType.ITs, testRunB.getTestType());
		assertEquals(TestRunner.TestType.ATs, testRunC.getTestType());
	}
	
	@Test
	public void javaOptsIsPassedThroughAsWhatTheSystemHas() throws Exception {
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		
		String expected = System.getenv("JAVA_OPTS");
		if(expected == null) { 
			expected = "";
		}
		assertEquals(expected, testRunner.getJavaOpts());
	}
}
