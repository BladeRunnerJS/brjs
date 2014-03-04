package com.caplin.cutlass.bundler.html;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;
import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class StructureHtmlBundlerFileListTestForApp1
{
	private BundlerFileTester test;
	
	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new HtmlBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	@Test @Ignore
	public void appAspectLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/b1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/b2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/c1.htm",
			APPLICATIONS_DIR + "/test-app1/main-aspect/resources/html/m1.html",
			APPLICATIONS_DIR + "/test-app1/main-aspect/resources/html/m2.html"
		});
	}
	
	@Test @Ignore
	public void bladeSetLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs2.html",
		});
	}
	
	@Test @Ignore
	public void bladeLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/b1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/b2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/c1.htm",
		});
	}
	
	@Test @Ignore
	public void workbenchLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/html/bs2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/b1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/b2.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/html/c1.htm",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/html/wb1.html",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/html/wb2.html",
		});
	}	

}
