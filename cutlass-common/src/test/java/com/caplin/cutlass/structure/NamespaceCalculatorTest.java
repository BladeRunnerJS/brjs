package com.caplin.cutlass.structure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.BRJSAccessor;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class NamespaceCalculatorTest
{
	
	private static final String testBase = "src/test/resources/ExampleAppStructure";

	@Before
	public void setup() {
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
	}
	
	@Test
	public void testGetPackageNamespaceForBladeLevelResources() throws Exception
	{
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase)));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx.a.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "caplinx.a.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx.a.blade1.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "caplinx.a.blade1.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/a-dir")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/a-dir/empty.txt")));
		
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx.a.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "caplinx.a.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx.a.blade1.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "caplinx.a.blade1.", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/a-dir/non-existant.file")));

		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File("src/test/resources/")));
		assertEquals( "", NamespaceCalculator.getPackageNamespaceForBladeLevelResources(null));
	}

	@Test(expected = Exception.class) @Ignore
	public void testIfNoAspectInApplicationExistsErrorIsThrown() throws Exception
	{
		NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File("src/test/resources/NamespaceAppStructure/" + APPLICATIONS_DIR + "/appwithnosection/a-bladeset"));
	}
	
	@Test(expected = Exception.class) @Ignore
	public void testIfMultipleNamespacesInApplicationExistsErrorIsThrown() throws Exception
	{
		NamespaceCalculator.getPackageNamespaceForBladeLevelResources(new File("src/test/resources/NamespaceAppStructure/" + APPLICATIONS_DIR + "/appwithmultiplenamespaces/a-bladeset"));
	}
	
	@Test
	public void testGetApplicationNamespace() throws Exception
	{
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
	}
	
	@Test 
	public void testGetApplicationNamespaceWhenAspectContainsSvnFolder() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("SvnFolderTest");
		
		File testResourceFolder = new File(testBase);
		FileUtility.copyDirectoryContents(testResourceFolder, tempDir);
		File tempAppDir = new File(tempDir, APPLICATIONS_DIR + File.separator + "app1");
		
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempAppDir));
		assertTrue(tempAppDir.exists());
		
		File svnDir = new File(tempAppDir, "a-aspect/src/.svn");
		svnDir.mkdir();
		assertTrue(svnDir.exists() && svnDir.isDirectory());

		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(tempAppDir, "a-aspect")));
	}
	
	@Test 
	public void testGetApplicationNamespaceLogsExtraFolderMessages() throws Exception
	{
		PrintStream originalStdErr = System.err;
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			PrintStream newStdErr = new PrintStream(output);
			System.setErr(newStdErr);
			
			File tempDir = FileUtility.createTemporaryDirectory("extraDirTest");
			
			File testResourceFolder = new File(testBase);
			FileUtility.copyDirectoryContents(testResourceFolder, tempDir);
			File tempAppDir = new File(tempDir, APPLICATIONS_DIR + File.separator + "app1");
			
			BRJSAccessor.initialize(BRJSTestFactory.createBRJS(tempAppDir));
			assertTrue(tempAppDir.exists());
			
			File extraDir = new File(tempAppDir, "a-aspect/src/extraDir");
			extraDir.mkdir();
			assertTrue(extraDir.exists() && extraDir.isDirectory());
			
			assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(tempAppDir, "a-aspect")));
		}
		finally
		{
			System.setErr(originalStdErr);
		}
	}
	
	@Test
	public void testGetBladesetNamespace() throws Exception
	{
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase)));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "another", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/another-bladeset")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/a-dir")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/a-dir/empty.txt")));
		
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "a", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File(testBase + "/" + SDK_DIR + "/a-dir/non-existant.file")));

		assertEquals( "", NamespaceCalculator.getBladesetNamespace(new File("src/test/resources/")));
	}
	
	@Test
	public void testGetBladeNamespace() throws Exception
	{
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase)));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/another-bladeset")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "blade2", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade2")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/a-dir")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/a-dir/empty.txt")));
		
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "blade1", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File(testBase + "/" + SDK_DIR + "/a-dir/non-existant.file")));

		assertEquals( "", NamespaceCalculator.getBladeNamespace(new File("src/test/resources/")));
	}
	
	@Ignore //TODO: is this now a valid test case with the model?
	@Test
	public void testGetApplicationNamespaceWithApplicationsOfTheSameNameInDifferentLocations() throws Exception
	{
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "novox", NamespaceCalculator.getAppNamespace(new File("src/test/resources/NamespaceAppStructure/" + APPLICATIONS_DIR + "/app1")));
	}
	
	@Test
	public void testGetApplicationNamespaceWithApplicationSrcFolderWhichContainsFolderAndFile() throws Exception
	{
		assertEquals( "caplinx", NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/app4")));
	}
	
	@Test(expected=NamespaceException.class)
	public void testGettingNamespaceWithInvalidConf() throws Exception
	{
		NamespaceCalculator.getAppNamespace(new File(testBase + "/" + APPLICATIONS_DIR + "/no-namespace"));
	}
	
	
}
