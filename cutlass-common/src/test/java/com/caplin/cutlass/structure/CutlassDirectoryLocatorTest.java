package com.caplin.cutlass.structure;

import static com.caplin.cutlass.structure.CutlassDirectoryLocator.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.testing.BRJSTestFactory;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class CutlassDirectoryLocatorTest
{

	private static final List<File> EMPTY_FILE_LIST = new ArrayList<File>();

	private static final String testBase = "src/test/resources/ExampleAppStructure";
	
	private File tempDir;
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
	}
	
	@After
	public void teardown()
	{
		if (tempDir != null)
		{
			tempDir.deleteOnExit();			
		}
	}
	
	@Test
	public void testGetScope() throws Exception
	{
		assertEquals( ScopeLevel.UNKNOWN_SCOPE, getScope(new File(testBase)));
		assertEquals( ScopeLevel.UNKNOWN_SCOPE, getScope(new File(testBase + "/apps/")));
		assertEquals( ScopeLevel.APP_SCOPE, getScope(new File(testBase + "/apps/app1")));
		assertEquals( ScopeLevel.ASPECT_SCOPE, getScope(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals( ScopeLevel.ASPECT_SCOPE, getScope(new File(testBase + "/apps/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( ScopeLevel.ASPECT_SCOPE, getScope(new File(testBase + "/apps/app1/a-aspect/tests/some-test-dir")));
		assertEquals( ScopeLevel.BLADESET_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals( ScopeLevel.BLADESET_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( ScopeLevel.BLADESET_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/tests/some-test-dir")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/themes/noir")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/tests/some-test-dir")));
		assertEquals( ScopeLevel.WORKBENCH_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( ScopeLevel.WORKBENCH_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( ScopeLevel.WORKBENCH_SCOPE, getScope(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/workbench/tests/some-test-dir")));
		assertEquals( ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, getScope(new File(testBase + "/apps/app1/thirdparty-libraries")));
		assertEquals( ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, getScope(new File(testBase + "/apps/app1/thirdparty-libraries/jquery")));
		assertEquals( ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, getScope(new File(testBase + "/apps/app1/thirdparty-libraries/jquery/jQuery.js")));
		assertEquals( ScopeLevel.SDK_SCOPE, getScope(new File(testBase + "/sdk/")));
		assertEquals( ScopeLevel.SDK_SCOPE, getScope(new File(testBase + "/sdk/a-dir")));
		assertEquals( ScopeLevel.SDK_SCOPE, getScope(new File(testBase + "/sdk/a-dir/empty.txt")));
		
		assertEquals( ScopeLevel.APP_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard")));
		assertEquals( ScopeLevel.ASPECT_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/main-aspect")));
		assertEquals( ScopeLevel.ASPECT_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( ScopeLevel.ASPECT_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/main-aspect/tests/some-test-dir")));
		assertEquals( ScopeLevel.BLADESET_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset")));
		assertEquals( ScopeLevel.BLADESET_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( ScopeLevel.BLADESET_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/tests/some-test-dir")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( ScopeLevel.BLADE_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/tests/some-test-dir")));
		assertEquals( ScopeLevel.WORKBENCH_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( ScopeLevel.WORKBENCH_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( ScopeLevel.WORKBENCH_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/workbench/tests/some-test-dir")));
		assertEquals( ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/thirdparty-libraries")));
		assertEquals( ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/thirdparty-libraries/extjs")));
		assertEquals( ScopeLevel.THIRDPARTY_LIBRARY_SCOPE, getScope(new File(testBase + "/sdk/system-applications/dashboard/thirdparty-libraries/extjs/extjs.js")));
		assertEquals( ScopeLevel.SDK_SCOPE, getScope(new File(testBase + "/sdk/a-dir/non-existant.file")));

		assertEquals( ScopeLevel.UNKNOWN_SCOPE, getScope(new File("src/test/resources/")));
		assertEquals( ScopeLevel.UNKNOWN_SCOPE, getScope(null));
	}
	
	@Test
	public void testGetRootDir() throws Exception
	{
		assertEquals(new File(testBase), getRootDir(new File(testBase)));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase), getRootDir(new File(testBase + "/sdk/a-dir/empty.txt")));

		assertEquals(new File(testBase), getRootDir(new File(testBase + "/sdk/a-dir/non-existant.dir/file.txt")));
		assertEquals(null, getRootDir(new File("src/test/resources/")));
		assertEquals(null, getRootDir(null));
	}
	
	@Test
	public void testGetConfDir() throws Exception
	{
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase)));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/sdk/a-dir/empty.txt")));

		assertEquals(new File(testBase + "/conf"), getConfigDir(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getConfigDir(new File("src/test/resources/")));
		assertEquals(null, getConfigDir(null));
	}
	
	@Test
	public void testGetTempDir() throws Exception
	{
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase)));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/sdk/a-dir/empty.txt")));
		
		assertEquals(new File(testBase + "/cutlass-tmp"), getTempDir(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getConfigDir(new File("src/test/resources/")));
		assertEquals(null, getConfigDir(null));
	}
	
	@Test
	public void testTestResultsDir() throws Exception
	{
		assertEquals(new File(testBase+ "/test-results"), getTestResultsDir(new File(testBase)));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/sdk/a-dir/empty.txt")));

		assertEquals(new File(testBase + "/test-results"), getTestResultsDir(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getTestResultsDir(new File("src/test/resources/")));
		assertEquals(null, getTestResultsDir(null));
	}
	
	
	@Test
	public void testSdkRootDir() throws Exception
	{
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase)).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/apps/")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/apps/app1")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/apps/app1/a-aspect")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/apps/app1/a-bladeset")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/sdk/")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/sdk/a-dir")).getDir());
		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/sdk/a-dir/empty.txt")).getDir());

		assertEquals(new File(testBase + "/sdk"), SdkModel.getSdkPath(new File(testBase + "/sdk/a-dir/non-existant.file")).getDir());
		assertNull(SdkModel.getSdkPath(new File("src/test/resources/")).getDir());
		assertNull(SdkModel.getSdkPath(null).getDir());
	}

	@Test
	public void testAppRootDir() throws Exception
	{
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase)));
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/apps"), getAppRootDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications"), getAppRootDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/sdk/system-applications"), getAppRootDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/sdk/system-applications"), getAppRootDir(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications"), getAppRootDir(new File(testBase + "/sdk/system-applications/dashboard/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications"), getAppRootDir(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/tests/test-integration/webdriver")));
		
		assertEquals(new File(testBase + "/sdk/system-applications"), getAppRootDir(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getAppRootDir(new File("src/test/resources/")));
		assertEquals(null, getAppRootDir(null));
	}

	@Test
	public void testAppAspectRootDir() throws Exception
	{
		assertEquals(null, getParentAppAspect(new File(testBase)));
		assertEquals(null, getParentAppAspect(new File(testBase + "/apps/")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/apps/app1/a-aspect"), getParentAppAspect(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/apps/app1/a-aspect"), getParentAppAspect(new File(testBase + "/apps/app1/a-aspect/libs/src/empty.txt")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/sdk/")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/sdk/a-dir")));
		assertEquals(null, getParentAppAspect(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/main-aspect"), getParentAppAspect(new File(testBase + "/sdk/system-applications/dashboard/main-aspect")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/main-aspect"), getParentAppAspect(new File(testBase + "/sdk/system-applications/dashboard/main-aspect/index.html")));

		assertEquals(null, getParentAppAspect(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getParentAppAspect(new File("src/test/resources/")));
		assertEquals(null, getParentAppAspect(null));
	}

	@Test
	public void testGetParentApp() throws Exception
	{
		assertEquals(null, getParentApp(new File(testBase)));
		assertEquals(null, getParentApp(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/apps/app1"), getParentApp(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/apps/app1"), getParentApp(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/apps/app1"), getParentApp(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/apps/app1"), getParentApp(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/apps/app1"), getParentApp(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(null, getParentApp(new File(testBase + "/sdk/")));
		assertEquals(null, getParentApp(new File(testBase + "/sdk/a-dir")));
		assertEquals(null, getParentApp(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard"), getParentApp(new File(testBase + "/sdk/system-applications/dashboard/empty.txt")));

		assertEquals(null, getParentApp(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getParentApp(new File("src/test/resources/")));
		assertEquals(null, getParentApp(null));
	}

	@Test
	public void testGetParentBladeset() throws Exception
	{
		assertEquals(null, getParentBladeset(new File(testBase)));
		assertEquals(null, getParentBladeset(new File(testBase + "/apps/")));
		assertEquals(null, getParentBladeset(new File(testBase + "/apps/app1")));
		assertEquals(null, getParentBladeset(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset"), getParentBladeset(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset"), getParentBladeset(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset"), getParentBladeset(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(null, getParentBladeset(new File(testBase + "/sdk/")));
		assertEquals(null, getParentBladeset(new File(testBase + "/sdk/a-dir")));
		assertEquals(null, getParentBladeset(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset"), getParentBladeset(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset"), getParentBladeset(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/empty.txt")));

		assertEquals(null, getParentBladeset(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getParentBladeset(new File("src/test/resources/")));
		assertEquals(null, getParentBladeset(null));
	}

	@Test
	public void testGetParentBlade() throws Exception
	{
		assertEquals(null, getParentBlade(new File(testBase)));
		assertEquals(null, getParentBlade(new File(testBase + "/apps/")));
		assertEquals(null, getParentBlade(new File(testBase + "/apps/app1")));
		assertEquals(null, getParentBlade(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(null, getParentBlade(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset/blades/blade1"), getParentBlade(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset/blades/blade1"), getParentBlade(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(null, getParentBlade(new File(testBase + "/sdk/")));
		assertEquals(null, getParentBlade(new File(testBase + "/sdk/a-dir")));
		assertEquals(null, getParentBlade(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1"), getParentBlade(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1"), getParentBlade(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));

		assertEquals(null, getParentBlade(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getParentBlade(new File("src/test/resources/")));
		assertEquals(null, getParentBlade(null));
	}

	/* TODO: change this test so it uses its own set of apps so we dont have to change it whenever a new app is added */
	@Test
	public void testGetApps() throws Exception
	{
		List<File> expectedApps = Arrays.asList(new File(testBase + "/apps/app-with-en_GB-locale"),
												new File(testBase + "/apps/app1"), 
												new File(testBase + "/apps/app2"),
												new File(testBase + "/apps/app4"),
												new File(testBase + "/apps/invalid-conf"),
												new File(testBase + "/apps/no-app-conf"),
												new File(testBase + "/apps/no-namespace"));

		assertEquals(expectedApps, getChildApps(new File(testBase)));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/apps/")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/apps/app1")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/sdk/")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/sdk/a-dir")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/sdk/a-dir/non-existant.file")));

		expectedApps = Arrays.asList(new File(testBase + "/sdk/system-applications/dashboard"));
		assertEquals(expectedApps, getChildApps(new File(testBase + "/sdk/system-applications")));
		
		assertEquals(null, getChildApps(new File("src/test/resources/")));
		assertEquals(null, getChildApps(null));
	}

	@Test
	public void testGetChildBladesets() throws Exception
	{
		List<File> expectedBladesets = Arrays.asList(new File(testBase + "/apps/app1/a-bladeset"), new File(testBase + "/apps/app1/another-bladeset"));

		assertEquals(null, getChildBladesets(new File(testBase)));
		assertEquals(null, getChildBladesets(new File(testBase + "/apps/")));
		assertEquals(expectedBladesets, getChildBladesets(new File(testBase + "/apps/app1")));
		assertEquals(expectedBladesets, getChildBladesets(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(expectedBladesets, getChildBladesets(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(expectedBladesets, getChildBladesets(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(expectedBladesets, getChildBladesets(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(null, getChildBladesets(new File(testBase + "/sdk/")));
		assertEquals(null, getChildBladesets(new File(testBase + "/sdk/a-dir")));
		assertEquals(null, getChildBladesets(new File(testBase + "/sdk/a-dir/empty.txt")));

		expectedBladesets = Arrays.asList(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset"));
		assertEquals(expectedBladesets, getChildBladesets(new File(testBase + "/sdk/system-applications/dashboard")));
		
		assertEquals(null, getChildBladesets(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getChildBladesets(new File("src/test/resources/")));
		assertEquals(null, getChildBladesets(null));
	}

	@Test
	public void testGetChildBlades() throws Exception
	{
		List<File> expectedBlades = Arrays.asList(new File(testBase + "/apps/app1/a-bladeset/blades/blade1"), new File(testBase + "/apps/app1/a-bladeset/blades/blade2"));

		assertEquals(0, getChildBlades(new File(testBase)).size());
		assertEquals(0, getChildBlades(new File(testBase + "/apps/")).size());
		assertEquals(0, getChildBlades(new File(testBase + "/apps/app1")).size());
		assertEquals(0, getChildBlades(new File(testBase + "/apps/app1/a-aspect")).size());
		assertEquals(expectedBlades, getChildBlades(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(expectedBlades, getChildBlades(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(expectedBlades, getChildBlades(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(0, getChildBlades(new File(testBase + "/sdk/")).size());
		assertEquals(0, getChildBlades(new File(testBase + "/sdk/a-dir")).size());
		assertEquals(0, getChildBlades(new File(testBase + "/sdk/a-dir/empty.txt")).size());
		
		expectedBlades = Arrays.asList(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1"));
		assertEquals(expectedBlades, getChildBlades(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset")));

		assertEquals(0, getChildBlades(new File(testBase + "/sdk/a-dir/non-existant.file")).size());
		assertEquals(0, getChildBlades(new File("src/test/resources/")).size());
		assertEquals(0, getChildBlades(null).size());
	}

	@Test
	public void getWorkbenchDir() throws Exception
	{
		assertEquals(null, getBladeWorkbenchDir(new File(testBase)));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/apps/")));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/apps/app1")));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/workbench"), getBladeWorkbenchDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/workbench"), getBladeWorkbenchDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/sdk/")));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/sdk/a-dir/empty.txt")));

		assertEquals(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1/workbench"), getBladeWorkbenchDir(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1")));
		
		assertEquals(null, getBladeWorkbenchDir(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getBladeWorkbenchDir(new File("src/test/resources/")));
		assertEquals(null, getBladeWorkbenchDir(null));
	}
	
	@Test
	public void testGetApplicationAspects() throws Exception
	{
		List<File> aspects = Arrays.asList(	new File(testBase + "/apps/app1/a-aspect"), 
											new File(testBase + "/apps/app1/another-aspect"));
				
		
		assertEquals(EMPTY_FILE_LIST, getApplicationAspects(new File(testBase)));
		assertEquals(EMPTY_FILE_LIST, getApplicationAspects(new File(testBase + "/apps/")));
		assertEquals(aspects, getApplicationAspects(new File(testBase + "/apps/app1")));
		assertEquals(aspects, getApplicationAspects(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(aspects, getApplicationAspects(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(aspects, getApplicationAspects(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(EMPTY_FILE_LIST, getApplicationAspects(new File(testBase + "/sdk/")));
		assertEquals(EMPTY_FILE_LIST, getApplicationAspects(new File(testBase + "/sdk/a-dir")));
		assertEquals(Arrays.asList(	new File(testBase + "/sdk/system-applications/dashboard/main-aspect")), getApplicationAspects(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals(EMPTY_FILE_LIST, getApplicationAspects(new File("src/test/resources/")));
	}
	
	@Test
	public void testGetApplications() throws Exception
	{
		List<File> applications = Arrays.asList(new File(testBase + "/apps/app-with-en_GB-locale"),
												new File(testBase + "/apps/app1"), 
												new File(testBase + "/apps/app2"),
												new File(testBase + "/apps/app4"),
												new File(testBase + "/apps/invalid-conf"),
												new File(testBase + "/apps/no-app-conf"),
												new File(testBase + "/apps/no-namespace"));
		
		assertEquals(applications, getApplications(new File(testBase)));
		assertEquals(applications, getApplications(new File(testBase + "/apps/")));
		assertEquals(applications, getApplications(new File(testBase + "/apps/app1")));
		assertEquals(applications, getApplications(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(applications, getApplications(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(applications, getApplications(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(applications, getApplications(new File(testBase + "/sdk/")));
		assertEquals(applications, getApplications(new File(testBase + "/sdk/a-dir")));
		assertEquals(applications, getApplications(new File(testBase + "/sdk/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals(EMPTY_FILE_LIST, getApplications(new File("src/test/resources/")));
	}
	
	@Test 
	public void testGetApplicationsWithHiddenFiles() throws Exception
	{
		if (!System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			tempDir = FileUtility.createTemporaryDirectory("CutlassDirectoryLocator");
			FileUtility.copyDirectoryContents(new File(testBase), tempDir);
			File appsDir = new File(tempDir + "/apps");
			List<File> applications = Arrays.asList(new File(appsDir, "app-with-en_GB-locale"),
													new File(appsDir, "app1"), 
													new File(appsDir, "app2"),
													new File(appsDir, "app4"),
													new File(appsDir, "invalid-conf"),
													new File(appsDir, "no-app-conf"),
													new File(appsDir, "no-namespace"));
			
			FileUtility.createHiddenFileAndFolder(appsDir);
			assertTrue(new File(appsDir, ".hiddenDir").exists());
			assertTrue(new File(appsDir, ".hiddenFile").exists());
			
			assertEquals(applications, getApplications(appsDir));			
		}
	}

	@Test
	public void testDatabaseDir() throws Exception
	{
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase)));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/sdk/a-dir/empty.txt")));

		assertEquals(new File(testBase + "/webcentric-db"), getDatabaseRootDir(new File(testBase + "/sdk/a-dir/non-existant.file")));
		assertEquals(null, getDatabaseRootDir(new File("src/test/resources/")));
		assertEquals(null, getDatabaseRootDir(null));
	}
	
	@Test
	public void testDirectoryLocatorHasKnowledgeOfPerforceStructure() throws Exception
	{
		String testBase = "src/test/resources/PerforceStructureTest";
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
		assertEquals(new File(testBase + "/cutlass-libraries"), getRootDir(new File(testBase + "/cutlass-libraries")));
		assertEquals(new File(testBase + "/cutlass-libraries"), getRootDir(new File(testBase + "/cutlass-libraries/sdk/libs/javascript/caplin/src/caplin/alerts/empty.txt")));
		assertEquals(new File(testBase + "/cutlass-libraries/" + SDK_DIR), SdkModel.getSdkPath(new File(testBase + "/cutlass-libraries/sdk/libs/javascript/caplin/src/caplin/alerts/empty.txt")).getDir());
	}

	@Test
	public void testGettingSdkLevelResources() throws Exception
	{
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase)));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/sdk/libs/javascript/caplin/src/caplin")));

		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File(testBase + "/sdk/a-dir/non-existant.dir/file.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/resources").getAbsoluteFile(), getSdkJsResourcesDir(new File("src/test/resources/")));
		assertEquals(null, getSdkJsResourcesDir(null));
	}

	@Test
	public void testGettingSdkLevelCaplinSrc() throws Exception
	{		
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase)));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/sdk/libs/javascript/caplin/src/caplin")));

		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File(testBase + "/sdk/a-dir/non-existant.dir/file.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/caplin/src").getAbsoluteFile(), getSDkCaplinSrcDir(new File("src/test/resources/")));
		assertEquals(null, getSDkCaplinSrcDir(null));
	}
	
	@Test
	public void testGettingSdkLevelThirdpartySrc() throws Exception
	{
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase)));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/apps/")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/apps/app1")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/apps/app1/a-aspect")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/apps/app1/a-bladeset")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/apps/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/sdk/")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/sdk/a-dir")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/sdk/a-dir/empty.txt")));
		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/sdk/libs/javascript/caplin/src/caplin")));

		assertEquals(new File(testBase + "/sdk/libs/javascript/thirdparty"), getSDkThirdpartySrcDir(new File(testBase + "/sdk/a-dir/non-existant.dir/file.txt")));
		assertEquals(null, getSDkThirdpartySrcDir(new File("src/test/resources/")));
		assertEquals(null, getSDkThirdpartySrcDir(null));
	}
	
	@Test
	public void testBladsetsMustBeNamed() throws Exception
	{
		assertFalse( CutlassDirectoryLocator.isBladesetDir(new File(testBase + "/apps/app1/bladeset")) );
		assertTrue( CutlassDirectoryLocator.isBladesetDir(new File(testBase + "/apps/app1/a-bladeset")) );
	}
	
	@Test
	public void testGetApplicationsOnlyReturnsDirectoriesAndNotFiles() throws Exception
	{
		File sdkBaseDir = new File("src/test/resources/UtilityStructureTests/get-applications/" + SDK_DIR);
		File applicationsDir = new File(sdkBaseDir.getParentFile(), APPLICATIONS_DIR);
		assertTrue(new File(applicationsDir, "app1").exists());
		assertTrue(new File(applicationsDir, "app2").exists());
		assertTrue(new File(applicationsDir, "fileA.txt").exists());
		assertTrue(new File(applicationsDir, "fileB.txt").exists());
		
		List<File> appList = CutlassDirectoryLocator.getApplications(sdkBaseDir);
		
		assertTrue(appList.size() == 2);
		assertTrue(appList.get(0).getName().equals("app1"));
		assertTrue(appList.get(1).getName().equals("app2"));
	}
}
