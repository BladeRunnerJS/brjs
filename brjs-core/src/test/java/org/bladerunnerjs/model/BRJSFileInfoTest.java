package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import java.io.File;

import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.OptimisticFileModificationService;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;
import org.junit.Before;
import org.junit.Test;

public class BRJSFileInfoTest extends TestModelAccessor {
	private File tmpDir;
	private BRJS brjs;
	private PessimisticFileModificationService fileModificationService;
	
	@Before
	public void setUp() throws Exception {
		tmpDir = FileUtility.createTemporaryDirectory(BRJSFileInfoTest.class.getSimpleName());
		File sdkDir = new File(tmpDir, "sdk");
		sdkDir.mkdir();
		brjs = createModel(sdkDir, new MockPluginLocator(), new OptimisticFileModificationService(), new TestLoggerFactory(new LogMessageStore()), new MockAppVersionGenerator());
		fileModificationService = new PessimisticFileModificationService();
		brjs.setFileModificationService(fileModificationService);
	}
	
	@Test
	public void switchingToANewFileModificationServiceDoesntBreakExistingFileModifiedCheckers() {
		File someDir = new File(tmpDir, "temp");
		someDir.mkdir();
		
		// TODO: change FileInfo so it no longer needs an instance of BRJS, since all it ultimately needs is the ability to get new FileInfo instances
		BRJSFileInfo brjsFileInfo = new BRJSFileInfo(someDir, brjs, fileModificationService);
		FileModifiedChecker fileModifiedChecker = new InfoFileModifiedChecker(brjsFileInfo);
		
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
		
		brjsFileInfo.reset(fileModificationService);
		
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
		assertTrue(fileModifiedChecker.hasChangedSinceLastCheck());
	}
}
