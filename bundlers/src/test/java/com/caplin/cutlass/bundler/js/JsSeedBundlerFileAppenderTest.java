package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;


public class JsSeedBundlerFileAppenderTest {

	private final String testBase = "src/test/resources/js-bundler/source-file-finder";
	private JsSeedBundlerFileAppender fileAppender;
	
	@Before
	public void setup() {
		fileAppender = new JsSeedBundlerFileAppender();
	}
	
	@Test
	public void testAppendingdSdkFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(new File[0]);
		
		List<File> foundFiles = new ArrayList<File>();
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(testBase)));
		fileAppender.appendLibraryResourceFiles(BRJSAccessor.root.sdkLib().assetLocation("resources").dir(), foundFiles);
		
		assertEquals( expectedFiles, foundFiles );
	}

	@Test
	public void testAppendingAppAspectFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-aspect/index.html"),
				new File(testBase, "apps/test-app1/a-aspect/index.htm"), 
				new File(testBase, "apps/test-app1/a-aspect/index.jsp"), 
				new File(testBase, "apps/test-app1/a-aspect/resources/xml"),
				new File(testBase, "apps/test-app1/a-aspect/resources/html")
		);
		
		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendAppAspectFiles(new File(testBase, "apps/test-app1/a-aspect"), foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}
	
	@Test
	public void testAppendingDefaultAspectFilesWithWebcentric() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/default-aspect/index.html"),
				new File(testBase, "apps/test-app1/default-aspect/index.htm"), 
				new File(testBase, "apps/test-app1/default-aspect/index.jsp"), 
				new File(testBase, "apps/test-app1/default-aspect/resources/xml"),
				new File(testBase, "apps/test-app1/default-aspect/resources/html"),
				new File(testBase, "apps/test-app1/default-aspect/webcentric")
		);
		
		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendAppAspectFiles(new File(testBase, "apps/test-app1/default-aspect"), foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}

	@Test
	public void testAppendingBladesetFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/resources/xml"),
				new File(testBase, "apps/test-app1/a-bladeset/resources/html")
		);
		
		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendBladesetFiles(new File(testBase, "apps/test-app1/a-bladeset"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}

	@Test
	public void testAppendingBladeFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/resources/xml"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/resources/html")
		);
		
		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendBladeFiles(new File(testBase, "apps/test-app1/a-bladeset/blades/blade1"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}
	
	@Test
	public void testAppendingWorkbenchFiles() throws Exception {
		List<File> expectedFiles = Arrays.asList(
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench/index.html"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench/index.htm"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench/index.jsp"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench/resources/xml"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/workbench/resources/html")
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
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/src-test"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/tests"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/resources/xml"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-unit/js-test-driver/resources/html")
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
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/src-test"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/tests"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/resources/xml"),
				new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver/resources/html")
		);
		
		List<File> foundFiles = new ArrayList<File>();
		fileAppender.appendTestFiles(new File(testBase, "apps/test-app1/a-bladeset/blades/blade1/tests/test-acceptance/js-test-driver"),foundFiles );
		
		assertEquals( expectedFiles, foundFiles );
	}
	
}
