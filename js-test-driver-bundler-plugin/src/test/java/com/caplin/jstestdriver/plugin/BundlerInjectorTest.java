package com.caplin.jstestdriver.plugin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.bundler.css.CssBundler;
import com.caplin.cutlass.bundler.html.HtmlBundler;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.caplin.cutlass.bundler.js.JsBundler;
import com.caplin.cutlass.bundler.xml.XmlBundler;
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
		List<BundlerHandler> resourceBundleHandlers = new ArrayList<BundlerHandler>();	
		
		jsBundleHandler = createMockBundlerHandler("js.bundle");
		cssBundleHandler = createMockBundlerHandler("css.bundle");
		xmlBundleHandler = createMockBundlerHandler("xml.bundle");
		htmlBundleHandler = createMockBundlerHandler("html.bundle");
		
		resourceBundleHandlers.add(jsBundleHandler);
		resourceBundleHandlers.add(cssBundleHandler);
		resourceBundleHandlers.add(xmlBundleHandler);
		resourceBundleHandlers.add(htmlBundleHandler);
		
		bundlerInjector.setBundlerHandlers(resourceBundleHandlers);
		
		testFiles = new ArrayList<FileInfo>();
		addFileInfoToList(testFiles, "a/b/c/a-non-bundle-file.js");
		addFileInfoToList(testFiles, "a/b/c/123_js.bundle");
		addFileInfoToList(testFiles, "a/b/c/123_css.bundle");
		addFileInfoToList(testFiles, "a/b/c/123_xml.bundle");
		addFileInfoToList(testFiles, "a/b/c/123_html.bundle");
		addFileInfoToList(testFiles, "a/b/c/another-non-bundle-file.js");
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
		
		verify(jsBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(cssBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(xmlBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(htmlBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
	}
	
	@Test
	public void testBundlerHandlerIsCalledWithExtensionContainingUnderscore() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = createMockBundlerHandler("js.bundle");
		bundlerInjector.setBundlerHandlers(Arrays.asList(bundlerHandler));

		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file1.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file2.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/src_js.bundle");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file3.js");
		
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(bundlerHandler).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}

	@Test
	public void testBundlerHandlerIsCalledWithExtensionNotContainingUnderscore() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = createMockBundlerHandler("js.bundle");
		bundlerInjector.setBundlerHandlers(Arrays.asList(bundlerHandler));

		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file1.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file2.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/js.bundle");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file3.js");
		
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(bundlerHandler).getBundledFiles(any(File.class), any(File.class), any(File.class));
	}
	
	@Test
	public void testBundlerHandlerIsNotCalledIfExtensionDoesntContainUnderscoreAndIsntAnAbsoluteMatch() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = createMockBundlerHandler("js.bundle");
		bundlerInjector.setBundlerHandlers(Arrays.asList(bundlerHandler));

		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file1.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file2.js");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/tests/test-unit/xjs.bundle");
		addFileInfoToList(inputFiles, "a/path/novotrader/sdk/apps/app1/bladeset/src/file3.js");
		
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler, atLeastOnce()).getAcceptedFileSuffix();
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
		
		verify(jsBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(cssBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(xmlBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(htmlBundleHandler, atLeastOnce()).getAcceptedFileSuffix();
		
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
	
	@Test
	public void testBundlerInjectorHasCorrectBundleHandlers() throws Exception {
		BundlerInjector bundlerInjector = new BundlerInjector();
		
		assertEquals(5, bundlerInjector.bundlerHandlers.size());
		assertEquals(new JsBundler().getClass(), bundlerInjector.bundlerHandlers.get(0).getBundler().getClass());
		assertEquals(new CssBundler().getClass(), bundlerInjector.bundlerHandlers.get(1).getBundler().getClass());
		assertEquals(new I18nBundler().getClass(), bundlerInjector.bundlerHandlers.get(2).getBundler().getClass());
		assertEquals(new XmlBundler().getClass(), bundlerInjector.bundlerHandlers.get(3).getBundler().getClass());
		assertEquals(new HtmlBundler().getClass(), bundlerInjector.bundlerHandlers.get(4).getBundler().getClass());
	}
	
	private BundlerHandler createMockBundlerHandler(String extension) {
		BundlerHandler mockBundlerHandler = mock(BundlerHandler.class);
		when(mockBundlerHandler.getAcceptedFileSuffix()).thenReturn(extension);
		return mockBundlerHandler;
	}
	
	private void addFileInfoToList(List<FileInfo> theList, String fileName) {
		theList.add(new FileInfo(fileName, -1, -1, false, false, null, fileName));
	}
	
	private void checkBundlerHandlerGetsCalledWithCorrectBaseAndTestPaths(String bundleFilename, String expectedBasePath, String expectedTestPath, String bundlerSuffix) throws Exception
	{
		BundlerInjector bundlerInjector = new BundlerInjector();
		BundlerHandler bundlerHandler = createMockBundlerHandler(bundlerSuffix);
		bundlerInjector.setBundlerHandlers(Arrays.asList(bundlerHandler));
		
		List<FileInfo> inputFiles = new ArrayList<FileInfo>();
		addFileInfoToList(inputFiles, bundleFilename);
		bundlerInjector.processDependencies(inputFiles);
		
		verify(bundlerHandler, atLeastOnce()).getAcceptedFileSuffix();
		verify(bundlerHandler).getBundledFiles(new File(expectedBasePath), new File(expectedTestPath), new File(bundleFilename));
	}	
	
}
