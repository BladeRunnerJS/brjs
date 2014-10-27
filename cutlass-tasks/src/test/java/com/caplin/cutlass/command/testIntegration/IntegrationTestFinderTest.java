package com.caplin.cutlass.command.testIntegration;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.TestModelAccessor;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;

public class IntegrationTestFinderTest extends TestModelAccessor
{

	private static final String TEST_ROOT = "src/test/resources/TestIntegrationCommand";
	private IntegrationTestFinder testFinder;
	private BRJS brjs;
	
	@Before
	public void setup() throws Exception
	{
		brjs = createModel(new File(TEST_ROOT));
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
		testFinder = new IntegrationTestFinder();
	}
	
	@Test
	public void testFindingAllTestDirsFromRoot() 
	{
		List<File> expectedFiles = Arrays.asList(
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-integration/webdriver").getAbsoluteFile(),
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1/workbench/tests/test-integration/webdriver").getAbsoluteFile()
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, new File(TEST_ROOT)) );
	}
	
	@Test
	public void testFindingAllTestsForSingleWorkbench() 
	{
		List<File> expectedFiles = Arrays.asList(
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1/workbench/tests/test-integration/webdriver").getAbsoluteFile()
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1")) );
	}
	
	@Test
	public void testFindingAllTestsForSingleAspect() 
	{
		List<File> expectedFiles = Arrays.asList(
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-integration/webdriver").getAbsoluteFile()
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect")) );
	}
	
	@Test
	public void testEmptyListReturnedIfNoTestsFound() 
	{
		List<File> expectedFiles = Arrays.asList(new File[0]);
		assertEquals( expectedFiles, testFinder.findTestDirs(brjs, new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-unit")) );
	}
	
}
