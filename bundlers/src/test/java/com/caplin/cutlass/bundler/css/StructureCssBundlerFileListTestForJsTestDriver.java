package com.caplin.cutlass.bundler.css;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class StructureCssBundlerFileListTestForJsTestDriver
{
	private static final String JSTD_AT_PATH = "tests/test-acceptance/js-test-driver";
	
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new CssBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	///////////////// Aspect Level Tests ///////////////////
	
	@Test
	public void requestAspectLevelCssForJsTestDriverTest() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect", JSTD_AT_PATH)
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/themes/noir/noir_de_DE.css",	
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir_de_DE.css",	
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/noir/noir2_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/noir/noir_de_DE.css",
		});
	}
	
	///////////////// Bladeset Level Tests ///////////////////
	
	@Test
	public void requestBladesetLevelThemeNoirForJsTestDriverTest() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset", JSTD_AT_PATH)
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir.css" 
		});
	}
		
	////////////////////// Blade Level Tests /////////////////////////////////
	
	@Test //PCTCUT-379
	public void requestBladeLevelNoirThemeLocaleForJsTestDriverTest() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1", JSTD_AT_PATH)
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir_de_DE.css", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir_de_DE.css",
		});
	}
	
	
	@Test //PCTCUT-379
	public void requestBladeLevelNoirThemeForJsTestDriverTest() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1", JSTD_AT_PATH)
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir.css", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir.css",
		});
	}
	
	///////////////////// Blade Level Nested Folders Tests ///////////////////////
	
	@Test
	public void requestBladeLevelThemeGreenNestedFoldersForJsTestDriverTest() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2", JSTD_AT_PATH)
		.whenRequestReceived("css/green_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/green/dark-green/pine-green1.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/green/light-green/apple-green1.css", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/green/light-green/lime-green/lime-green1.css"
		});
	}
	
		
	////////////////////// Workbench Level Tests /////////////////////////////////
	
	@Test
	public void requestWorkbenchLevelThemeNoirForJsTestDriverTestPullsInBladesetBladeAndWorkbenchCss() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench", JSTD_AT_PATH)
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/style/wb1.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/style/wb2.css",
		});
	}	
	
	@Test
	public void requestWorkbenchLevelLocaleForJsTestDriverTestPullsInBladesetBladeLibraryAndWorkbenchCss() throws Exception
	{
		test.givenTestDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench", JSTD_AT_PATH)
		.whenRequestReceived("css/common_en_GB_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_en_GB.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/common/common_en_GB.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/common/common_en_GB.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/common/common2_en_GB.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/common/common_en_GB.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/style/wb5_en_GB.css",
		});
	}
}