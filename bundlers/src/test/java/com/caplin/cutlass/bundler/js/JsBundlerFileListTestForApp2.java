package com.caplin.cutlass.bundler.js;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class JsBundlerFileListTestForApp2
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		BladeRunnerSourceFileProvider.disableUsedBladesFiltering();
		test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}

	@Test
	public void appAspectLevelRequestForApp2DefaultAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app2/default-aspect/src/section/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/default-aspect/src/section/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/default-aspect/src/section/app/main1.js",
				APPLICATIONS_DIR + "/test-app2/default-aspect/src/section/app/main2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/src/section/fi/fi-blade2/app/blade2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/src/section/fx/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/src/section/fx/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/xmlDepend.js"
		});
	}

	@Test
	public void appAspectLevelRequestForApp2AlternateAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app2/xtra-aspect/src/section/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/xtra-aspect/src/section/xmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/xtra-aspect/src/section/app/x1.js",
				APPLICATIONS_DIR + "/test-app2/xtra-aspect/src/section/app/x2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/src/section/fi/fi-blade2/app/blade2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/src/section/fx/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/src/section/fx/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/xmlDepend.js"
		});
	}
	
	
	@Test
	public void bladesetLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fi-bladeset")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/xmlDepend.js"
		});		
	}
	
	@Test
	public void bladeLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/xmlDepend.js"
		});
	}
	
	@Test
	public void workbenchLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/workbench")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] { 
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/workbench/src/workbench/wb1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/workbench/src/workbench/wb2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/htmlDepend.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/app/bladeset2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade1.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/src/section/fi/fi-blade1/app/blade2.js",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/src/section/fi/xmlDepend.js"
		});
	}	
}
