package com.caplin.cutlass.bundler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;


import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class BundlerSourceFileProviderTest {

	private BladeRunnerSourceFileProvider sourceFileProvider;
	private BladeRunnerFileAppender mockFileAppender;
	private static final String testRoot = "src/test/resources/generic-bundler/bundler-source-file-provider";
	private InOrder inOrder;
	
	@Before
	public void setup() {
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testRoot)));
		
		mockFileAppender = mock(BladeRunnerFileAppender.class);
		sourceFileProvider = new BladeRunnerSourceFileProvider(mockFileAppender);
		inOrder = inOrder(mockFileAppender);
	}
	
	@After
	public void tearDown() {
		inOrder.verifyNoMoreInteractions();
		verifyNoMoreInteractions(mockFileAppender);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_AppAspectScope_NoTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-aspect");
		
		sourceFileProvider.getSourceFiles(baseDir, null);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendThirdPartyLibraryFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/another-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade2")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/another-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/another-bladeset/blades/blade2")), any(List.class));
		inOrder.verify(mockFileAppender).appendAppAspectFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-aspect")), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_BladesetScope_NoTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset");
		
		sourceFileProvider.getSourceFiles(baseDir, null);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_BladeScope_NoTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1");
		
		sourceFileProvider.getSourceFiles(baseDir, null);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_WorkbenchScope_NoTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench");
		
		sourceFileProvider.getSourceFiles(baseDir, null);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendThirdPartyLibraryFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendWorkbenchAspectFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/default-aspect")), any(List.class));
		inOrder.verify(mockFileAppender).appendWorkbenchFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_AppAspectScope_WithTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-aspect");
		File testDir = new File(baseDir+"/test/test-acceptance");
		
		sourceFileProvider.getSourceFiles(baseDir, testDir);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendThirdPartyLibraryFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/another-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade2")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/another-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/another-bladeset/blades/blade2")), any(List.class));
		inOrder.verify(mockFileAppender).appendAppAspectFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-aspect")), any(List.class));
		inOrder.verify(mockFileAppender).appendTestFiles(eq(testDir), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_BladesetScope_WithTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset");
		File testDir = new File(baseDir+"/test/test-acceptance");
		
		sourceFileProvider.getSourceFiles(baseDir, testDir);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendTestFiles(eq(testDir), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_BladeScope_WithTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1");
		File testDir = new File(baseDir+"/test/test-acceptance");
		
		sourceFileProvider.getSourceFiles(baseDir, testDir);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendTestFiles(eq(testDir), any(List.class));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_WorkbenchScope_WithTestDir_LooksInCorrectLocations() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench");
		File testDir = new File(baseDir+"/test/test-acceptance");
		
		sourceFileProvider.getSourceFiles(baseDir, testDir);
		
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + SDK_DIR + "/libs/javascript/caplin/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendLibrarySourceFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/libs/lib/src").getAbsoluteFile()), any(List.class));
		inOrder.verify(mockFileAppender).appendThirdPartyLibraryFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladesetFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset")), any(List.class));
		inOrder.verify(mockFileAppender).appendBladeFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1")), any(List.class));
		inOrder.verify(mockFileAppender).appendWorkbenchAspectFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/default-aspect")), any(List.class));
		inOrder.verify(mockFileAppender).appendWorkbenchFiles(eq(new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/workbench")), any(List.class));
		inOrder.verify(mockFileAppender).appendTestFiles(eq(testDir), any(List.class));
	}
	
	@Test
	public void testPassingInAnInvalidScopeReturnsEmptyList() throws Exception {
		File baseDir = new File(testRoot+"/" + APPLICATIONS_DIR + "/app1/a-bladeset/blade1/resources");
		
		assertEquals( 0, sourceFileProvider.getSourceFiles(baseDir, null).size());
		
	}
	
	@Test
	public void testPassingInANonExistantDirectoryReturnsEmptyList() throws Exception {
		File baseDir = new File(testRoot+"/i-dont-exist");
		
		assertEquals( 0, sourceFileProvider.getSourceFiles(baseDir, null).size());
	}
}
