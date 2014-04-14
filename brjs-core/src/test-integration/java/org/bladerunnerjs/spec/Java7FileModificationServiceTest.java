package org.bladerunnerjs.spec;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.testing.utility.MockLoggerFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;
import org.junit.Before;
import org.junit.Test;

public class Java7FileModificationServiceTest {
	private final Java7FileModificationService fileModificationService = new Java7FileModificationService(new MockLoggerFactory());
	private final Map<String, FileModifiedChecker> watches = new HashMap<>();
	private File tempDir;
	
	@Before
	public void setUp() throws IOException {
		tempDir = FileUtility.createTemporaryDirectory(Java7FileModificationServiceTest.class.getSimpleName());
	}
	
	/**
	 * Test Directory Structure:
	 * 
	 * root-dir/
	 *    inactive-dir/
	 *       inactive-file
	 *    active-dir/
	 *       active-file
	 *       inactive-file
	 *    incoming-dir
	 *       watched-file
	 *       unwatched-file
	 *    outgoing-dir/
	 *       watched-file
	 *       unwatched-file
	 */
	@Test
	public void java7FileModificationServiceTest() throws Exception {
		// create directory structure
		mkfile("root-dir/active-dir/active-file");
		mkfile("root-dir/active-dir/inactive-file");
		mkfile("root-dir/inactive-dir/inactive-file");
		mkfile("root-dir/outgoing-dir/watched-file");
		mkfile("root-dir/outgoing-dir/unwatched-file");
		
		// initialize watching service
		fileModificationService.setRootDir(tempDir);
		
		// watch files and directories
		watch("root-dir");
		watch("root-dir/active-dir");
		watch("root-dir/active-dir/active-file");
		watch("root-dir/active-dir/inactive-file");
		watch("root-dir/inactive-dir");
		watch("root-dir/inactive-dir/inactive-file");
		watch("root-dir/incoming-dir");
		watch("root-dir/incoming-dir/watched-file");
		watch("root-dir/outgoing-dir");
		watch("root-dir/outgoing-dir/watched-file");
		
		// verify change is reported on first invocation
		assertChanged("root-dir");
		assertChanged("root-dir/active-dir");
		assertChanged("root-dir/active-dir/active-file");
		assertChanged("root-dir/active-dir/inactive-file");
		assertChanged("root-dir/inactive-dir");
		assertChanged("root-dir/inactive-dir/inactive-file");
		assertChanged("root-dir/incoming-dir");
		assertChanged("root-dir/incoming-dir/watched-file");
		assertChanged("root-dir/outgoing-dir");
		assertChanged("root-dir/outgoing-dir/watched-file");
		
		// perform change
		touch("root-dir/active-dir/active-file");
		mkfile("root-dir/incoming-dir/watched-file");
		mkfile("root-dir/incoming-dir/unwatched-file");
		delete("root-dir/outgoing-dir");
		
		// wait for change
		waitForChange("root-dir");
		
		// verify change
		assertChanged("root-dir/active-dir");
		assertChanged("root-dir/active-dir/active-file");
		assertUnchanged("root-dir/active-dir/inactive-file");
		assertUnchanged("root-dir/inactive-dir");
		assertUnchanged("root-dir/inactive-dir/inactive-file");
		assertChanged("root-dir/incoming-dir");
		assertChanged("root-dir/incoming-dir/watched-file");
		assertChanged("root-dir/outgoing-dir");
		assertChanged("root-dir/outgoing-dir/watched-file");
		
		// verify no further change
		assertUnchanged("root-dir");
		assertUnchanged("root-dir/active-dir");
		assertUnchanged("root-dir/active-dir/active-file");
		assertUnchanged("root-dir/active-dir/inactive-file");
		assertUnchanged("root-dir/inactive-dir");
		assertUnchanged("root-dir/inactive-dir/inactive-file");
		assertUnchanged("root-dir/incoming-dir");
		assertUnchanged("root-dir/incoming-dir/watched-file");
		assertUnchanged("root-dir/outgoing-dir");
		assertUnchanged("root-dir/outgoing-dir/watched-file");
	}
	
	private void mkfile(String filePath) throws IOException {
		FileUtils.writeStringToFile(new File(tempDir,  filePath), "");
	}
	
	private void touch(String filePath) throws IOException {
		FileUtils.touch(new File(tempDir,  filePath));
	}
	
	private void delete(String filePath) throws IOException {
		FileUtils.forceDelete(new File(tempDir,  filePath));
	}
	
	private void watch(String filePath) {
		FileModificationInfo fileModificationInfo = fileModificationService.getModificationInfo(new File(tempDir,  filePath));
		watches.put(filePath, new InfoFileModifiedChecker(fileModificationInfo));
	}
	
	private void waitForChange(String filePath) throws InterruptedException {
		FileModifiedChecker fileModifiedChecker = watches.get(filePath);
		long startTime = new Date().getTime();
		
		while(!fileModifiedChecker.hasChangedSinceLastCheck()) {
			long elapsedTime = new Date().getTime() - startTime;
			if(elapsedTime > 5000) {
				fail("Timeout: no change detected after 5 seconds.");
			}
			
			Thread.sleep(25);
		}
	}
	
	private void assertUnchanged(String filePath) {
		assertFalse("'" + filePath + "' not expected to have changed.", watches.get(filePath).hasChangedSinceLastCheck());
	}
	
	private void assertChanged(String filePath) {
		assertTrue("'" + filePath + "' expected to have changed.", watches.get(filePath).hasChangedSinceLastCheck());
	}
}
