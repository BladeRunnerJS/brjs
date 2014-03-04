package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;

public class JsSourceFileFinderGetSeedFilesTest
{
	private static final String testBase = "src/test/resources/js-bundler/source-file-finder";
	
	@Before
	public void setup() 
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
		BladeRunnerSourceFileProvider.disableUsedBladesFiltering();
	}
	
	/* seed file tests */

	@Test
	public void testGettingAppAspectSeedFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/index.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/resources/html/file.html").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/resources/xml/file.xml").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/resources/html/file.html").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/resources/xml/file.xml").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingAppAspectSeedFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/tests/test-acceptance/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/tests/test-acceptance/js-test-driver/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/tests/test-acceptance/js-test-driver/resources/xml/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/file.html").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/index.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/resources/html/file.html").getAbsoluteFile(),  
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/resources/xml/file.xml").getAbsoluteFile(),  
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/resources/html/file.html").getAbsoluteFile(),  
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/resources/xml/file.xml").getAbsoluteFile(),  
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingBladesetAspectSeedFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingBladesetAspectSeedFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/tests/test-acceptance/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/tests/test-acceptance/js-test-driver/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/tests/test-acceptance/js-test-driver/resources/xml/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/file.html").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingBladeAspectSeedFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingBladeAspectSeedFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/resources/xml/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/file.html").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingWorkbenchAspectSeedFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList( 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/index.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingWorkbenchAspectSeedFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/tests/test-acceptance/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/tests/test-acceptance/js-test-driver/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/tests/test-acceptance/js-test-driver/resources/xml/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/file.html").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.xml").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.js").getAbsoluteFile(),
				new File(testDir, "src-test/dir1/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/index.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/file.xml").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/file.html").getAbsoluteFile(),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/file.xml").getAbsoluteFile()
		);
		
		JsSourceFileFinderTestHelper.assertFilesEquals( expectedFiles, SourceFileLocator.getAllSeedFiles(baseDir, testDir) );
	}
}