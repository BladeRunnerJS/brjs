package org.bladerunnerjs.api.memoization;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.FileObserver;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.memoization.PollingFileModificationObserver;
import org.bladerunnerjs.memoization.WatchingFileModificationObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class FileObserverTest extends SpecTest
{
	
	private FileObserverFactory fileObserverFactory;
	private FileObserver fileObserver;
	private FileModificationRegistry modificationRegistry;
	
	@Parameters(name="{0}")
	public static Collection<Object[]> getParameters() {
		FileObserverFactory pollingObserverFactory = new FileObserverFactory()
		{
			public FileObserver createObserver(BRJS brjs)
			{
				return new PollingFileModificationObserver(brjs, 100);
			}
		};
		FileObserverFactory watchingObserverFactory = new FileObserverFactory()
		{
			public FileObserver createObserver(BRJS brjs)
			{
				return new WatchingFileModificationObserver(brjs);
			}
		};
		
		return Arrays.asList(new Object[][]{
				{"Polling", pollingObserverFactory},
				{"Watching", watchingObserverFactory}
			});
	}
	
	public FileObserverTest(String testName, FileObserverFactory fileObserverFactory) {
		this.fileObserverFactory = fileObserverFactory;
	}
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
		modificationRegistry = brjs.getFileModificationRegistry();
		fileObserver = fileObserverFactory.createObserver(brjs);
	}
	
	@After
	public void tearDown() throws Exception {
		fileObserver.stop();
	}
	
	
	
	@Test
	public void fileVersionIsIncrementedForChangesInTheRootDir() throws Exception {
		fileObserver.start();
		File file = brjs.file("somefile.txt").getUnderlyingFile();
		long oldVersion = modificationRegistry.getFileVersion(file);
		file.createNewFile();
		assertVersionIncreased(oldVersion, file);
	}
	
	
	private void assertVersionIncreased(long oldVersion, File file) throws Exception {
		long newVersion = -1;
		int i = 0;
		while (true) {
			try {
				newVersion = modificationRegistry.getFileVersion(file);
				assertTrue(oldVersion < newVersion);
				break;
			} catch (AssertionError ex) {
				if (i++ > 10) {
					throw ex;
				}
				Thread.sleep(1000);
			}
		}
	}
	
	private static interface FileObserverFactory {
        public FileObserver createObserver(BRJS brjs);
    }
	
}
