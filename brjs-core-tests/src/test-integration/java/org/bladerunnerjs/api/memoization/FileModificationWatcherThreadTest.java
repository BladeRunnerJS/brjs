package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.memoization.DefaultWatchKeyService;
import org.bladerunnerjs.memoization.WatchKeyServiceFactory;
import org.bladerunnerjs.utility.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileModificationWatcherThreadTest
{	
	private static final int MAX_UPDATE_CHECKS = 120; // 120 x 500ms (wait 1 minute before failing)
	private static final int THREAD_SLEEP_INTEVAL = 500;
	
	private BRJS mockBrjs;
	private DefaultWatchKeyService mockWatchKeyService;
	private FileModificationRegistry mockModificationRegistry;
	private MemoizedFile rootWatchDir;
	private FileModificationWatcherThread modificationWatcherThread;
	private List<File> fileChanges = Collections.synchronizedList( new ArrayList<>() );
	private File fileInRoot;
	private File dirInRoot;
	private File fileInChildDir;
	private WatchKey rootWatchDirWatchKey;
	private WatchKey dirInRootWatchKey;
	private File nestedDir;
	private WatchKey nestedDirWatchKey;
	private WatchKeyServiceFactory mockWatchServiceFactory;
	private Logger mockLogger;
	
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
		mockLogger = mock(Logger.class);
		when(mockBrjs.logger(any(Class.class))).thenReturn(mockLogger);
		
		mockWatchKeyService = mock(DefaultWatchKeyService.class);
		mockWatchServiceFactory = mock(WatchKeyServiceFactory.class);
		when(mockWatchServiceFactory.createWatchService()).thenReturn(mockWatchKeyService);
		
		rootWatchDirWatchKey = mock(WatchKey.class);
		when(rootWatchDirWatchKey.reset()).thenReturn(true);
		dirInRootWatchKey = mock(WatchKey.class);
		when(dirInRootWatchKey.reset()).thenReturn(true);
		nestedDirWatchKey = mock(WatchKey.class);
		when(nestedDirWatchKey.reset()).thenReturn(true);
		
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
	public void messageIsLoggedWhenFileChangesAreDetected() throws Exception
	{
		allowMockWatchKeyForDir( rootWatchDir, rootWatchDirWatchKey );
		
		createAndInitWatcher();
		verify(mockLogger).debug(FileModificationWatcherThread.USING_WATCH_SERVICE_MSG, FileModificationWatcherThread.class.getSimpleName(), mockWatchKeyService.getClass().getSimpleName());
		
		queueWatchServiceEventKeys(rootWatchDirWatchKey);
				
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mockCreateFileEvent(fileInRoot));
		checkForUpdates(1);
		verify(mockLogger).debug(FileModificationWatcherThread.FILE_CHANGED_MSG, ENTRY_CREATE, fileInRoot.getPath());
		
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mockFileChangeEvent(fileInRoot));
		checkForUpdates(1);
		verify(mockLogger).debug(FileModificationWatcherThread.FILE_CHANGED_MSG, ENTRY_MODIFY, fileInRoot.getPath());
		
		queueWatchKeyPollEvents(rootWatchDirWatchKey, mockFileDeleteEvent(fileInRoot));
		checkForUpdates(1);
		verify(mockLogger).debug(FileModificationWatcherThread.FILE_CHANGED_MSG, ENTRY_DELETE, fileInRoot.getPath());
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
	
	
	
	
	/*
	 * #### The following 2 tests are Ignored because they are unreliable on Travis and Linux ####
	 * TODO: investigate the unreliability
	 */
	
	@Test @Ignore // we use the package private methods on FileModificationWatcherThread here to avoid having a multithreaded test
	public void usingTheRealWatchServiceDetectsFileChanges() throws Exception {
		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, new WatchKeyServiceFactory());
		
		modificationWatcherThread.init();
		
		fileInRoot.createNewFile();
		
		if (!waitForUpdate(fileInRoot, true)) {
			fail("Changes were not detected");			
		}
	}
	
	@Test @Ignore // we use the package private methods on FileModificationWatcherThread here to avoid having a multithreaded test
	public void usingTheRealWatchServiceDetectsFileNestedChanges() throws Exception {
		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, new WatchKeyServiceFactory());
		
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
		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, mockWatchServiceFactory);
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
		file.createNewFile();
		return createMockEvent(file, ENTRY_CREATE);
	}
	
	private WatchEvent<Path> mockFileChangeEvent(File file) throws IOException
	{
		file.setLastModified(System.currentTimeMillis());
		return createMockEvent(file, ENTRY_MODIFY);
	}
	
	private WatchEvent<Path> mockFileDeleteEvent(File file) throws IOException
	{
		org.apache.commons.io.FileUtils.deleteQuietly(file);
		return createMockEvent(file, ENTRY_DELETE);
	}
	
	private WatchEvent<Path> mockMkdirEvent(File dir)
	{
		dir.mkdir();		
		return createMockEvent(dir, ENTRY_CREATE);
	}
	
	private WatchEvent<Path> createMockEvent(File dir, Kind<Path> kind)
	{
		@SuppressWarnings("unchecked")
		WatchEvent<Path> watchEvent = mock(WatchEvent.class);
		when(watchEvent.kind()).thenReturn(kind);
		when(watchEvent.context()).thenReturn(dir.toPath());
		return watchEvent;
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
