package com.caplin.cutlass.bundler.css;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

import com.caplin.cutlass.bundler.BundlerFileTester;

public class StructureCssBundlerFileListTestForApp2
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new CssBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
		
	
///////////////// Aspect Level Tests ///////////////////
	@Test @Ignore
	public void requestAspectLevelThemeCommonForDefaultAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
		.whenRequestReceived("css/common_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/themes/common/common.css",	
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/common/common.css",	
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/common/common2.css",
		});
	}
	
	@Test
	public void requestAspectLevelThemeNoirLocaleForDefaultAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/themes/noir/noir_de_DE.css",	
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir_de_DE.css",	
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/noir/noir2_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/noir/noir_de_DE.css"
		});
	}
	
	@Test @Ignore
	public void requestAspectLevelThemeCommonRequestForAlternateAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
		.whenRequestReceived("css/common_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/themes/common/common.css",	
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/common/common.css",	
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/themes/common/common2.css"
		});
	}
	
	@Test
	public void requestAspectLevelThemeNoirLocaleForAlternateAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/themes/noir/noir_de_DE.css",	
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir_de_DE.css",	
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/themes/noir/noir2_de_DE.css",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/themes/noir/noir_de_DE.css"
		});
	}
	
	///////////////// Bladeset Level Tests ///////////////////
	
	@Test
	public void requestBladesetLevelThemeNoir() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir.css" 
		});
	}
		
	////////////////////// Blade Level Tests /////////////////////////////////
	
	@Test
	public void requestBladeLevelThemeGold() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2")
		.whenRequestReceived("css/gold_css.bundle")
		.thenBundledFilesEquals(new String[] {
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/gold/gold.css"
		});
	}
		
	@Test
	public void requestBladeLevelThemeGoldLocaleWhenBladesetDoesNotHaveGoldTheme() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2")
		.whenRequestReceived("css/gold_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/gold/gold_de_DE.css", 
		});
	}
	
	@Test
	public void requestBladeLevelThemeNoirAndPullsInBladesetLevelThemeNoir() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/themes/noir/noir.css", 
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/themes/noir/noir.css", 
		});
	}

///////////////////// Blade Level Nested Folders Tests ///////////////////////
	
	@Test
	public void requestBladeLevelThemeGreenNestedFolders() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2")
		.whenRequestReceived("css/green_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/green/dark-green/pine-green1.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/green/light-green/apple-green1.css", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/themes/green/light-green/lime-green/lime-green1.css"
		});
	}
		
////////////////////// Workbench Level Tests /////////////////////////////////
	
	@Test
	public void requestWorkbenchLevelThemeNoir() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app2/default-aspect/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/style/wb1.css",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/style/wb2.css"
		});
	}

}