package com.caplin.cutlass.bundler.js;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class JsBundlerFileListTestForFilePathsWithBrackets
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}

	@Test
//	@Ignore
	public void appAspectLevelRequestForAppWithBracketsInPath() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-folder-with-brackets()/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
				APPLICATIONS_DIR + "/app-folder-with-brackets()/default-aspect/src/rootpatch.js",
				"js-patches/rootpatch.js",
				APPLICATIONS_DIR + "/app-folder-with-brackets()/default-aspect/src/nsx/this/class/is/patched.js",
				"js-patches/nsx/this/class/is/patched.js",
				APPLICATIONS_DIR + "/app-folder-with-brackets()/a-bladeset/blades/blade1/src/nsx/a/blade1/class.js",
				APPLICATIONS_DIR + "/app-folder-with-brackets()/a-bladeset/src/nsx/a/patched/class.js",
				"js-patches/nsx/a/patched/class.js",
		});
	}
}
