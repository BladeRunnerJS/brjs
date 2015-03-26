package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class WatchingFileModificationObserverThreadTest
{	
	private static final int MAX_UPDATE_CHECKS = 120; // 120 x 500ms (wait 1 minute before failing)
	private static final int THREAD_SLEEP_INTEVAL = 500;
	
	private BRJS mockBrjs;
	private DefaultWatchKeyService mockWatchKeyService;
	private FileModificationRegistry mockModificationRegistry;
	private MemoizedFile rootWatchDir;
	private WatchingFileModificationObserverThread modificationWatcherThread;
	private List<File> fileChanges = Collections.synchronizedList( new ArrayList<>() );
	private File fileInRoot;
	private File dirInRoot;
	private File fileInChildDir;
	private WatchKey rootWatchDirWatchKey;
	private WatchKey dirInRootWatchKey;
	private File nestedDir;
	private WatchKey nestedDirWatchKey;
	private WatchKeyServiceFactory mockWatchServiceFactory;
	
	@Before
	public void setup() throws IOException, InterruptedException {
		
		mockModificationRegistry = mock(FileModificationRegistry.class);
		doAnswer(new Answer<Object>() {
	        public Object answer(InvocationOnMock invocation) {
	            Object[] args = invocation.getArguments();
	            fileChanges.add( (File) args[0] );
	            return null;
	        }
	    }).when(mockModificationRegistry).incrementFileVersion(any(File.class));
		
		mockBrjs = mock(BRJS.class);
		rootWatchDir = new MemoizedFile(mockBrjs, FileUtils.createTemporaryDirectory( this.getClass() ).getAbsolutePath() );
		when(mockBrjs.dir()).thenReturn(rootWatchDir);
		when(mockBrjs.getFileModificationRegistry()).thenReturn(mockModificationRegistry);
		when(mockBrjs.logger(any(Class.class))).thenReturn(mock(Logger.class));
		
		mockWatchKeyService = mock(DefaultWatchKeyService.class);
		mockWatchServiceFactory = mock(WatchKeyServiceFactory.class);
		when(mockWatchServiceFactory.createWatchService()).thenReturn(mockWatchKeyService);
		
		rootWatchDirWatchKey = mock(WatchKey.class);
		dirInRootWatchKey = mock(WatchKey.class);
		nestedDirWatchKey = mock(WatchKey.class);
		
		fileInRoot = new File(rootWatchDir, "some-file.txt");
		dirInRoot = new File(rootWatchDir, "some-dir");
		fileInChildDir = new File(dirInRoot, "nested-file.txt");
		nestedDir = new File(dirInRoot, "nested-dir");
		
		rootWatchDir.getUnderlyingFile().mkdir();
	}
	
	@After
	public void tearDown() {
		modificationWatcherThread.interrupt();
		modificationWatcherThread.tearDown();
		FileUtils.deleteQuietly(rootWatchDir);
	}
	
	@Test
	public void watchEventsCauseTheFileModificationRegistryToBeNotified() throws Exception
	{
		allowMockWatchKeyForDir( rootWatchDir, rootWatchDirWatchKey );
		
		createAndInitWatcher();
		
		queueWatchServiceEventKeys(rootWatchDirWatchKey);
		
		WatchEvent<Path> createNewFileWatchEvent = mockCreateFileEvent(fileInRoot);

		queueWatchKeyPollEvents(rootWatchDirWatchKey, createNewFileWatchEvent);
		
		checkForUpdates(1);
		
		verify(mockWatchKeyService, times(1)).waitForEvents();
		assertEquals(1, fileChanges.size());
		assertEquals(fileInRoot, fileChanges.get(0));
	}

	@Test
	public void newDirectoryWatchEventsCauseWatchKeysToBeCreatedForTheNewDirectory() throws Exception
	{
		allowMockWatchKeyForDir( rootWatchDir, rootWatchDirWatchKey );
		allowMockWatchKeyForDir( dirInRoot, dirInRootWatchKey );

		createAndInitWatcher();
		
		WatchEvent<Path> mkdirWatchEvent = mockMkdirEvent(dirInRoot);

		queueWatchServiceEventKeys(rootWatchDirWatchKey, dirInRootWatchKey);
		
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mkdirWatchEvent);
		queueWatchKeyPollEvents(dirInRootWatchKey);

		checkForUpdates(1);
		
		verify(mockWatchKeyService, times(1)).waitForEvents();
		verify(mockWatchKeyService).createWatchKeysForDir(rootWatchDir.toPath(), false);
		verify(mockWatchKeyService).createWatchKeysForDir(dirInRoot.toPath(), true);
		verifyNoMoreInteractions(mockWatchKeyService);
	}

	@Test
	public void newDirectoryWatchEventsCauseTheFileModificationServiceToBeNotified() throws Exception
	{
		allowMockWatchKeyForDir( rootWatchDir, rootWatchDirWatchKey );
		allowMockWatchKeyForDir( dirInRoot, dirInRootWatchKey );

		createAndInitWatcher();
		
		WatchEvent<Path> mkdirWatchEvent = mockMkdirEvent(dirInRoot);
		
		queueWatchServiceEventKeys(rootWatchDirWatchKey, dirInRootWatchKey);
		
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mkdirWatchEvent);
		queueWatchKeyPollEvents(dirInRootWatchKey);

		checkForUpdates(1);
		
		verify(mockWatchKeyService, times(1)).waitForEvents();
		assertEquals(1, fileChanges.size());
		assertEquals(dirInRoot, fileChanges.get(0));
	}
	
	@Test
	public void changesToFilesInNewDirectoriesCauseTheFileModifictationRegistryToBeNotified() throws Exception
	{
		allowMockWatchKeyForDir( rootWatchDir, rootWatchDirWatchKey );
		allowMockWatchKeyForDir( dirInRoot, dirInRootWatchKey );

		createAndInitWatcher();
		
		WatchEvent<Path> mkdirWatchEvent = mockMkdirEvent(dirInRoot);
		
		WatchEvent<Path> createNewFileWatchEvent = mockCreateFileEvent(fileInChildDir);
		
		queueWatchServiceEventKeys(rootWatchDirWatchKey, dirInRootWatchKey);
		
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mkdirWatchEvent);
		queueWatchKeyPollEvents(dirInRootWatchKey, createNewFileWatchEvent);
		
		checkForUpdates(2);
		
		assertEquals(2, fileChanges.size());
		assertEquals(dirInRoot, fileChanges.get(0));
		assertEquals(fileInChildDir, fileChanges.get(1));
	}
	
	@Test
	public void newNestedDirectoriesCauseWatchKeysToBeCreatedForTheNewDirectory() throws Exception
	{		
		allowMockWatchKeyForDir( rootWatchDir, rootWatchDirWatchKey );
		allowMockWatchKeyForDir( dirInRoot, dirInRootWatchKey );
		allowMockWatchKeyForDir( nestedDir, nestedDirWatchKey );

		createAndInitWatcher();
		
		WatchEvent<Path> mkdirWatchEvent = mockMkdirEvent(dirInRoot);
		WatchEvent<Path> nestedMkdirWatchEvent = mockMkdirEvent(nestedDir);

		queueWatchServiceEventKeys(rootWatchDirWatchKey, dirInRootWatchKey, nestedDirWatchKey);
		
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mkdirWatchEvent);
		queueWatchKeyPollEvents(dirInRootWatchKey, nestedMkdirWatchEvent);
		queueWatchKeyPollEvents(nestedDirWatchKey);
		
		checkForUpdates(2);
		
		verify(mockWatchKeyService, times(2)).waitForEvents();
		verify(mockWatchKeyService).createWatchKeysForDir(rootWatchDir.toPath(), false);
		verify(mockWatchKeyService).createWatchKeysForDir(dirInRoot.toPath(), true);
		verify(mockWatchKeyService).createWatchKeysForDir(nestedDir.toPath(), true);
		verifyNoMoreInteractions(mockWatchKeyService);
	}
	
	@Test // we use the package private methods on FileModificationWatcherThread here to avoid having a multithreaded test
	public void usingTheRealWatchServiceDetectsFileChanges() throws Exception {
		modificationWatcherThread = new WatchingFileModificationObserverThread(mockBrjs, new WatchKeyServiceFactory());
		
		modificationWatcherThread.init();
		
		fileInRoot.createNewFile();
		
		if (!waitForUpdate(fileInRoot, true)) {
			fail("Changes were not detected");			
		}
	}
	
	@Test // we use the package private methods on FileModificationWatcherThread here to avoid having a multithreaded test
	public void usingTheRealWatchServiceDetectsFileNestedChanges() throws Exception {
		modificationWatcherThread = new WatchingFileModificationObserverThread(mockBrjs, new WatchKeyServiceFactory());
		
		modificationWatcherThread.init();
		
		File dir1 = new File(rootWatchDir, "dir1");
		File dir2 = new File(dir1, "dir2");
		File dir3 = new File(dir2, "dir3");
		File nestedFile = new File(dir3, "file.txt");
		
		for (File f : Arrays.asList(dir1, dir2, dir3)) {
			f.mkdir();
			if (!waitForUpdate(f)) {
				fail("Changes were not detected");			
			}
		}
		
		nestedFile.createNewFile();
		if (!waitForUpdate(nestedFile)) {
			fail("Changes were not detected");			
		}
	}
	
	
	
	private boolean waitForUpdate(File expectedFile) throws InterruptedException, IOException {
		return waitForUpdate(expectedFile, false);
	}
	
	private boolean waitForUpdate(File expectedFile, boolean mustBeIndex0) throws InterruptedException, IOException {
		for (int i = 0; i < MAX_UPDATE_CHECKS; i++) {
			checkForUpdates(1);
			boolean foundFileChange = (mustBeIndex0) ? fileChanges.indexOf(expectedFile) == 0 : fileChanges.contains(expectedFile);
			if (fileChanges.size() > 0 && foundFileChange) {
				fileChanges.remove(foundFileChange);
				return true;
			}
			Thread.sleep(THREAD_SLEEP_INTEVAL);
		}
		return false;
	}
	
	private void createAndInitWatcher() throws IOException
	{
		modificationWatcherThread = new WatchingFileModificationObserverThread(mockBrjs, mockWatchServiceFactory);
		modificationWatcherThread.init();
	}
	
	private void queueWatchServiceEventKeys(WatchKey... watchKeys) throws InterruptedException
	{
		OngoingStubbing<WatchKey> stub = when(mockWatchKeyService.waitForEvents());
		for (WatchKey key : watchKeys) {
			stub = stub.thenReturn(key);
		}
	}
	
	private void queueWatchKeyPollEvents(WatchKey key, WatchEvent<?>... watchEvents) throws InterruptedException
	{
		OngoingStubbing<List<WatchEvent<?>>> stub = when(key.pollEvents());
		for (WatchEvent<?> event : watchEvents) {
			stub = stub.thenReturn( Arrays.asList(event) );
		}
		stub.thenReturn( Arrays.asList() );
	}
	
	private WatchEvent<Path> mockCreateFileEvent(File file) throws IOException
	{
		@SuppressWarnings("unchecked")
		WatchEvent<Path> createNewFileWatchEvent = mock(WatchEvent.class);
		when(createNewFileWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(createNewFileWatchEvent.context()).thenReturn(file.toPath());
		file.createNewFile();
		return createNewFileWatchEvent;
	}
	
	private WatchEvent<Path> mockMkdirEvent(File dir)
	{
		@SuppressWarnings("unchecked")
		WatchEvent<Path> mkdirWatchEvent = mock(WatchEvent.class);
		when(mkdirWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(mkdirWatchEvent.context()).thenReturn(dir.toPath());
		dir.mkdir();		
		return mkdirWatchEvent;
	}
	
	private void allowMockWatchKeyForDir(File watchDir, WatchKey watchKey) throws IOException
	{
		when(mockWatchKeyService.createWatchKeysForDir( eq(watchDir.toPath()), any(Boolean.class)) )
			.thenReturn( ImmutableMap.of( watchKey, watchDir.toPath() ) );
	}
	
	private void checkForUpdates(int times) throws IOException, InterruptedException
	{
		for (int i = 0; i < times; i++) {
			modificationWatcherThread.checkForUpdates();
		}
	}
	
}
