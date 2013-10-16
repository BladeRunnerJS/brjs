package com.caplin.cutlass.bundler.js;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

public class JsBundlerFileListTestWithDependentClasses
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new JsBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	@Test
	public void unnamespacedClassesCanReferToEachOther() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
			APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade1/src/RootClass.js",
			APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade1/src/ChildClass.js",
			APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade1/src/GrandChildClass.js"
		});
	}
	
	@Test
	public void classesCanExtendClassesThatStartWithTheSameName() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade2/workbench")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
			APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade2/src/caplin/component/ComponentInterface.js",
			APPLICATIONS_DIR + "/app-with-dependent-classes/a-bladeset/blades/blade2/src/caplin/component/Component.js"
		});
	}
}
