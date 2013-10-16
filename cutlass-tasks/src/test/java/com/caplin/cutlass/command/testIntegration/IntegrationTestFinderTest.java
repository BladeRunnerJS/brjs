package com.caplin.cutlass.command.testIntegration;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;

public class IntegrationTestFinderTest
{

	private static final String TEST_ROOT = "src/test/resources/TestIntegrationCommand";
	private IntegrationTestFinder testFinder;
	
	@Before
	public void setup()
	{
		testFinder = new IntegrationTestFinder();
	}
	
	@Test
	public void testFindingAllTestDirsFromRoot() 
	{
		List<File> expectedFiles = Arrays.asList(
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-integration/webdriver"),
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1/workbench/tests/test-integration/webdriver")
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(new File(TEST_ROOT)) );
	}
	
	@Test
	public void testFindingAllTestsForSingleWorkbench() 
	{
		List<File> expectedFiles = Arrays.asList(
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1/workbench/tests/test-integration/webdriver")
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/some-bladeset/blades/blade1")) );
	}
	
	@Test
	public void testFindingAllTestsForSingleAspect() 
	{
		List<File> expectedFiles = Arrays.asList(
				new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-integration/webdriver")
		);
		assertEquals( expectedFiles, testFinder.findTestDirs(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect")) );
	}
	
	@Test
	public void testEmptyListReturnedIfNoTestsFound() 
	{
		List<File> expectedFiles = Arrays.asList(new File[0]);
		assertEquals( expectedFiles, testFinder.findTestDirs(new File(TEST_ROOT, APPLICATIONS_DIR + "/app1/main-aspect/tests/test-unit")) );
	}
	
}
