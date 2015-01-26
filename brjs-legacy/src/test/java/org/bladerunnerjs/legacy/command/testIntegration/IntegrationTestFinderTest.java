package org.bladerunnerjs.legacy.command.testIntegration;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.legacy.command.testIntegration.IntegrationTestFinder;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;


public class IntegrationTestFinderTest
{

	private static final String APPLICATIONS_DIR = "apps";
	private static final String TEST_ROOT = "src/test/resources/TestIntegrationCommand";
	private IntegrationTestFinder testFinder;
	private BRJS brjs;
	
	@Before
	public void setup() throws Exception
	{
		brjs = BRJSTestModelFactory.createModel(new File(TEST_ROOT));
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
		testFinder = new IntegrationTestFinder();
	}
	
	@Test
	public void testFindingAllTestDirsFromRoot() 
	{
		List<MemoizedFile> expectedFiles = Arrays.asList(
			brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-integration/webdriver").getAbsoluteFile()),
			brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1/workbench/tests/test-integration/webdriver").getAbsoluteFile())
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, brjs.getMemoizedFile(new File(TEST_ROOT))) );
	}
	
	@Test
	public void testFindingAllTestsForSingleWorkbench() 
	{
		List<MemoizedFile> expectedFiles = Arrays.asList(
			brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1/workbench/tests/test-integration/webdriver").getAbsoluteFile())
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1"))) );
	}
	
	@Test
	public void testFindingAllTestsForSingleAspect() 
	{
		List<MemoizedFile> expectedFiles = Arrays.asList(
			brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-integration/webdriver").getAbsoluteFile())
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect"))) );
	}
	
	@Test
	public void testEmptyListReturnedIfNoTestsFound() 
	{
		List<MemoizedFile> expectedFiles = Collections.emptyList();
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, brjs.getMemoizedFile(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-unit"))) );
	}
	
}
