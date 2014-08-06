package org.bladerunnerjs.utility.filemodification;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.utility.FileUtility;
import org.junit.Before;
import org.junit.Test;

public class PessimisticFileModificationInfoTest {
	private File testDir;
	
	@Before
	public void setUp() throws Exception {
		testDir = FileUtility.createTemporaryDirectory(PessimisticFileModificationInfoTest.class.getSimpleName());
	}
	
	@Test
	public void lastModifiedShouldNotChangeForANonExistentFile() {
		File nonExistentFile = new File(testDir, "non-existent-file");
		FileModificationInfo fMI = new PessimisticFileModificationInfo(nonExistentFile);
		
		assertEquals(0, fMI.getLastModified());
		assertEquals(0, fMI.getLastModified());
	}
	
	@Test
	public void lastModifiedChangesAsAFileComesIntoExistence() throws Exception {
		File initiallyNonExistentFile = new File(testDir, "initialially-non-existent-file");
		FileModificationInfo fMI = new PessimisticFileModificationInfo(initiallyNonExistentFile);
		
		assertEquals(0, fMI.getLastModified());
		
		FileUtils.write(initiallyNonExistentFile, "content");
		
		assertEquals(1, fMI.getLastModified());
		assertEquals(1, fMI.getLastModified());
	}
	
	@Test
	public void lastModifiedChangesAsAFileGoesOutOfExistence() throws Exception {
		File existentFile = new File(testDir, "existent-file");
		FileUtils.write(existentFile, "content");
		FileModificationInfo fMI = new PessimisticFileModificationInfo(existentFile);
		
		assertEquals(1, fMI.getLastModified());
		assertEquals(1, fMI.getLastModified());
		
		existentFile.delete();
		
		assertEquals(2, fMI.getLastModified());
		assertEquals(2, fMI.getLastModified());
	}
	
	@Test
	public void lastModifiedShouldChangeIfTheFileDoesntChange() throws Exception {
		File file = new File(testDir, "file");
		FileUtils.write(file, "content");
		FileModificationInfo fMI = new PessimisticFileModificationInfo(file);
		
		assertEquals(1, fMI.getLastModified());
		assertEquals(1, fMI.getLastModified());
	}
	
	@Test
	public void lastModifiedShouldChangeIfTheFileChanges() throws Exception {
		File file = new File(testDir, "file");
		FileUtils.write(file, "initial content");
		FileModificationInfo fMI = new PessimisticFileModificationInfo(file);
		
		assertEquals(1, fMI.getLastModified());
		
		FileUtils.write(file, "subsequent content");
		
		assertEquals(2, fMI.getLastModified());
		assertEquals(2, fMI.getLastModified());
	}
	
	@Test
	public void lastModifiedShouldNotChangeIfTheExactSameContentIsReWritten() throws Exception {
		File file = new File(testDir, "file");
		FileUtils.write(file, "unchanging content");
		FileModificationInfo fMI = new PessimisticFileModificationInfo(file);
		
		assertEquals(1, fMI.getLastModified());
		
		FileUtils.write(file, "unchanging content");
		
		assertEquals(1, fMI.getLastModified());
		assertEquals(1, fMI.getLastModified());
	}
}
