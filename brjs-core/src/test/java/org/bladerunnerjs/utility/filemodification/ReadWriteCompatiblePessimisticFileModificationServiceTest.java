package org.bladerunnerjs.utility.filemodification;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

public class ReadWriteCompatiblePessimisticFileModificationServiceTest extends TestModelAccessor {
	private File testDir;
	private File existentFile;
	private File nonExistentFile;
	private File existentDir;
	private File nonExistentDir;
	private ReadWriteCompatiblePessimisticFileModificationService fMS;
	
	@Before
	public void setUp() throws Exception {
		testDir = FileUtility.createTemporaryDirectory(PessimisticFileModificationInfoTest.class.getSimpleName());
		existentFile = new File(testDir, "existent-file");
		nonExistentFile = new File(testDir, "non-existent-file");
		existentDir = new File(testDir, "existent-dir");
		nonExistentDir = new File(testDir, "non-existent-dir");
		
		File brjsDir = new File(testDir, "brjs");
		new File(brjsDir, "sdk").mkdir();
		fMS = new ReadWriteCompatiblePessimisticFileModificationService();
		fMS.initialise(createModel(brjsDir), null);
		
		FileUtils.write(existentFile, "initial content");
		existentDir.mkdir();
	}
	
	@Test
	public void fileInfoObjectsBehaveAsNormalWhenOnlyFilesAreProvided() throws Exception {
		List<FileModificationInfo> modificationInfoSet = fMS.getModificationInfoSet(new File[] {existentFile, nonExistentFile});
		FileModificationInfo existentFileFMI = modificationInfoSet.get(0);
		FileModificationInfo nonExistentFileFMI = modificationInfoSet.get(1);
		
		assertEquals(1, existentFileFMI.getLastModified());
		assertEquals(1, existentFileFMI.getLastModified());
		
		assertEquals(0, nonExistentFileFMI.getLastModified());
		assertEquals(0, nonExistentFileFMI.getLastModified());
		
		FileUtils.write(existentFile, "content");
		
		assertEquals(2, existentFileFMI.getLastModified());
		assertEquals(0, nonExistentFileFMI.getLastModified());
	}
	
	@Test
	public void fileInfoObjectsBehaveAsNormalWhenOnlyDirectoriesAreProvided() {
		List<FileModificationInfo> modificationInfoSet = fMS.getModificationInfoSet(new File[] {existentDir, nonExistentDir});
		FileModificationInfo existentDirFMI = modificationInfoSet.get(0);
		FileModificationInfo nonExistentDirFMI = modificationInfoSet.get(1);
		
		assertEquals(1, existentDirFMI.getLastModified());
		assertEquals(2, existentDirFMI.getLastModified());
		
		assertEquals(0, nonExistentDirFMI.getLastModified());
		assertEquals(0, nonExistentDirFMI.getLastModified());
		
		nonExistentDir.mkdir();
		
		assertEquals(3, existentDirFMI.getLastModified());
		assertEquals(1, nonExistentDirFMI.getLastModified());
	}
	
	@Test
	public void directoryfileInfosStopBeingPessimisticWhenTheFirstItemIsAFile() throws Exception {
		List<FileModificationInfo> modificationInfoSet = fMS.getModificationInfoSet(new File[] {existentFile, existentDir, nonExistentFile});
		FileModificationInfo existentFileFMI = modificationInfoSet.get(0);
		FileModificationInfo existentDirFMI = modificationInfoSet.get(1);
		FileModificationInfo nonExistentFileFMI = modificationInfoSet.get(2);
		
		assertEquals(1, existentFileFMI.getLastModified());
		assertEquals(1, existentFileFMI.getLastModified());
		
		assertEquals(1, existentDirFMI.getLastModified());
		assertEquals(1, existentDirFMI.getLastModified());
		
		assertEquals(0, nonExistentFileFMI.getLastModified());
		assertEquals(0, nonExistentFileFMI.getLastModified());
		
		FileUtils.write(existentFile, "content");
		
		assertEquals(2, existentFileFMI.getLastModified());
		assertEquals(1, existentDirFMI.getLastModified());
		assertEquals(0, nonExistentFileFMI.getLastModified());
	}
	
	@Test
	public void directoryfileInfosStopBeingPessimisticEvenWhenTheFirstItemIsANonExistentFile() throws Exception {
		List<FileModificationInfo> modificationInfoSet = fMS.getModificationInfoSet(new File[] {nonExistentFile, existentDir});
		FileModificationInfo nonExistentFileFMI = modificationInfoSet.get(0);
		FileModificationInfo existentDirFMI = modificationInfoSet.get(1);
		
		assertEquals(0, nonExistentFileFMI.getLastModified());
		assertEquals(0, nonExistentFileFMI.getLastModified());
		
		assertEquals(1, existentDirFMI.getLastModified());
		assertEquals(1, existentDirFMI.getLastModified());
		
		FileUtils.write(nonExistentFile, "content");
		
		assertEquals(1, nonExistentFileFMI.getLastModified());
		assertEquals(1, existentDirFMI.getLastModified());
	}
}
