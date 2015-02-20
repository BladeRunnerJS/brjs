package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class FileModificationRegistryThreadSafetyTest extends SpecTest
{

	private File fileInRoot;
	private File dirInRoot;
	private File fileInChildDir;
	
	@Before
	public void setup() throws Exception {
		given(brjs).hasBeenCreated();
		fileInRoot = new File(testSdkDirectory, "some-file.txt");
		dirInRoot = new File(testSdkDirectory, "some-dir");
		fileInChildDir = new File(dirInRoot, "nested-file.txt");
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
					brjs.getFileModificationRegistry().incrementFileVersion(fileInChildDir);
					brjs.getFileModificationRegistry().incrementChildFileVersions(fileInRoot);
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
					brjs.getFileModificationRegistry().getFileVersion(fileInChildDir);
					brjs.getFileModificationRegistry().getFileVersion(dirInRoot);
					brjs.getFileModificationRegistry().getFileVersion(fileInRoot);
				}
			} catch (Throwable t) {
				thrownException = t;
			}
		}
	}
	
}
