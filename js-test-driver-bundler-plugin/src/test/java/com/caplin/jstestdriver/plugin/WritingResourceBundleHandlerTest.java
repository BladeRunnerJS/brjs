package com.caplin.jstestdriver.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import com.caplin.cutlass.util.FileUtility;

public class WritingResourceBundleHandlerTest {

	private LegacyFileBundlerPlugin mockBundler;
	private File tempDir;
	private File bundleParentDir;
	
	@Before
	public void setup() throws IOException {
		mockBundler = mock(LegacyFileBundlerPlugin.class);
		
		tempDir = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		bundleParentDir = new File(tempDir, "a/b/c");
	}
	
	@After
	public void tearDown()
	{
		Collection<File> allTempFiles = FileUtils.listFilesAndDirs(tempDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		
		for (File tempFile : allTempFiles)
		{
			tempFile.setWritable(true);
			tempFile.setReadable(true);
			tempFile.setExecutable(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHandlerPassesCorrectPathToBundler() throws Exception {
		WritingResourceBundlerHandler servletHandler = new WritingResourceBundlerHandler(mockBundler, "_bundle.extension", true);
		
		servletHandler.getBundledFiles(bundleParentDir, bundleParentDir, new File(bundleParentDir, "file_bundle.extension"));
		
		verify(mockBundler).getBundleFiles(bundleParentDir, bundleParentDir, "/file_bundle.extension");
		verify(mockBundler).writeBundle( any(List.class), any(OutputStream.class));
		verifyNoMoreInteractions(mockBundler);
	}
	
	@Test
	public void testHandlerThrowsExceptionIfParentFilesCannotBeCreated() throws Exception {
		WritingResourceBundlerHandler servletHandler = new WritingResourceBundlerHandler(mockBundler, "_bundle.extension", true);

		new File(tempDir, "a").mkdir();
		new File(tempDir, "a").setWritable(false);
		new File(tempDir, "a").setReadable(false);
		new File(tempDir, "a").setExecutable(false);
		
		try {
			servletHandler.getBundledFiles(bundleParentDir, bundleParentDir, new File(bundleParentDir, "file_bundle.extension"));
			// setWriteable(false) doesnt work on Windows - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6728842
			// if we are on windows ignore that an exception is thrown
			if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
				fail("Exception expected");
			} else {
				//ignore the fact there is no exception thrown
			}
		} catch (RuntimeException ex) {
			verifyNoMoreInteractions(mockBundler);
			assertTrue(ex.toString().contains("Unable to create parent directory"));
		}
	}
	
	@Test 
	public void testHandlerThrowsExceptionIfBundleFileCannotBeCreated() throws Exception {
		WritingResourceBundlerHandler servletHandler = new WritingResourceBundlerHandler(mockBundler, "_bundle.extension", true);		
		
		bundleParentDir.mkdirs();
		bundleParentDir.setWritable(false);
		bundleParentDir.setReadable(false);
		
		try {
			servletHandler.getBundledFiles(bundleParentDir, bundleParentDir, new File(bundleParentDir, "file_bundle.extension"));
			// setWriteable(false) doesnt work on Windows - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6728842
			// if we are on windows ignore that an exception is thrown
			if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
				fail("Exception expected");
			} else {
				//ignore the fact there is no exception thrown
			}
		} catch (RuntimeException ex) {
			verifyNoMoreInteractions(mockBundler);
			assertTrue(ex.toString().contains("Unable to create or write to file"));
		}
	}
	
	@Test 
	public void testHandlerThrowsExceptionIfBundleFileCannotBeWrittenTo() throws Exception {
		WritingResourceBundlerHandler servletHandler = new WritingResourceBundlerHandler(mockBundler, "_bundle.extension", true);		
		
		bundleParentDir.mkdirs();
		new File(bundleParentDir, "file_bundle.extension").createNewFile();
		new File(bundleParentDir, "file_bundle.extension").setWritable(false);
		new File(bundleParentDir, "file_bundle.extension").getParentFile().setWritable(false);
		
		
		try {
			servletHandler.getBundledFiles(bundleParentDir, bundleParentDir, new File(bundleParentDir, "file_bundle.extension"));
			// setWriteable(false) doesnt work on Windows - see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6728842
			// if we are on windows ignore that an exception is thrown
			if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1) {
				fail("Exception expected");
			} else {
				//ignore the fact there is no exception thrown
			}
		} catch (RuntimeException ex) {
			verifyNoMoreInteractions(mockBundler);
			assertTrue(ex.toString().contains("Unable to create or write to file"));
		}			
	}
	
	@Test
	public void testHandlerReturnsCorrectFileSuffix() throws Exception {
		WritingResourceBundlerHandler servletHandler = new WritingResourceBundlerHandler(mockBundler, "_bundle.extension", true);
		assertEquals("_bundle.extension", servletHandler.getAcceptedFileSuffix());
	}
	
	@Test
	public void testHandlerReturnsCorrectBundler() throws Exception {
		WritingResourceBundlerHandler servletHandler = new WritingResourceBundlerHandler(mockBundler, "_bundle.extension", true);
		assertSame(mockBundler, servletHandler.getBundler());
	}
	
}
