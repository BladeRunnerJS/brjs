package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.sinbin.CutlassConfig;

public class JsSourceBundlerFileAppenderTest {

	private final String testBase = "src/test/resources/js-bundler/source-file-finder";
	private JsSourceBundlerFileAppender fileAppender;
	
	@Before
	public void setup() {
		fileAppender = new JsSourceBundlerFileAppender();
	}
	
	@Test
	public void testAppendingdSdkFiles() throws Exception {
		File sourceDir = new File(testBase, CutlassConfig.SDK_DIR + "/libs/javascript/caplin/src");
		List<File> expectedFiles = Arrays.asList(sourceDir);
		List<File> foundFiles = new ArrayList<File>();
		
		fileAppender.appendLibrarySourceFiles(sourceDir, foundFiles);
		assertEquals(expectedFiles, foundFiles);
	}

	@Test
	public void testAppendingAppAspectFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-aspect/src")
		);

		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendAppAspectFiles(new File(testBase, "apps/test-app1/a-aspect"), foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}

	@Test
	public void testAppendingBladesetFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/src")
		);

		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendBladesetFiles(new File(testBase, "apps/test-app1/a-bladeset"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}

	@Test
	public void testAppendingBladeFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/src")
		);

		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendBladeFiles(new File(testBase, "apps/test-app1/a-bladeset/blades/blade1"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}
	
	@Test
	public void testAppendingWorkbenchFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench/src")
		);

		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendWorkbenchFiles(new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}

	@Test
	public void appendTestUnitFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/tests/src-test"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/src-test"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/src-test")
		);

		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendTestFiles(new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}
	
	@Test
	public void appendTestAcceptanceFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/tests/src-test"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/src-test"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/src-test")
		);

		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendTestFiles(new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}
	
}
