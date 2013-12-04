package com.caplin.cutlass.bundler.js;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class JsBundlerFileListTestForApp1
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}

	@Test
	public void appAspectLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib1/lib1_1.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib1/lib1_2.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib2/lib2_1.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib2/lib2_2.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib2/subdir/lib2_subdir.js",
				SDK_DIR + "/libs/javascript/thirdparty/knockout/knockout.js",
				APPLICATIONS_DIR + "/test-app1/main-aspect/src/section/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/main-aspect/src/section/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/main-aspect/src/section/app/main1.js", 
				APPLICATIONS_DIR + "/test-app1/main-aspect/src/section/app/main2.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/xmlDepend.js"
		});
	}
	
	@Test
	public void defaultAppAspectLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				SDK_DIR + "/libs/javascript/thirdparty/jquery/jQuery.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib1/lib1_1.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib1/lib1_2.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib2/lib2_1.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib2/lib2_2.js",
				APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib2/subdir/lib2_subdir.js",
				SDK_DIR + "/libs/javascript/thirdparty/knockout/knockout.js",
				APPLICATIONS_DIR + "/test-app1/default-aspect/src/section/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/default-aspect/src/section/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/default-aspect/src/section/app/default1.js", 
				APPLICATIONS_DIR + "/test-app1/default-aspect/src/section/app/default2.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js"
		});
	}
	
	@Test
	public void bladesetLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js", 
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js"
		});
	}
	
	@Test
	public void bladeLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js"
		});
	}
	
	@Test
	public void workbenchLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/src/workbench/wb1.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/src/workbench/wb2.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/src/section/a/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade1.js",
				APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/src/section/a/blade1/app/blade2.js"
		});
	}
	
}
