package com.caplin.cutlass.bundler.html;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.bundler.MergeTestHelper;

public class MergeHtmlBundlerTest
{
	private MergeTestHelper test;
	
	@Before
	public void setUp()
	{
		test = new MergeTestHelper(new HtmlBundler());
	}
	
	@Test(expected=RequestHandlingException.class)
	public void requestsMustBeWellFormed() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new HtmlBundler();
		String aspectPath = "src/test/resources/generic-bundler/bundler-structure-tests/" + APPLICATIONS_DIR + "/app1/main-aspect";
		bundler.getBundleFiles(new File(aspectPath), null, "html.bundle.foo");
	}
	
	@Test
	public void multipleFilesCanBeConcatenated() throws Exception
	{
		test.givenInputFiles(new String[] {
			"src/test/resources/html-bundler/input/html1.html",
			"src/test/resources/html-bundler/input/html2.html"
		})
		.thenBundleIsCreated("src/test/resources/html-bundler/output/html1+html2.html");
	}
	
	@Test
	public void multipleFilesCanBeConcatenatedInAnyOrder() throws Exception
	{
		test.givenInputFiles(new String[] {
				"src/test/resources/html-bundler/input/html2.html",
				"src/test/resources/html-bundler/input/html1.html"
		})
		.thenBundleIsCreated("src/test/resources/html-bundler/output/html2+html1.html");
	}
	
	@Test
	public void singleFilesCanBeSupported() throws Exception
	{
		test.givenInputFiles(new String[] {
			"src/test/resources/html-bundler/input/html1.html"
		})
		.thenBundleIsCreated("src/test/resources/html-bundler/output/html1-only.html");
	}
	
}
