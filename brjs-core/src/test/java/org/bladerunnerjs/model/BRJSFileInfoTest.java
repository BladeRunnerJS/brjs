package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.File;

import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;
import org.bladerunnerjs.utility.filemodification.TestTimeAccessor;
import org.junit.Before;
import org.junit.Test;

public class BRJSFileInfoTest extends TestModelAccessor {
	private File tmpDir;
	private PessimisticFileModificationService fileModificationService;
	
	@Before
	public void setUp() throws Exception {
		tmpDir = FileUtility.createTemporaryDirectory(BRJSFileInfoTest.class.getSimpleName());
		File sdkDir = new File(tmpDir, "sdk");
		sdkDir.mkdir();
		fileModificationService = new PessimisticFileModificationService();
		fileModificationService.initialise(sdkDir, new TestTimeAccessor(), null);
	}
	
	@Test
	public void switchingToANewFileModificationServiceDoesntBreakExistingFileModifiedCheckers() {
		File someDir = new File(tmpDir, "temp");
		someDir.mkdir();
		
		BRJSFileInfo brjsFileInfo = new BRJSFileInfo(someDir, fileModificationService, null, null);
		FileModifiedChecker fileModifiedChecker = new InfoFileModifiedChecker(brjsFileInfo);
		
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
		
		brjsFileInfo.reset(fileModificationService);
		
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
	}
}
