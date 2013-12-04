package com.caplin.cutlass.bundler.css;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class StructureCssBundlerFileListTestForApp1
{
	private BundlerFileTester test;

	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new CssBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	@Test
	public void appAspectLevelThemeNoirRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir.css", 
			APPLICATIONS_DIR + "/test-app1/main-aspect/themes/noir/noir.css"
		});
	}
	
	@Test
	public void appAspectLevelThemeCommonRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("css/common_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style.css",
			APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib3/lib3.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common.css", 
			APPLICATIONS_DIR + "/test-app1/main-aspect/themes/common/common.css"
		});
	}
	
	@Test
	public void appAspectLevelThemeNoirLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir_de_DE.css", 
			APPLICATIONS_DIR + "/test-app1/main-aspect/themes/noir/noir_de_DE.css"
		});
	}
	
	@Test
	public void appAspectLevelThemeCommonLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("css/common_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common_de_DE.css", 
			APPLICATIONS_DIR + "/test-app1/main-aspect/themes/common/common_de_DE.css"
		});
	}
	
	@Test
	public void appAspectLevelThemeNoirBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("css/noir_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir_ie6.css", 
			APPLICATIONS_DIR + "/test-app1/main-aspect/themes/noir/noir_ie6.css"
		});
	}
	
	@Test
	public void appAspectLevelThemeCommonBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("css/common_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common_ie6.css",
			APPLICATIONS_DIR + "/test-app1/main-aspect/themes/common/common_ie6.css"
		});
	}
	
	///////////////// Bladeset Level Tests ///////////////////
	
	@Test
	public void bladesetLevelThemeNoirRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir.css" 
		});
	}
	
	@Test
	public void bladesetLevelThemeCommonRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("css/common_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common.css",
		});
	}
	
	@Test
	public void bladesetLevelThemeNoirLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_de_DE.css" 
		});
	}
	
	@Test
	public void bladesetLevelThemeCommonLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("css/common_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_de_DE.css"
		});
	}
	
	@Test
	public void bladesetLevelThemeNoirBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("css/noir_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_ie6.css"
		});
	}
	
	@Test
	public void bladesetLevelThemeCommonBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("css/common_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_ie6.css", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_ie6.css"
		});
	}
	
	////////////////////// Blade Level Tests /////////////////////////////////
	
	@Test
	public void bladeLevelThemeNoirRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir.css" 
		});
	}
	
	@Test
	public void bladeLevelThemeCommonRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("css/common_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style.css", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common.css" 
		});
	}
	
	@Test
	public void bladeLevelThemeNoirLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir_de_DE.css" 
		});
	}
	
	@Test
	public void bladeLevelThemeCommonLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("css/common_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_de_DE.css", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common_de_DE.css" 
		});
	}
	
	@Test
	public void bladeLevelThemeNoirBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("css/noir_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir_ie6.css" 
		});
	}
	
	@Test
	public void bladeLevelThemeCommonBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("css/common_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_ie6.css", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common_ie6.css" 
		});
	}
	
////////////////////// Workbench Level Tests /////////////////////////////////
	
	@Test
	public void workbenchLevelThemeNoirRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("css/noir_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app1/default-aspect/themes/noir/noir.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/style/blade.css",
		});
	}
	
	@Test
	public void workbenchLevelThemeCommonRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("css/common_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style.css",
			APPLICATIONS_DIR + "/test-app1/thirdparty-libraries/lib3/lib3.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app1/default-aspect/themes/common/common.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/style/blade.css"
		});
	}
	
	@Test
	public void workbenchLevelThemeNoirLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("css/noir_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/default-aspect/themes/noir/noir_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/style/blade_de_DE.css",
		});
	}
	
	@Test
	public void workbenchLevelThemeCommonLocaleRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("css/common_de_DE_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/default-aspect/themes/common/common_de_DE.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/style/blade_de_DE.css"
		});
	}
	
	@Test
	public void workbenchLevelThemeNoirBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("css/noir_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/noir/noir_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/noir/noir_ie6.css",
			APPLICATIONS_DIR + "/test-app1/default-aspect/themes/noir/noir_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/style/blade_ie6.css"
		});
	}
	
	@Test
	public void workbenchLevelThemeCommonBrowserRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("css/common_ie6_css.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/style_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/themes/common/common_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/themes/common/common_ie6.css",
			APPLICATIONS_DIR + "/test-app1/default-aspect/themes/common/common_ie6.css",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/style/blade_ie6.css"
		});
	}
	
	
	/* language only css tests */
	
	@Test
	public void testLocalesCanHaveLanaguageOnly() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/languageLocales-aspect")
		.whenRequestReceived("css/noir_de_css.bundle")
		.thenBundledFilesEquals(new String[] {
			APPLICATIONS_DIR + "/test-app1/languageLocales-aspect/themes/noir/noir_de.css"
		});
	}
	
	
}