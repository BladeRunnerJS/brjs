package com.caplin.cutlass.bundler.html;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

public class StructureHtmlBundlerFileListTestForApp2 {
	
	private BundlerFileTester test;
	
	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new HtmlBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	@Test
	public void appAspectLevelRequestForApp2DefaultAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/html/fi-1.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/html/fi-2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-1.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-2.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/html/fi-b11.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/html/fi-b12.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/html/fi-b21.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/html/fi-b22.html", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b11.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b12.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/html/fx-b21.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/html/fx-b22.html",
			APPLICATIONS_DIR + "/test-app2/default-aspect/resources/html/m1.html",
			APPLICATIONS_DIR + "/test-app2/default-aspect/resources/html/m2.html",
		});
	}
	
	@Test
	public void appAspectLevelRequestForApp2AlternateAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/html/fi-1.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/html/fi-2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-1.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-2.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/html/fi-b11.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/html/fi-b12.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/html/fi-b21.html",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/html/fi-b22.html", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b11.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b12.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/html/fx-b21.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/html/fx-b22.html",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/resources/html/x1.html",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/resources/html/x2.html"
		});
	}
	
	@Test
	public void bladeSetLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-1.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-2.html",
		});
	}
	
	@Test
	public void bladeLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-1.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b11.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b12.html",
		});
	}
	
	@Test
	public void workbenchLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench")
		.whenRequestReceived("html.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/chart/somehtml.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile1.html",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/htmlfile2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-1.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/html/fx-2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b11.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/html/fx-b12.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/html/wb1.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/html/wb2.html",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/html/wb3.html",
		});
	}	
}
