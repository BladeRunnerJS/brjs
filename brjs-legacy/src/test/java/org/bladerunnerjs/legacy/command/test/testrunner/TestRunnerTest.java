package org.bladerunnerjs.legacy.command.test.testrunner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.legacy.command.test.testrunner.TestRunResult;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunner;
import org.bladerunnerjs.legacy.conf.TestRunnerConfiguration;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;

public class TestRunnerTest {

	TestRunnerConfiguration config;
	MemoizedFile configFile;
	MemoizedFile resultDir;
	boolean verbose, debug;
	private BRJS brjs;
	
	private List<String> browserList(String browsers) {
		return Arrays.asList(browsers.split(", *"));
	}
	
	
	@Before
	public void beforeTest() throws Exception {
		// we're cheekily using another tests sdk structure so the test can work
		File sdkBaseDir = new File("src/test/resources/AnalyseApplicationCommandTest/structure-tests/sdk");
		brjs = BRJSTestModelFactory.createModel(sdkBaseDir);
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
		
		configFile = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources/test-runner.conf") );
		resultDir = brjs.getMemoizedFile( new File(".build/test-results") );
		
		config = TestRunnerConfiguration.getConfiguration(configFile, browserList("ff5"));
		config.setOperatingSystem("OS1");
		TestRunner.disableLogging = true;
	}
	
	@Test
	public void runATsWhenTestAcceptanceDirDoesNotExist() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/dir-with-no-tests") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ATs);
		
		assertFalse(testRunner.hasTestRun());
	}
	
	@Test
	public void runUTsWhenTestUnitDirExists() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());	
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		TestRunResult specificTestRun = testRunner.getTestResultList().get(0);
		
		assertTrue(testRunner.hasTestRun());
		assertEquals(TestRunner.TestType.UTs, specificTestRun.getTestType());
	}
	
	@Test
	public void runUTsWithValidBaseDir() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		
		assertEquals(1, testRunner.getTestResultList().size());
	}
	
	@Test
	public void runUTsWithSameBaseDirAsTestDir() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory, directory, TestRunner.TestType.UTs);
		
		assertEquals(1, testRunner.getTestResultList().size());
	}
	
	@Test
	public void runUTsWithInvalidBaseDir() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/DOESNOTEXIST") );
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
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ALL);
		TestRunResult specificUTsTestRun = testRunner.getTestResultList().get(0);
		
		assertEquals( new File("src/test/resources/TestCommand/ct-runner-resources/test-unit").getAbsolutePath(), specificUTsTestRun.getTestDirectory().toString());
	}
	
	@Test
	public void runUTsAndATs() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTsAndATs);
		TestRunResult specificUTsTestRun = testRunner.getTestResultList().get(0);
		TestRunResult specificATsTestRun = testRunner.getTestResultList().get(1);
		
		assertTrue(testRunner.getTestResultList().size() == 2);
		assertEquals( new File("src/test/resources/TestCommand/ct-runner-resources/test-unit").getAbsolutePath(), specificUTsTestRun.getTestDirectory().toString());
		assertEquals( new File("src/test/resources/TestCommand/ct-runner-resources/test-acceptance").getAbsolutePath(), specificATsTestRun.getTestDirectory().toString());
	}
	
	@Test
	public void runITs() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.ITs);
		TestRunResult specificITsTestRun = testRunner.getTestResultList().get(0);
		
		assertTrue(testRunner.getTestResultList().size() == 1);
		assertEquals( new File("src/test/resources/TestCommand/ct-runner-resources/test-integration").getAbsolutePath(), specificITsTestRun.getTestDirectory().toString());
	}

	@Test
	public void getDurationDoesNotThrowException() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		TestRunResult specificTestRun = testRunner.getTestResultList().get(0);
				
		specificTestRun.getDurationInSeconds();
	}
	
	@Test
	public void getDurationAfterSettingStartTime() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
		TestRunner testRunner = new TestRunner(configFile, resultDir, config.getBrowserNames());		
		testRunner.runAllTestsInDirectory(directory.getParentFile(), directory, TestRunner.TestType.UTs);
		TestRunResult specificTestRun = testRunner.getTestResultList().get(0);
		
		specificTestRun.setStartTime(System.currentTimeMillis());
		assertTrue(specificTestRun.getDurationInSeconds() >= 0);
	}
	
	@Test
	public void runALLWhenThereIsAreAllTestDirTypes() throws Exception {
		MemoizedFile directory = brjs.getMemoizedFile( new File("src/test/resources/TestCommand/ct-runner-resources") );
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
