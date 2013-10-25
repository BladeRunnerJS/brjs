package com.caplin.cutlass.bundler.js;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

public class AppLibrariesTest
{
	private BundlerFileTester tester;
	
	@Before
	public void setUp()
	{
		tester = new BundlerFileTester(new JsBundler(), "src/test/resources/js-bundler/app-libs");
	}
	
	@Test
	public void aliasWithinHtmlTemplate() throws Exception
	{
		tester.givenDirectoryOnDisk("apps/alias-within-html-template-app/default-aspect")
		.whenRequestReceived("js/js.bundle")
		.thenBundledFilesEquals(new String[] {
			"sdk/libs/javascript/thirdparty/caplin-bootstrap/bootstrap.js",
			"apps/alias-within-html-template-app/libs/lib1/src/novox/pkg/Class.js",
			"apps/alias-within-html-template-app/libs/lib1/src/novox/pkg/AliasClass.js"
		});
	}
}
