package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.junit.Before;
import org.junit.Test;


public class FileModificationRegistryThreadSafetyTest
{

	private File fileInRoot;
	private File dirInRoot;
	private File fileInChildDir;
	private File testSdkDirectory;
	private FileModificationRegistry fileModificationRegistry;
	
	@Before
	public void setup() throws Exception {
		testSdkDirectory = BRJSTestModelFactory.createTestSdkDirectory();
		fileInRoot = new File(testSdkDirectory, "some-file.txt");
		dirInRoot = new File(testSdkDirectory, "some-dir");
		fileInChildDir = new File(dirInRoot, "nested-file.txt");
		fileModificationRegistry = new FileModificationRegistry(FalseFileFilter.INSTANCE, testSdkDirectory);
	}
	
	
	@Test
	public void theMapUnderlyingFileModificationRegistryIsThreadSafe() throws Throwable {
		int numberOfTestThreads = 100;
		
		List<ThreadSafetyTestThread> testThreads = new ArrayList<>();
		for (int i = 0; i < numberOfTestThreads; i++) {
			testThreads.add( new FileModificationServiceAccessorThread() );
			testThreads.add( new FileModificationServiceUpdateThread() );
		}
		
		for (ThreadSafetyTestThread t : testThreads) {
			t.start();
		}
		for (ThreadSafetyTestThread t : testThreads) {
			t.join();
		}
		
		for (ThreadSafetyTestThread t : testThreads) {
			if (t.thrownException != null) {
				throw t.thrownException;
			}
		}
	}
	
	
	abstract class ThreadSafetyTestThread extends Thread {
		public Throwable thrownException;
		static final int threadIterationLimit = 10000;
	}
	
	class FileModificationServiceUpdateThread extends ThreadSafetyTestThread {
		@Override
		public void run()
		{
			try {
				for (int i = 0; i < threadIterationLimit; i++) {
					fileModificationRegistry.incrementFileVersion(fileInChildDir);
					fileModificationRegistry.incrementChildFileVersions(fileInRoot);
				}
			} catch (Throwable t) {
				thrownException = t;
			}
		}
	}
	
	class FileModificationServiceAccessorThread extends ThreadSafetyTestThread {
		public Throwable thrownException;
		@Override
		public void run()
		{
			try {
				for (int i = 0; i < threadIterationLimit; i++) {
					fileModificationRegistry.getFileVersion(fileInChildDir);
					fileModificationRegistry.getFileVersion(dirInRoot);
					fileModificationRegistry.getFileVersion(fileInRoot);
				}
			} catch (Throwable t) {
				thrownException = t;
			}
		}
	}
	
}
