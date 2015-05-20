package org.bladerunnerjs.api.memoization;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.FileObserver;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.memoization.PollingFileModificationObserver;
import org.bladerunnerjs.memoization.WatchingFileModificationObserver;
import org.bladerunnerjs.spec.brjs.BRJSTest;
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
	private File brjsDir;
	private File secondaryTempFolder;
	
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
		brjsDir = brjs.dir().getUnderlyingFile();
	}
	
	@After
	public void tearDown() throws Exception {
		fileObserver.stop();
		if (secondaryTempFolder != null) FileUtils.deleteQuietly(secondaryTempFolder);
	}
	
	
	
	@Test
	public void fileVersionIsIncrementedForNewFilesInTheRootDir() throws Exception {
		fileObserver.start();
		File file = new File(brjsDir, "somefile.txt");
		long oldVersion = modificationRegistry.getFileVersion(file);
		file.createNewFile();
		assertVersionIncreased(oldVersion, file);
	}
	
	@Test
	public void fileVersionIsIncrementedForNewFilesInASubDirDir() throws Exception {
		File file = new File(brjsDir, "dir1/dir2/somefile.txt");
		file.getParentFile().mkdirs();
		fileObserver.start();
		long oldVersion = modificationRegistry.getFileVersion(file);
		file.createNewFile();
		assertVersionIncreased(oldVersion, file);
	}
	
	@Test
	public void fileVersionIsIncrementedForNewDirsInTheRootDir() throws Exception {
		File file = new File(brjsDir, "dir1");
		fileObserver.start();
		long oldVersion = modificationRegistry.getFileVersion(file);
		file.mkdir();
		assertVersionIncreased(oldVersion, file);
	}
	
	@Test
	public void fileVersionIsIncrementedForNewDirsInASubDir() throws Exception {
		File file = new File(brjsDir, "dir1/dir2/dir3");
		file.getParentFile().mkdirs();
		fileObserver.start();
		long oldVersion = modificationRegistry.getFileVersion(file);
		file.mkdir();
		assertVersionIncreased(oldVersion, file);
	}
	
	@Test
	public void fileVersionIsIncrementedForParentDirs() throws Exception {
		File dir1 = new File(brjsDir, "dir1");
		File dir2 = new File(dir1, "dir2");
		File file = new File(dir2, "someFile.txt");
		file.getParentFile().mkdirs();
		fileObserver.start();
		long oldFileVersion = modificationRegistry.getFileVersion(file);
		long oldDir1Version = modificationRegistry.getFileVersion(dir1);
		long oldDir2Version = modificationRegistry.getFileVersion(dir2);
		file.createNewFile();
		assertVersionIncreased(oldFileVersion, file);
		assertVersionIncreased(oldDir1Version, dir1);
		assertVersionIncreased(oldDir2Version, dir2);
	}
	
	@Test
	public void fileVersionIsIncrementedForParentDirsIncludingTheRoot() throws Exception {
		File dir1 = new File(brjsDir, "dir1");
		File file = new File(dir1, "someFile.txt");
		file.getParentFile().mkdirs();
		fileObserver.start();
		long oldFileVersion = modificationRegistry.getFileVersion(file);
		long oldDir1Version = modificationRegistry.getFileVersion(dir1);
		long oldDir2Version = modificationRegistry.getFileVersion(brjsDir);
		file.createNewFile();
		assertVersionIncreased(oldFileVersion, file);
		assertVersionIncreased(oldDir1Version, dir1);
		assertVersionIncreased(oldDir2Version, brjsDir);
	}
	
	@Test
	public void fileVersionIsIncrementedForDirsInASeperateAppsDirectory() throws Exception {
		brjs.close();
		secondaryTempFolder = org.bladerunnerjs.utility.FileUtils.createTemporaryDirectory(BRJSTest.class);
		File brjsAppsDir = new File(secondaryTempFolder, "brjs-apps");
		brjsAppsDir.mkdir();
		
		given(brjs).hasBeenCreatedWithWorkingDir(secondaryTempFolder);
		modificationRegistry = brjs.getFileModificationRegistry();
		fileObserver = fileObserverFactory.createObserver(brjs);
		brjsDir = brjs.dir().getUnderlyingFile();
		
		File dir1 = new File(brjsAppsDir, "dir1");
		File file = new File(dir1, "someFile.txt");
		file.getParentFile().mkdirs();
		fileObserver.start();
		long oldFileVersion = modificationRegistry.getFileVersion(file);
		long oldDir1Version = modificationRegistry.getFileVersion(dir1);
		long oldDir2Version = modificationRegistry.getFileVersion(brjsAppsDir);
		file.createNewFile();
		assertVersionIncreased(oldFileVersion, file);
		assertVersionIncreased(oldDir1Version, dir1);
		assertVersionIncreased(oldDir2Version, brjsAppsDir);
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
				if (i++ > 100) {
					throw ex;
				}
				Thread.sleep(100);
			}
		}
	}
	
	private static interface FileObserverFactory {
        public FileObserver createObserver(BRJS brjs);
    }
	
}
