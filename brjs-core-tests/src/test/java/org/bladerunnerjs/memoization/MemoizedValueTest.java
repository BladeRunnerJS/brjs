package org.bladerunnerjs.memoization;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.MockAppVersionGenerator;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.TestLoggerFactory;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MemoizedValueTest {
	private File tempDir;
	private File sdkDir;
	private File watchFile;
	private BRJS brjs;
	private LogMessageStore loggerMessageStore;
	
	@Before
	public void setUp() throws Exception {
		tempDir = FileUtils.createTemporaryDirectory( this.getClass() );
		sdkDir = new File(tempDir, "sdk");
		watchFile = new File(sdkDir, "watch-file");
		
		sdkDir.mkdir();
		loggerMessageStore = new LogMessageStore();
		loggerMessageStore.storeLogsIfEnabled();
		loggerMessageStore.enableStoringLogs();
		brjs = BRJSTestModelFactory.createModel(sdkDir, new MockPluginLocator(), new TestLoggerFactory(loggerMessageStore), new MockAppVersionGenerator());
	}

	@After
	public void tearDown() {
		brjs.close();
		org.apache.commons.io.FileUtils.deleteQuietly(tempDir);
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
		brjs.getFileModificationRegistry().incrementFileVersion(watchFile);
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
		brjs.getFileModificationRegistry().incrementFileVersion(watchFile);
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
		assertEquals(1, (int) memoizedValue.value(incrementingGetter));
	}
	
	@Test
	public void aMessageIsLoggedWhenValueIsRecalculated() {
		MemoizedValue<Integer> memoizedValue = new MemoizedValue<>("THE_ID", brjs, watchFile);
		Getter<RuntimeException> incrementingGetter = new IncrementingGetter();

		memoizedValue.value(incrementingGetter);
		brjs.getFileModificationRegistry().incrementFileVersion(watchFile);
		memoizedValue.value(incrementingGetter);
		
		loggerMessageStore.verifyDebugLogMessage(MemoizedValue.RECALCULATING_VALUE_MSG, "MemoizedFile_"+tempDir.getAbsolutePath()+".exists");
		loggerMessageStore.verifyDebugLogMessage(MemoizedValue.RECALCULATING_VALUE_MSG, "THE_ID");
	}
	
	@Test
	public void aMessageIsLoggedWhenTheMemoizedValueIsUsed() {
		MemoizedValue<Integer> memoizedValue = new MemoizedValue<>("THE_ID", brjs, watchFile);
		Getter<RuntimeException> incrementingGetter = new IncrementingGetter();

		memoizedValue.value(incrementingGetter);
		memoizedValue.value(incrementingGetter);
		loggerMessageStore.verifyDebugLogMessage(MemoizedValue.USING_MEMOIZED_VALUE_MSG, "THE_ID");
	}
	
	
	private class IncrementingGetter implements Getter<RuntimeException> {
		int count = 0;
		
		@Override
		public Object get() {
			return count++;
		}
	}
}
