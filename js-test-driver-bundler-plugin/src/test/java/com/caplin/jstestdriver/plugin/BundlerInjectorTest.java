package com.caplin.jstestdriver.plugin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;
import org.bladerunnerjs.testing.specutility.engine.ConsoleMessageStore;
import org.bladerunnerjs.testing.specutility.engine.ConsoleStoreWriter;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;
import com.google.jstestdriver.FileInfo;

public class BundlerInjectorTest {

	private BundlerHandler jsBundleHandler;
	private BundlerHandler cssBundleHandler;
	private BundlerHandler xmlBundleHandler;
	private BundlerHandler htmlBundleHandler;
	private BundlerInjector bundlerInjector;
	private List<FileInfo> testFiles;
	
	@Before
	public void setup() throws Exception {
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(".")));
		
		bundlerInjector = new BundlerInjector();
		Map<String,BundlerHandler> resourceBundleHandlers = new HashMap<String,BundlerHandler>();	
		
		jsBundleHandler = mock(BundlerHandler.class);
		cssBundleHandler = mock(BundlerHandler.class);
		xmlBundleHandler = mock(BundlerHandler.class);
		htmlBundleHandler = mock(BundlerHandler.class);
		
		resourceBundleHandlers.put("js.bundle", jsBundleHandler);
		resourceBundleHandlers.put("css.bundle", cssBundleHandler);
		resourceBundleHandlers.put("xml.bundle", xmlBundleHandler);
		resourceBundleHandlers.put("html.bundle", htmlBundleHandler);
		
		bundlerInjector.setBundlerHandlers(resourceBundleHandlers);
		
		testFiles = new ArrayList<FileInfo>();
		addFileInfoToList(testFiles, "a/b/c/a-non-bundle-file.js");
		addFileInfoToList(testFiles, "a/b/c/123_js.bundle");
		addFileInfoToList(testFiles, "a/b/c/123_css.bundle");
		addFileInfoToList(testFiles, "a/b/c/123_xml.bundle");
		addFileInfoToList(testFiles, "a/b/c/123_html.bundle");
		addFileInfoToList(testFiles, "a/b/c/another-non-bundle-file.js");
		
		BRJSThreadSafeModelAccessor.destroy();
	}
	
	@After
	public void tearDown() {
		verifyNoMoreInteractions(jsBundleHandler);
		verifyNoMoreInteractions(cssBundleHandler);
		verifyNoMoreInteractions(xmlBundleHandler);
		verifyNoMoreInteractions(htmlBundleHandler);
	}
	
	@Ignore  //TODO: FIXME
	@Test
	public void testCorrectBasePathAndTestPathIsPassedToBundlerHandler() throws Exception {
		//checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths( <BUNDLE.FILE>, 						<BASE.DIR>, <TEST.DIR>,		<BUNDLE.EXTENSION>);
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"a/b/c/d/src_js.bundle", 			"a/b", 		"a/b/c/d",		"js.bundle");
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"a/b/c/d/../src_js.bundle", 		"a/b/c", 		"a/b/c/d/..",	"js.bundle");
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"/a/b/c/d/src_js.bundle", 			"/a/b", 		"/a/b/c/d",		"js.bundle");
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"/a/b/c/d/../src_js.bundle", 		"/a/b/c", 	"/a/b/c/d/..",	"js.bundle");
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"/a/b/c/aTheme_ie6_css.bundle", 	"/a", 		"/a/b/c",		"css.bundle");
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"/a/b/c/aTheme_en_US_css.bundle", 	"/a", 		"/a/b/c",		"css.bundle");
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"1/2/3/a/b/c/abc_123-_0_js.bundle", "1/2/3/a", 	"1/2/3/a/b/c/",	"js.bundle");
		
		checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(	"a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/src_js.bundle", 			
																	"a/path/novotrader/sdk/apps/app1/bladeset/", 		
																	"a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/",		
																	"js.bundle");
	}
	
	@Test
	public void testBundlerHandlerIsNotUsedIfNoBundleFilePresent() throws Exception {
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "src/file1.js");
		addFileInfoToList(inputFiles, "src/file2.js");
		addFileInfoToList(inputFiles, "src/file3.js");
		
		List<FileInfo> processedInputFiles = bundlerInjector.processDependencies(inputFiles);
		
		assertEquals(3, processedInputFiles.size());
		assertEquals("src/file1.js", processedInputFiles.get(0).getFilePath());
		assertEquals("src/file2.js", processedInputFiles.get(1).getFilePath());
		assertEquals("src/file3.js", processedInputFiles.get(2).getFilePath());
	}
	
	@Test
	public void testBundlerHandlerIsCalledWithExtensionContainingUnderscore() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = mock(BundlerHandler.class);
		
		Map<String,BundlerHandler> handlers = new HashMap<String,BundlerHandler>();
		handlers.put("js.bundle", bundlerHandler);
		bundlerInjector.setBundlerHandlers(handlers);

		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file1.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file2.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/bundles/src_js.bundle");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file3.js");
		
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}

	@Test
	public void testBundlerHandlerIsCalledWithExtensionNotContainingUnderscore() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = mock(BundlerHandler.class);
		
		Map<String,BundlerHandler> handlers = new HashMap<String,BundlerHandler>();
		handlers.put("js.bundle", bundlerHandler);
		bundlerInjector.setBundlerHandlers(handlers);

		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file1.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file2.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/bundles/js.bundle");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file3.js");
		
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}
	
	@Test
	public void testBundlerHandlerIsNotCalledIfExtensionDoesntContainUnderscoreAndIsntAnAbsoluteMatch() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = mock(BundlerHandler.class);
		
		Map<String,BundlerHandler> handlers = new HashMap<String,BundlerHandler>();
		handlers.put("js.bundle", bundlerHandler);
		bundlerInjector.setBundlerHandlers(handlers);

		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file1.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file2.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/xjs.bundle");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file3.js");
		
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler, never()).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}
	
	@Test
	public void testTestFilesAreUnchanged() throws Exception {
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "src/file1.js");
		addFileInfoToList(inputFiles, "src/file2.js");
		addFileInfoToList(inputFiles, "src/file_js.bundle");
		addFileInfoToList(inputFiles, "src/file3.js");
		
		List<FileInfo> processedInputFiles = bundlerInjector.processTests(inputFiles);
		
		assertEquals(4, processedInputFiles.size());
		assertEquals("src/file1.js", processedInputFiles.get(0).getFilePath());
		assertEquals("src/file2.js", processedInputFiles.get(1).getFilePath());
		assertEquals("src/file_js.bundle", processedInputFiles.get(2).getFilePath());
		assertEquals("src/file3.js", processedInputFiles.get(3).getFilePath());
	}
	
	@Test
	public void testPluginFilesAreUnchanged() throws Exception {
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "src/file1.js");
		addFileInfoToList(inputFiles, "src/file2.js");
		addFileInfoToList(inputFiles, "src/file_js.bundle");
		addFileInfoToList(inputFiles, "src/file3.js");
		
		List<FileInfo> processedInputFiles = bundlerInjector.processPlugins(inputFiles);
		
		assertEquals(4, processedInputFiles.size());
		assertEquals("src/file1.js", processedInputFiles.get(0).getFilePath());
		assertEquals("src/file2.js", processedInputFiles.get(1).getFilePath());
		assertEquals("src/file_js.bundle", processedInputFiles.get(2).getFilePath());
		assertEquals("src/file3.js", processedInputFiles.get(3).getFilePath());
	}
	
	@Ignore  //TODO: FIXME
	@Test
	public void testProcessDependenciesIsCalledOnAllBundlerHandlers() throws Exception {
		bundlerInjector.processDependencies(testFiles);
		
		verify(jsBundleHandler, times(1)).getBundledFiles(new File("a"), new File("a/b/c/"), new File("a/b/c/123_js.bundle"));
		verify(cssBundleHandler, times(1)).getBundledFiles(new File("a"),new File("a/b/c/"),  new File("a/b/c/123_css.bundle"));
		verify(xmlBundleHandler, times(1)).getBundledFiles(new File("a"), new File("a/b/c/"), new File("a/b/c/123_xml.bundle"));
		verify(htmlBundleHandler, times(1)).getBundledFiles(new File("a"), new File("a/b/c/"), new File("a/b/c/123_html.bundle"));	
	}
	
	@Test
	public void testProcessPluginsIsNotCalledOnHandlers() throws Exception {
		bundlerInjector.processPlugins(testFiles);
		verify(jsBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
		verify(cssBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
		verify(xmlBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
		verify(htmlBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}
	
	@Test
	public void testProcessTestsIsNotCalledOnHandlers() throws Exception {
		bundlerInjector.processTests(testFiles);
		verify(jsBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
		verify(cssBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
		verify(xmlBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
		verify(htmlBundleHandler, times(0)).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}
	
	@Test //TODO: tidy up this test - in reality we'll probably get rid of all these tests anyway since they'll all be obsolete when everything using the model
	public void jsBundleRequestArePassedToTheNewModel() throws Exception {	
		LogMessageStore logging = new LogMessageStore();
		ConsoleMessageStore output = new ConsoleMessageStore();
		File testSdkDirectory = createTestSdkDirectory();
		BRJS brjs = new BRJS(testSdkDirectory, new BRJSPluginLocator(), new Java7FileModificationService(), new TestLoggerFactory(logging), new ConsoleStoreWriter(output));
		
		BRJSThreadSafeModelAccessor.initializeModel(brjs);
		
		App app = brjs.app("my-app");
		app.create();
		
		Bladeset bladeset = app.bladeset("bs1");
		Blade blade = bladeset.blade("b1");
		File bladeSrcFile = blade.file("src/srcFile.js");
		bladeSrcFile.getParentFile().mkdirs();
		bladeSrcFile.createNewFile();
		org.apache.commons.io.FileUtils.write(bladeSrcFile, "// some blade src code");
		
		TestPack testPack = blade.testType("unit").testTech("techy");
		testPack.create();
		File jsBundleFile = testPack.file("bundles/js.bundle");
		
		testPack.tests().create();
		File testFile = testPack.tests().file("test1.js"); 
		testFile.createNewFile();
		
		org.apache.commons.io.FileUtils.write(testFile, "require('appns/bs1/b1/srcFile');");
		
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, jsBundleFile.getAbsolutePath());
		new BundlerInjector().processDependencies(inputFiles);
		
		String bundleFileContents = org.apache.commons.io.FileUtils.readFileToString(jsBundleFile);
		assertTrue(bundleFileContents.contains("// some blade src code"));		
	}
	
	
	@Test //TODO: tidy up this test - in reality we'll probably get rid of all these tests anyway since they'll all be obsolete when everything using the model
	public void jsBundleRequestsUsingTheNewModelWorkWithSdkLibs() throws Exception {	
		LogMessageStore logging = new LogMessageStore();
		ConsoleMessageStore output = new ConsoleMessageStore();
		File testSdkDirectory = createTestSdkDirectory();
		BRJS brjs = new BRJS(testSdkDirectory, new BRJSPluginLocator(), new Java7FileModificationService(), new TestLoggerFactory(logging), new ConsoleStoreWriter(output));
		
		BRJSThreadSafeModelAccessor.initializeModel(brjs);
		
		JsLib sdkLib = brjs.sdkLib("br");
		File sdkLibSrcFile = sdkLib.file("src/srcFile.js");
		sdkLibSrcFile.getParentFile().mkdirs();
		sdkLibSrcFile.createNewFile();
		org.apache.commons.io.FileUtils.write(sdkLibSrcFile, "// some SDK src code");
		
		TestPack testPack = sdkLib.testType("unit").testTech("techy");
		testPack.create();
		
		File jsBundleFile = testPack.file("bundles/js.bundle");

		testPack.tests().create();
		File testFile = testPack.tests().file("test1.js"); 
		testFile.createNewFile();
		org.apache.commons.io.FileUtils.write(testFile, "require('br/srcFile');");
		
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, jsBundleFile.getAbsolutePath());
		new BundlerInjector().processDependencies(inputFiles);
		
		String bundleFileContents = org.apache.commons.io.FileUtils.readFileToString(jsBundleFile);
		assertTrue(bundleFileContents.contains("// some SDK src code"));		
	}
	
	
	
	private File createTestSdkDirectory() {
		File sdkDir;
		
		try {
			sdkDir = FileUtility.createTemporaryDirectory("test");
			new File(sdkDir, "sdk").mkdirs();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sdkDir;
	}
	
	private void addFileInfoToList(List<FileInfo> theList, String fileName) {
		theList.add(new FileInfo(fileName, -1, -1, false, false, null, fileName));
	}
	
	private void checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(String bundleFilename, String expectedBasePath, String expectedTestPath, String bundlerSuffix) throws Exception
	{
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = mock(BundlerHandler.class);
		
		Map<String,BundlerHandler> handlers = new HashMap<String,BundlerHandler>();
		handlers.put("js.bundle", bundlerHandler);
		bundlerInjector.setBundlerHandlers(handlers);
		
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, bundleFilename);
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler).getBundledFiles(new File(expectedBasePath), new File(expectedTestPath), new File(bundleFilename));
	}	
	
}
