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
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class JsSourceFileFinderGetSourceFilesTest
{
	private static final String testBase = "src/test/resources/js-bundler/source-file-finder";
	
	@Before
	public void setup() 
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
		
		BladeRunnerSourceFileProvider.disableUsedBladesFiltering();
	}
	
	/* source file tests */

	@Test
	public void testGettingAppAspectSourceFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/src/dir1/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/src/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/src/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/src/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/src/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/src/dir1/file.js")
		);
		
		List<ClassnameFileMapping> actualFiles = SourceFileLocator.getAllSourceFiles(baseDir, testDir);
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, actualFiles );
	}

	@Test
	public void testGettingAppAspectSourceFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/tests/test-unit/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/src/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/src/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/src/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/src/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/tests/test-unit/js-test-driver/src-test/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/tests/test-unit/js-test-driver/src-test/file.js") 
		);
		
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, SourceFileLocator.getAllSourceFiles(baseDir, testDir) );
	}

	@Test
	public void testGettingBladesetAspectSourceFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js")
		);
		
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, SourceFileLocator.getAllSourceFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingBladesetAspectSourceFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/tests/test-unit/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/tests/test-unit/js-test-driver/src-test/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/tests/test-unit/js-test-driver/src-test/file.js")
		);
		
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, SourceFileLocator.getAllSourceFiles(baseDir, testDir) );
	}

	@Test
	public void testGettingBladeAspectSourceFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js") 
		);
		
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, SourceFileLocator.getAllSourceFiles(baseDir, testDir) );
	}
	
	@Test
	public void testGettingBladeAspectSourceFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/src-test/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/src-test/file.js")
		);
		
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, SourceFileLocator.getAllSourceFiles(baseDir, testDir) );
	}

	@Test
	public void testGettingWorkbenchAspectSourceFiles() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench");
		File testDir = null;
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/src/file.js")
		);
		
		List<ClassnameFileMapping> sourceFiles = SourceFileLocator.getAllSourceFiles(baseDir, testDir);
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, sourceFiles );
	}
	
	@Test
	public void testGettingWorkbenchAspectSourceFilesWithTestDir() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench");
		File testDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/tests/test-unit/js-test-driver");
		
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js"), 
				new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/src/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/src/file.js"),
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/tests/test-unit/js-test-driver/src-test/dir1/file.js"), 
				new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/tests/test-unit/js-test-driver/src-test/file.js")
		);
		
		JsSourceFileFinderTestHelper.assertFilesSameAsClassMapping( expectedFiles, SourceFileLocator.getAllSourceFiles(baseDir, testDir) );
	}
	
	@Test
	public void testClassnameFileMappingsAreCorrect() throws Exception 
	{
		File baseDir = new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect");
		File testDir = null;
		
		List<ClassnameFileMapping> expectedFiles = Arrays.asList(
				new ClassnameFileMapping("caplin.pkg1.class", new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/class.js")), 
				new ClassnameFileMapping("caplin.pkg1.pkg2.class", new File(testBase, SDK_DIR + "/libs/javascript/caplin/src/caplin/pkg1/pkg2/class.js")), 
				new ClassnameFileMapping("dir1.a.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/src/dir1/a/file.js")),
				new ClassnameFileMapping("dir1.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/src/dir1/file.js")),
				new ClassnameFileMapping("file", new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/src/file.js")),
				new ClassnameFileMapping("dir1.a.blade1.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/dir1/a/blade1/file.js")), 
				new ClassnameFileMapping("dir1.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/src/dir1/file.js")), 
				new ClassnameFileMapping("file", new File(testBase, APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade2/src/file.js")),
				new ClassnameFileMapping("dir1.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/src/dir1/file.js")), 
				new ClassnameFileMapping("file", new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade1/src/file.js")),
				new ClassnameFileMapping("dir1.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/src/dir1/file.js")), 
				new ClassnameFileMapping("file", new File(testBase, APPLICATIONS_DIR + "/test-app1/another-bladeset/blades/blade2/src/file.js")),
				new ClassnameFileMapping("dir1.file", new File(testBase, APPLICATIONS_DIR + "/test-app1/a-aspect/src/dir1/file.js"))
		);
		
		List<ClassnameFileMapping> actualFiles = SourceFileLocator.getAllSourceFiles(baseDir, testDir);
		JsSourceFileFinderTestHelper.assertClassnameFileMappingsEquals( expectedFiles, actualFiles );
	}
}