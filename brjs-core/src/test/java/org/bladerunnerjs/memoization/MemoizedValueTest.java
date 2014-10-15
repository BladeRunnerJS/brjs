package org.bladerunnerjs.memoization;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filemodification.OptimisticFileModificationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MemoizedValueTest extends TestModelAccessor {
	private File tempDir;
	private File sdkDir;
	private File watchFile;
	private BRJS brjs;
	private File watchFileChild;
	
	@Before
	public void setUp() throws Exception {
		tempDir = FileUtility.createTemporaryDirectory( this.getClass() );
		watchFile = new File(tempDir, "watch-file");
		watchFileChild = new File(tempDir, "watch-file/some/sub/dir/watch-file");
		sdkDir = new File(tempDir, "sdk");
		
		sdkDir.mkdir();
		brjs = createModel(sdkDir, new MockPluginLocator(), new OptimisticFileModificationService(), new TestLoggerFactory(new LogMessageStore()), new MockAppVersionGenerator(), new FileModificationRegistry(true));
	}

	@After
	public void tearDown() {
		brjs.close();
	}
	
	@Test
	public void valueIsCalculatedOnFirstInvocationButMemoizedForSubsequentInvocations() {
		MemoizedValue<Integer> memoizedValue = new MemoizedValue<>("id", brjs, watchFile);
		Getter<RuntimeException> incrementingGetter = new IncrementingGetter();
		
		assertEquals(0, (int) memoizedValue.value(incrementingGetter));
		assertEquals(0, (int) memoizedValue.value(incrementingGetter));
		assertEquals(0, (int) memoizedValue.value(incrementingGetter));
	}
	
	@Test
	public void valueIsRecalculatedTheSecondTimeIfTheFileHasChanged() throws IOException {
		MemoizedValue<Integer> memoizedValue = new MemoizedValue<>("id", brjs, watchFile);
		Getter<RuntimeException> incrementingGetter = new IncrementingGetter();
		
		assertEquals(0, (int) memoizedValue.value(incrementingGetter));
		brjs.getFileModificationRegistry().updateLastModified(watchFile);
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
	}
	
	@Test
	public void valueIsRecalculatedTheSecondTimeIfAnExceptionOcurredTheFirstTime() {
		MemoizedValue<Integer> memoizedValue = new MemoizedValue<>("id", brjs, watchFile);
		Getter<RuntimeException> incrementingGetter = new IncrementingGetter();
		
		try {
			memoizedValue.value(() -> {throw new RuntimeException("Unexpected error!");});
		}
		catch(RuntimeException e) {
		}
		
		assertEquals(0, (int) memoizedValue.value(incrementingGetter));
		brjs.getFileModificationRegistry().updateLastModified(watchFile);
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
	}
	
	@Test
	public void valueIsRecreatedIfAParentDirectoryChanges() {
		MemoizedValue<Integer> memoizedValue = new MemoizedValue<>("id", brjs, watchFileChild);
		Getter<RuntimeException> incrementingGetter = new IncrementingGetter();
		
		assertEquals(0, (int) memoizedValue.value(incrementingGetter));
		brjs.getFileModificationRegistry().updateLastModified(watchFile);
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
	}
	
	private class IncrementingGetter implements Getter<RuntimeException> {
		int count = 0;
		
		@Override
		public Object get() {
			return count++;
		}
	}
}
