package com.caplin.cutlass.bundler.css;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.structure.model.SdkModel;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;

public class StructureCssBundlerFilenameTests
{
	private BundlerFileTester test;

	@BeforeClass
	public static void setupSuite()
	{
		SdkModel.removeAllNodes();		
	}
	
	@Before
	public void setUp()
	{
		BladeRunnerSourceFileProvider.disableUsedBladesFiltering();
		test = new BundlerFileTester(new CssBundler(), "src/test/resources/css-bundler/input/" + APPLICATIONS_DIR + "/app1");
	}
	
	@Test
	public void themesWithDashesInName() throws Exception
	{
		test.givenDirectoryOnDisk("main-aspect")
		.whenRequestReceived("css/theme-with-hyphens-in-name_css.bundle")
		.thenBundledFilesEquals(new String[] {
			"a-bladeset/themes/theme-with-hyphens-in-name/my-theme.css",
			"a-bladeset/blades/blade1/themes/theme-with-hyphens-in-name/my-theme.css",
			"main-aspect/themes/theme-with-hyphens-in-name/my-theme.css"
		});
	}
	
	@Test
	public void themesWithDotsInName() throws Exception
	{
		test.givenDirectoryOnDisk("main-aspect")
		.whenRequestReceived("css/theme.with.dots.in.name_css.bundle")
		.thenBundledFilesEquals(new String[] {
			"a-bladeset/themes/theme.with.dots.in.name/my.theme.css",
			"a-bladeset/blades/blade1/themes/theme.with.dots.in.name/my.theme.css",
			"main-aspect/themes/theme.with.dots.in.name/my.theme.css"
		});
	}
	
}