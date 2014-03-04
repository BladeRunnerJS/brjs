package com.caplin.cutlass.bundler.image;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.CutlassConfig;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class ImageBundlerTest
{
	private static final File BASE_DIR = new File("src/test/resources/generic-bundler/bundler-structure-tests");
	private static final File APP_BASE = new File(BASE_DIR, CutlassConfig.APPLICATIONS_DIR + "/test-app2").getAbsoluteFile();
	private static final File DEFAULT_ASPECT = new File(APP_BASE, "/default-aspect").getAbsoluteFile();
	private static final File ALTERNATE_ASPECT = new File(APP_BASE, "/xtra-aspect");
	private static final File WORKBENCH = new File(APP_BASE, "/fx-bladeset/blades/fx-blade1/workbench");
	private static final File BLADE = new File(APP_BASE, "/fx-bladeset/blades/fx-blade1");
	private static final File BLADESET = new File(APP_BASE, "/fx-bladeset/blades");
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(BASE_DIR));
	}
	
	@Test
	public void testDefaultAspectRequestForSameLevelImageIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "default-aspect/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test
	public void testAlternateAspectRequestForSameLevelIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(ALTERNATE_ASPECT, null, "images/theme_noir/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "xtra-aspect/themes/noir/image.png").getAbsoluteFile()), files);
	}
	
	@Test
	public void testBladesetAspectRequestForSameLevelImageIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(BLADESET, null, "images/bladeset_fx/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test
	public void testBladeAspectRequestForSameLevelImageIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(BLADE, null, "images/bladeset_fx/blade_fx-blade1/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test
	public void testWorkbenchAspectRequestsForSameLevelImageIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(WORKBENCH, null, "images/bladeset_fx/blade_fx-blade1/workbench/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/workbench/resources/style/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test
	public void testBladesetImagesCanBeRequestedForDefaultAspectRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}

	@Test
	public void testBladeImagesCanBeRequestedForDefaultAspectRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test
	public void testBladesetImagesCanBeRequestedForBladeLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(BLADE, null, "images/bladeset_fx/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	} 
	
	@Test
	public void testAspectImagesCanBeRequestedForWorkbenchLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(WORKBENCH, null, "images/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "default-aspect/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	} 
	
	@Test
	public void testBladesetImagesCanBeRequestedForWorkbenchLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(WORKBENCH, null, "images/bladeset_fx/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	} 
	
	@Test
	public void testBladeImagesCanBeRequestedForWorkbenchLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(WORKBENCH, null, "images/bladeset_fx/blade_fx-blade1/theme_noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	} 
	
	@Test
	public void testJpgRequestIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/theme_noir/picture.jpg_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/themes/noir/picture.jpg").getAbsoluteFile()), files);
	}
	
	@Test
	public void testJpegUpperCaseRequestIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/theme_noir/picture.JPEG_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/themes/noir/picture.JPEG").getAbsoluteFile()), files);
	}
	
	@Test
	public void testGifRequestIsTransformedCorrectly() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/theme_common/img/imageWithGifExtension.gif_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "default-aspect/themes/common/img/imageWithGifExtension.gif").getAbsoluteFile()), files);
	}

	@Test
	public void testImageOnOutputStreamMatchesImageOnDisk() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/theme_noir/image.png_image.bundle");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		File fileOnDisk = new File(APP_BASE, "default-aspect/themes/noir/image.png");
		bundler.writeBundle(files, out);

		assertArrayEquals(FileUtils.readFileToByteArray(fileOnDisk), out.toByteArray());
		assertTrue(fileOnDisk.exists());
	}
	
	@Test
	public void testImageOnOutputStreamMatchesImageOnDiskForGifFiles() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/theme_common/img/imageWithGifExtension.gif_image.bundle");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bundler.writeBundle(files, out);

		File fileOnDisk = new File(APP_BASE, "default-aspect/themes/common/img/imageWithGifExtension.gif");

		assertArrayEquals(FileUtils.readFileToByteArray(fileOnDisk), out.toByteArray());
		assertTrue(fileOnDisk.exists());
	}
	
	@Test
	public void testImageOnOutputStreamMatchesImageOnDiskForJpgFiles() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/theme_noir/picture.jpg_image.bundle");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bundler.writeBundle(files, out);

		File fileOnDisk = new File(APP_BASE, "fx-bladeset/blades/fx-blade1/themes/noir/picture.jpg");

		assertArrayEquals(FileUtils.readFileToByteArray(fileOnDisk), out.toByteArray());
		assertTrue(fileOnDisk.exists());
	}
	
	// Invalid Request and Bundler Exception Test
	@Test(expected = RequestHandlingException.class)
	public void testCheckInvalidRequest() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		bundler.getBundleFiles(new File("."), null, "images/bladeset_my-bladeset/SOME_NONSENSE/blade_my-blade/theme_my-theme/image.png_image.bundle");
	}
	
	@Test(expected = RequestHandlingException.class)
	public void appLevelDefaultAspectImagesCanNotBeRequestedForBladesetLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/../default-aspect/themes/noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/../default-aspect/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test(expected = RequestHandlingException.class)
	public void appLevelDefaultAspectImagesCanNotBeRequestedForBladeLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/../../../default-aspect/themes/noir/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/../../../default-aspect/themes/noir/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test(expected = RequestHandlingException.class)
	public void appLevelDefaultAspectImagesCanNotBeRequestedForWorkbenchLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/workbench/../../../../../../default-aspect/themes/noir/my/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/workbench/resources/style/../../../../../../default-aspect/themes/noir/my/image.png").getAbsoluteFile()), files);
	}
	
	@Test(expected = RequestHandlingException.class)
	public void workbenchImagesCanNotBeRequestedForAppDefaultAspectLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/workbench/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/workbench/resources/style/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test(expected = RequestHandlingException.class)
	public void workbenchImagesCanNotBeRequestedForBladesetLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/workbench/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/workbench/resources/style/my/own/image.png").getAbsoluteFile()), files);
	}

	@Test(expected = RequestHandlingException.class)
	public void workbenchImagesCanNotBeRequestedForAppAlternateAspectLevelRequests() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(ALTERNATE_ASPECT, null, "images/bladeset_fx/blade_fx-blade1/workbench/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(APP_BASE, "fx-bladeset/blades/fx-blade1/workbench/resources/style/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testCheckValidRequestForNonExistentFile() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/theme_common/img/wrongUrlImage.gif_image.bundle");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bundler.writeBundle(files, out);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testCheckValidRequestForNonExistentFileAtSdkLevel() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/sdk/caplin/chart/non-existant-image.gif_image.bundle");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bundler.writeBundle(files, out);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testCheckValidRequestForNonExistentFolderAtSdkLevel() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/sdk/caplin/no-such-folder/non-existant-image.gif_image.bundle");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bundler.writeBundle(files, out);
	}
	
	@Test
	public void testGetValidRequestStringsForSimpleImageOnDisk() throws Exception {
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getImages()).thenReturn(Arrays.asList(new File(APP_BASE, "default-aspect/themes/noir/my/own/image.png")));
		ImageBundler bundler = new ImageBundler();
		
		assertEquals(Arrays.asList("images/theme_noir/my/own/image.png_image.bundle"), bundler.getValidRequestStrings(metaData));
	}
	
	@Test @Ignore
	public void testGetValidRequestStringsForSimpleImageOnDiskFromSDK() throws Exception {
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getImages()).thenReturn(Arrays.asList(new File(BASE_DIR, SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/my/own/image.png")));
		ImageBundler bundler = new ImageBundler();
		
		assertEquals(Arrays.asList("images/sdk/caplin/alerts/my/own/image.png_image.bundle"), bundler.getValidRequestStrings(metaData));
	}
	
	@Test @Ignore
	public void testCutlassSdkThemeRedirectsToSdkResources() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/sdk/caplin/alerts/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(BASE_DIR, SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test @Ignore
	public void testCutlassSdkRedirectsToSdkResources() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/sdk/caplin/alerts/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(BASE_DIR, SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test @Ignore
	public void testCutlassSdkRedirectsToSdkResourcesFromBladeset() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(BLADESET, null, "images/sdk/caplin/alerts/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(BASE_DIR, SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/my/own/image.png").getAbsoluteFile()), files);
	}

	@Test @Ignore
	public void testCutlassSdkRedirectsToSdkResourcesFromBlade() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(BLADE, null, "images/sdk/caplin/alerts/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(BASE_DIR, SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/my/own/image.png").getAbsoluteFile()), files);
	}
	
	@Test @Ignore
	public void testCutlassSdkRedirectsToSdkResourcesFromWorkbench() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		List<File> files = bundler.getBundleFiles(WORKBENCH, null, "images/sdk/caplin/alerts/my/own/image.png_image.bundle");
		assertFileListEquals(Arrays.asList(new File(BASE_DIR, SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/my/own/image.png").getAbsoluteFile()), files);
	}

	@Test(expected = RequestHandlingException.class)
	public void testMisspeltCutlassSdkThrowsMalformedBundlerRequestException() throws Exception
	{
		ImageBundler bundler = new ImageBundler();
		bundler.getBundleFiles(DEFAULT_ASPECT, null, "images/sbk/caplin/alerts/my/own/image.png_image.bundle");
	}
	
	private void assertFileListEquals(List<File> expected, List<File> actual)
	{
		assertEquals("expected and actual list sizes are different", expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
		{
			assertEquals("file paths are not equal", expected.get(i).getAbsoluteFile(), actual.get(i).getAbsoluteFile());
		}
	}

}
