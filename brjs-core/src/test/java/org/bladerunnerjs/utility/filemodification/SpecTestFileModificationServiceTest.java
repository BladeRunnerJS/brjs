package org.bladerunnerjs.utility.filemodification;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

public class SpecTestFileModificationServiceTest extends TestModelAccessor {
	private File testDir;
	private File existentFile;
	private File nonExistentFile;
	private File existentDir;
	private File nonExistentDir;
	private SpecTestFileModificationService fMS;
	
	@Before
	public void setUp() throws Exception {
		testDir = FileUtility.createTemporaryDirectory( this.getClass() );
		existentFile = new File(testDir, "existent-file");
		nonExistentFile = new File(testDir, "non-existent-file");
		existentDir = new File(testDir, "existent-dir");
		nonExistentDir = new File(testDir, "non-existent-dir");
		
		File brjsDir = new File(testDir, "brjs");
		new File(brjsDir, "sdk").mkdir();
		fMS = new SpecTestFileModificationService();
		createModel(brjsDir, fMS);
		
		FileUtils.write(existentFile, "initial content");
		existentDir.mkdir();
	}
	
	@Test
	public void fileInfoObjectsBehaveAsNormalWhenOnlyFilesAreProvided() throws Exception {
		List<FileModificationInfo> modificationInfoSet = getModificationInfoSet(fMS, new File[] {existentFile, nonExistentFile});
		FileModificationInfo existentFileFMI = modificationInfoSet.get(0);
		FileModificationInfo nonExistentFileFMI = modificationInfoSet.get(1);
		
		long existentFileInitialLastModified = existentFileFMI.getLastModified();
		assertEquals(existentFileInitialLastModified, existentFileFMI.getLastModified());
		
		long nonExistentFileInitialLastModified = nonExistentFileFMI.getLastModified();
		assertEquals(nonExistentFileInitialLastModified, nonExistentFileFMI.getLastModified());
		
		FileUtils.write(existentFile, "content");
		
		assertNotEquals(existentFileInitialLastModified, existentFileFMI.getLastModified());
		assertEquals(nonExistentFileInitialLastModified, nonExistentFileFMI.getLastModified());
	}
	
	@Test
	public void fileInfoObjectsBehaveAsNormalWhenOnlyDirectoriesAreProvided() {
		List<FileModificationInfo> modificationInfoSet = getModificationInfoSet(fMS, new File[] {existentDir, nonExistentDir});
		FileModificationInfo existentDirFMI = modificationInfoSet.get(0);
		FileModificationInfo nonExistentDirFMI = modificationInfoSet.get(1);
		
		long existentDirInitialLastModified = existentDirFMI.getLastModified();
		long existentDirSubsequentLastModified = existentDirFMI.getLastModified();
		assertNotEquals(existentDirInitialLastModified, existentDirSubsequentLastModified);
		
		long nonExistentDirInitialLastModified = nonExistentDirFMI.getLastModified();
		assertEquals(nonExistentDirInitialLastModified, nonExistentDirFMI.getLastModified());
		
		nonExistentDir.mkdir();
		
		assertNotEquals(existentDirSubsequentLastModified, existentDirFMI.getLastModified());
		assertNotEquals(nonExistentDirInitialLastModified, nonExistentDirFMI.getLastModified());
	}
	
	@Test
	public void directoryfileInfosStopBeingPessimisticWhenTheFirstItemIsAFile() throws Exception {
		List<FileModificationInfo> modificationInfoSet = getModificationInfoSet(fMS, new File[] {existentFile, existentDir, nonExistentFile});
		FileModificationInfo existentFileFMI = modificationInfoSet.get(0);
		FileModificationInfo existentDirFMI = modificationInfoSet.get(1);
		FileModificationInfo nonExistentFileFMI = modificationInfoSet.get(2);
		
		long existentFileInitialLastModified = existentFileFMI.getLastModified();
		assertEquals(existentFileInitialLastModified, existentFileFMI.getLastModified());
		
		long existentDirInitialLastModified = existentDirFMI.getLastModified();
		assertEquals(existentDirInitialLastModified, existentDirFMI.getLastModified());
		
		long nonExistentFileInitialLastModified = nonExistentFileFMI.getLastModified();
		assertEquals(nonExistentFileInitialLastModified, nonExistentFileFMI.getLastModified());
		
		FileUtils.write(existentFile, "content");
		
		assertNotEquals(existentFileInitialLastModified, existentFileFMI.getLastModified());
		assertEquals(existentDirInitialLastModified, existentDirFMI.getLastModified());
		assertEquals(nonExistentFileInitialLastModified, nonExistentFileFMI.getLastModified());
	}
	
	@Test
	public void directoryfileInfosStopBeingPessimisticEvenWhenTheFirstItemIsANonExistentFile() throws Exception {
		List<FileModificationInfo> modificationInfoSet = getModificationInfoSet(fMS, new File[] {nonExistentFile, existentDir});
		FileModificationInfo nonExistentFileFMI = modificationInfoSet.get(0);
		FileModificationInfo existentDirFMI = modificationInfoSet.get(1);
		
		long nonExistentFileInitialLastModified = nonExistentFileFMI.getLastModified();
		assertEquals(nonExistentFileInitialLastModified, nonExistentFileFMI.getLastModified());
		
		long existentDirInitialLastModified = existentDirFMI.getLastModified();
		assertEquals(existentDirInitialLastModified, existentDirFMI.getLastModified());
		
		FileUtils.write(nonExistentFile, "content");
		
		assertNotEquals(nonExistentFileInitialLastModified, nonExistentFileFMI.getLastModified());
		assertEquals(existentDirInitialLastModified, existentDirFMI.getLastModified());
	}
	
	// Note: this method was formerly part of the interface, but is now only used by the unit tests for this class
	private List<FileModificationInfo> getModificationInfoSet(SpecTestFileModificationService fMS, File[] files) {
		List<FileModificationInfo> modificationInfoSet = new ArrayList<>();
		PrimaryFileModificationInfo primaryFileModificationInfo = null;
		File primarySetFile = null;
		
		for(File file : files) {
			if(primarySetFile == null) {
				primarySetFile = file;
				primaryFileModificationInfo = (PrimaryFileModificationInfo) fMS.getFileSetModificationInfo(file, primarySetFile);
				modificationInfoSet.add(primaryFileModificationInfo);
			}
			else {
				modificationInfoSet.add(fMS.getFileSetModificationInfo(file, primarySetFile));
			}
		}
		
		return modificationInfoSet;
	}
}
