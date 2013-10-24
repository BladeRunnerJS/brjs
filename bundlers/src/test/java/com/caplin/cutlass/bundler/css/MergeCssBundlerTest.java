package com.caplin.cutlass.bundler.css;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

import java.io.File;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.MergeTestHelper;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class MergeCssBundlerTest {
	
	private final static String ROOT_DIR = "src/test/resources/css-bundler/input/";
	private final static String APPLICATIONS_DIR = ROOT_DIR + CutlassConfig.APPLICATIONS_DIR;
	
	private MergeTestHelper test;
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(ROOT_DIR)));
		BladeRunnerSourceFileProvider.disableUsedBladesFiltering();
		test = new MergeTestHelper(new CssBundler());
	}
	
	@Test
	public void singleFileCanBeParsed() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/themes/theme1/style1.css"
		})
		.thenBundleIsCreated(APPLICATIONS_DIR + "/app1/a-bladeset/themes/theme1/style1.css");
	}
	
	@Test
	public void multipleFilesCanBeConcatenated() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/themes/theme1/style1.css",
			APPLICATIONS_DIR + "/app1/a-bladeset/themes/theme1/style2.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/simple-tests/style1+style2.css");
	}
	
	@Test
	public void cssFileWithMultipleUrlsIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/multiple-urls.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/multiple-urls.css");
	}
	
	@Test
	public void cssFileWithExternalAndNonLocalUrlsIsNotChanged() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/urls-that-shouldnt-be-parsed.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/urls-that-shouldnt-be-parsed.css");
	}
	
	@Test
	public void cssFileWithExternalsUrlIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/external-urls.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/external-urls.css");
	}
	
	@Test
	public void appAspectImageUrlIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/level-tests/app-level-with-url.css");
	}
	
	@Test
	public void bladesetImageUrlIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/themes/theme1/noir.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/level-tests/bladeset-level-with-url.css");
	}
	
	@Test
	public void bladeImageUrlIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/theme1/noir.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/level-tests/blade-level-with-url.css");
	}
	
	@Test
	public void workbenchImageUrlIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/resources/style/workbench.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/level-tests/workbench-level-with-url.css");
	}
	
	@Test
	public void rootLevelCssIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/root-level-url.css");
	}
	
	@Test
	public void childLevelCssIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/child-dir/url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/child-level-url.css");
	}
	
	@Test
	public void grandchildLevelCssIsParsedCorrectly() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/child-dir/grandchild-dir/url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/grandchild-level-url.css");
	}
	
	@Test
	public void rootLevelCssCanReferToChildDir() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/url-down.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/child-level-url.css");
	}
	
	@Test
	public void childLevelCssCanReferToRootDir() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/child-dir/url-up.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/root-level-url.css");
	}
	
	@Test
	public void childLevelCssCanReferToGrandchildDir() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/child-dir/url-down.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/grandchild-level-url.css");
	}
	
	@Test
	public void grandchildLevelCssCanReferToChildDir() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/child-dir/grandchild-dir/url-up.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/child-level-url.css");
	}
	
	@Test
	public void grandchildLevelCssCanReferToRootDir() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/child-dir/grandchild-dir/url-up-up.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/root-level-url.css");
	}
	
	@Test
	public void rootLevelCssCanReferToRootDirViaChildDir() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/url-down-then-up.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/root-level-url.css");
	}
	
	@Test
	public void theme1CssCanReferToCommonThemeCss() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/sideways-url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/external-url-tests/common-theme-url.css");
	}
	
	@Test
	public void blade1CssCanReferToBladesetCss() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/theme1/bladeset-url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/external-url-tests/bladeset-url.css");
	}
	@Ignore
	@Test
	public void blade1CssCanReferToLibraryImage() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/theme1/library-url.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/external-url-tests/library-url.css");
	}
	
	@Test
	public void workbenchCssCanReferToBladesetCss() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/resources/style/bladeset-url-3.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/external-url-tests/bladeset-url-3.css");
	}
	
	@Test
	public void workbenchCssCanReferToBladeCss() throws Exception
	{
		test.givenInputFiles(new String[] {
			APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/resources/style/bladeset-url-2.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/external-url-tests/bladeset-url-2.css");
	}
	
	// Sdk test
	
	@Test
	public void filesCanBeParsedFromSdkLevel() throws Exception
	{
		test.givenInputFiles(new String[] {
			"src/test/resources/css-bundler/input/" + SDK_DIR + "/libs/javascript/caplin/alerts/style1.css",
			"src/test/resources/css-bundler/input/" + SDK_DIR + "/libs/javascript/caplin/alerts/style2.css"
		})
		.thenBundleIsCreated("src/test/resources/css-bundler/output/merge-with-sdk-resources/style1+style2.css");
	}
	
	// Image urls from sdk level
	@Test
	public void applevelCssCanReferToSdkImageFile() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/main-aspect/themes/theme1/with-sdk-image-url.css"
			})
			.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/app-level-css-with-sdk-image.css");
	}
	
	@Test
	public void bladesetlevelCssCanReferToSdkImageFile() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/a-bladeset/themes/theme1/with-sdk-image-url.css"
			})
			.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/bladeset-level-css-with-sdk-image.css");
	}
	
	@Test
	public void bladelevelCssCanReferToSdkImageFile() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/theme1/with-sdk-image-url.css"
			})
			.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/blade-level-css-with-sdk-image.css");
	}
	
	@Test
	public void workbenchlevelCssCanReferToSdkImageFile() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/resources/style/with-sdk-image-url.css"
			})
			.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/wb-level-css-with-sdk-image.css");
	}
	
	@Test
	public void thirdpartylibrarylevelCssCanReferToThirdpartylibraryImageFile() throws Exception
	{
		test.givenInputFiles(new String[] {
				APPLICATIONS_DIR + "/app1/thirdparty-libraries/lib1/with-thirdpartylibrary-image-url.css"
			})
			.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/with-thirdpartylibrary-image-url.css");
	}
	
	@Test
	public void thirdpartylibrarylevelCssCanReferToThirdpartylibraryInSdkImageFile() throws Exception
	{
		test.givenInputFiles(new String[] {
				ROOT_DIR + "sdk/libs/javascript/thirdparty/lib1/with-thirdpartylibrary-image-in-sdk-url.css"
			})
			.thenBundleIsCreated("src/test/resources/css-bundler/output/url-tests/with-thirdpartylibrary-image-in-sdk-url.css");
	}

}

