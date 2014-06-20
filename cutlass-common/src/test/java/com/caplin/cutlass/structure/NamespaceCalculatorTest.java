package com.caplin.cutlass.structure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.bladerunnerjs.model.TestModelAccessor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.exception.NamespaceException;
import org.bladerunnerjs.model.StaticModelAccessor;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class NamespaceCalculatorTest extends TestModelAccessor
{
	
	private static final String testBase = "src/test/resources/ExampleAppStructure";

	@Before
	public void setup() throws Exception {
		StaticModelAccessor.destroy();
		StaticModelAccessor.initializeModel(createModel(new File(testBase)));
	}
	
	@Test
	public void testGetPackageNamespaceForBladeLevelResources() throws Exception
	{
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase)));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx.a.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "caplinx.a.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx.a.blade1.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "caplinx.a.blade1.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/a-dir")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/a-dir/empty.txt")));
		
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx.a.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "caplinx.a.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx.a.blade1.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "caplinx.a.blade1.", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File(testBase + "/" + SDK_DIR + "/a-dir/non-existant.file")));

		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File("src/test/resources/")));
		assertEquals( "", RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(null));
	}

	@Test(expected = Exception.class) @Ignore
	public void testIfNoAspectInApplicationExistsErrorIsThrown() throws Exception
	{
		RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File("src/test/resources/NamespaceAppStructure/" + APPLICATIONS_DIR + "/appwithnosection/a-bladeset"));
	}
	
	@Test(expected = Exception.class) @Ignore
	public void testIfMultipleNamespacesInApplicationExistsErrorIsThrown() throws Exception
	{
		RequirePrefixCalculator.getPackageRequirePrefixForBladeLevelResources(new File("src/test/resources/NamespaceAppStructure/" + APPLICATIONS_DIR + "/appwithmultiplenamespaces/a-bladeset"));
	}
	
	@Test
	public void testGetApplicationNamespace() throws Exception
	{
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
	}
	
	@Test 
	public void testGetApplicationNamespaceWhenAspectContainsSvnFolder() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("SvnFolderTest");
		
		File testResourceFolder = new File(testBase);
		FileUtility.copyDirectoryContents(testResourceFolder, tempDir);
		File tempAppDir = new File(tempDir, APPLICATIONS_DIR + File.separator + "app1");
		
		StaticModelAccessor.destroy();
		StaticModelAccessor.initializeModel(createModel(tempAppDir));
		assertTrue(tempAppDir.exists());
		
		File svnDir = new File(tempAppDir, "a-aspect/src/.svn");
		svnDir.mkdir();
		assertTrue(svnDir.exists() && svnDir.isDirectory());

		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(tempAppDir, "a-aspect")));
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
			
			StaticModelAccessor.destroy();
			StaticModelAccessor.initializeModel(createModel(tempAppDir));
			assertTrue(tempAppDir.exists());
			
			File extraDir = new File(tempAppDir, "a-aspect/src/extraDir");
			extraDir.mkdir();
			assertTrue(extraDir.exists() && extraDir.isDirectory());
			
			assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(tempAppDir, "a-aspect")));
		}
		finally
		{
			System.setErr(originalStdErr);
		}
	}
	
	@Test
	public void testGetBladesetNamespace() throws Exception
	{
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase)));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "another", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/another-bladeset")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/a-dir")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/a-dir/empty.txt")));
		
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "a", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File(testBase + "/" + SDK_DIR + "/a-dir/non-existant.file")));

		assertEquals( "", RequirePrefixCalculator.getBladesetRequirePrefix(new File("src/test/resources/")));
	}
	
	@Test
	public void testGetBladeNamespace() throws Exception
	{
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase)));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-aspect/themes/noir/noir.css")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/another-bladeset")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.css")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")));
		assertEquals( "blade2", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade2")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/a-dir")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/a-dir/empty.txt")));
		
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/main-aspect/themes/noir/noir.css")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/themes/noir/noir.css")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/empty.txt")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench")));
		assertEquals( "blade1", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/system-applications/dashboard/a-bladeset/blades/blade1/workbench/workbench.css")));
		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File(testBase + "/" + SDK_DIR + "/a-dir/non-existant.file")));

		assertEquals( "", RequirePrefixCalculator.getBladeRequirePrefix(new File("src/test/resources/")));
	}
	
	@Ignore //TODO: is this now a valid test case with the model?
	@Test
	public void testGetApplicationNamespaceWithApplicationsOfTheSameNameInDifferentLocations() throws Exception
	{
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app1")));
		assertEquals( "novox", RequirePrefixCalculator.getAppRequirePrefix(new File("src/test/resources/NamespaceAppStructure/" + APPLICATIONS_DIR + "/app1")));
	}
	
	@Test
	public void testGetApplicationNamespaceWithApplicationSrcFolderWhichContainsFolderAndFile() throws Exception
	{
		assertEquals( "caplinx", RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/app4")));
	}
	
	@Test(expected=NamespaceException.class)
	public void testGettingNamespaceWithInvalidConf() throws Exception
	{
		RequirePrefixCalculator.getAppRequirePrefix(new File(testBase + "/" + APPLICATIONS_DIR + "/no-namespace"));
	}
	
	
}
