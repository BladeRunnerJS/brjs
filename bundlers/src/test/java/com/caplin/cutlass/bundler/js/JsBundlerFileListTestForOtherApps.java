package com.caplin.cutlass.bundler.js;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

public class JsBundlerFileListTestForOtherApps
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}

	@Test
	public void appAspectLevelRequestForAppUsingPatches() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-using-patches/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-using-patches/default-aspect/src/rootpatch.js",
				"js-patches/rootpatch.js",
				APPLICATIONS_DIR + "/app-using-patches/default-aspect/src/nsx/this/class/is/patched.js",
				"js-patches/nsx/this/class/is/patched.js",
				APPLICATIONS_DIR + "/app-using-patches/a-bladeset/blades/blade1/src/nsx/a/blade1/class.js",
				APPLICATIONS_DIR + "/app-using-patches/a-bladeset/src/nsx/a/patched/class.js",
				"js-patches/nsx/a/patched/class.js",
		});
	}

	@Test
	public void workbenchLevelRequestForAppUsingPatches() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-using-patches/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-using-patches/a-bladeset/blades/blade1/src/nsx/a/blade1/class.js",
				APPLICATIONS_DIR + "/app-using-patches/a-bladeset/src/nsx/a/patched/class.js",
				"js-patches/nsx/a/patched/class.js",
		});
	}

	@Test
	public void appAspectLevelRequestForAppUsingSdkLibraryPatch() throws Exception
	{
		test = new BundlerFileTester(new JsBundler(), "src/test/resources/js-bundler/patch-sdk-structure");
		
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-using-patches/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-using-patches/default-aspect/src/novox/App.js",
				SDK_DIR + "/libs/javascript/caplin/src/caplin/package1/Pack1Class.js",
				"js-patches/caplin/package1/Pack1Class.js",
				SDK_DIR + "/libs/javascript/caplin/src/caplin/package2/Pack2Class.js",
		});
	}
	
}
