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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileModificationWatcherThreadTest
{	
	private static final int MAX_UPDATE_CHECKS = 120; // 120 x 500ms (wait 1 minute before failing)
	private static final int THREAD_SLEEP_INTEVAL = 500;
	
	private BRJS mockBrjs;
	private WatchService mockWatchService;
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
		
		mockWatchService = mock(WatchService.class);
		
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
		when(mockWatchService.createWatchKeyForDir(rootWatchDir.toPath())).thenReturn(rootWatchDirWatchKey);
		
		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, mockWatchService);
		modificationWatcherThread.init();

		@SuppressWarnings("unchecked")
		WatchEvent<Path> createNewFileWatchEvent = mock(WatchEvent.class);
		when(createNewFileWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(createNewFileWatchEvent.context()).thenReturn(fileInRoot.toPath());
		fileInRoot.createNewFile();

		when(rootWatchDirWatchKey.pollEvents()).thenReturn( Arrays.asList(createNewFileWatchEvent) ).thenReturn( Arrays.asList() );

		modificationWatcherThread.checkForUpdates();
		
		assertEquals(1, fileChanges.size());
		assertEquals(fileInRoot, fileChanges.get(0));
	}
	
	@Test
	public void newDirectoryWatchEventsCauseWatchKeysToBeCreatedForTheNewDirectory() throws Exception
	{
		when(mockWatchService.createWatchKeyForDir(rootWatchDir.toPath())).thenReturn(rootWatchDirWatchKey);
		when(mockWatchService.createWatchKeyForDir(dirInRoot.toPath())).thenReturn(dirInRootWatchKey);

		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, mockWatchService);
		modificationWatcherThread.init();
		
		@SuppressWarnings("unchecked")
		WatchEvent<Path> mkdirWatchEvent = mock(WatchEvent.class);
		when(mkdirWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(mkdirWatchEvent.context()).thenReturn(dirInRoot.toPath());
		dirInRoot.mkdir();
		
		when(rootWatchDirWatchKey.pollEvents()).thenReturn( Arrays.asList(mkdirWatchEvent) ).thenReturn( Arrays.asList() );
		when(dirInRootWatchKey.pollEvents()).thenReturn( Arrays.asList() );

		modificationWatcherThread.checkForUpdates();
		
		verify(mockWatchService).createWatchKeyForDir(rootWatchDir.toPath());
		verify(mockWatchService).createWatchKeyForDir(dirInRoot.toPath());
		verifyNoMoreInteractions(mockWatchService);
	}
	
	@Test
	public void newDirectoryWatchEventsCauseTheFileModificationServiceToBeNotified() throws Exception
	{
		when(mockWatchService.createWatchKeyForDir(rootWatchDir.toPath())).thenReturn(rootWatchDirWatchKey);
		when(mockWatchService.createWatchKeyForDir(dirInRoot.toPath())).thenReturn(dirInRootWatchKey);

		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, mockWatchService);
		modificationWatcherThread.init();
		
		@SuppressWarnings("unchecked")
		WatchEvent<Path> mkdirWatchEvent = mock(WatchEvent.class);
		when(mkdirWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(mkdirWatchEvent.context()).thenReturn(dirInRoot.toPath());
		dirInRoot.mkdir();
		
		when(rootWatchDirWatchKey.pollEvents()).thenReturn( Arrays.asList(mkdirWatchEvent) ).thenReturn( Arrays.asList() );
		when(dirInRootWatchKey.pollEvents()).thenReturn( Arrays.asList() );

		modificationWatcherThread.checkForUpdates();
		
		assertEquals(1, fileChanges.size());
		assertEquals(dirInRoot, fileChanges.get(0));
	}
	
	@Test
	public void changesToFilesInNewDirectoriesCauseTheFileModifictationRegistryToBeNotified() throws Exception
	{
		when(mockWatchService.createWatchKeyForDir(rootWatchDir.toPath())).thenReturn(rootWatchDirWatchKey);
		when(mockWatchService.createWatchKeyForDir(dirInRoot.toPath())).thenReturn(dirInRootWatchKey);

		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, mockWatchService);
		modificationWatcherThread.init();
		
		@SuppressWarnings("unchecked")
		WatchEvent<Path> mkdirWatchEvent = mock(WatchEvent.class);
		when(mkdirWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(mkdirWatchEvent.context()).thenReturn(dirInRoot.toPath());
		dirInRoot.mkdir();
		
		@SuppressWarnings("unchecked")
		WatchEvent<Path> createNewFileWatchEvent = mock(WatchEvent.class);
		when(createNewFileWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(createNewFileWatchEvent.context()).thenReturn(fileInChildDir.toPath());
		fileInChildDir.createNewFile();
		
		when(rootWatchDirWatchKey.pollEvents()).thenReturn( Arrays.asList(mkdirWatchEvent) ).thenReturn( Arrays.asList() );
		when(dirInRootWatchKey.pollEvents()).thenReturn( Arrays.asList(createNewFileWatchEvent) ).thenReturn( Arrays.asList() );
		
		modificationWatcherThread.checkForUpdates();
		modificationWatcherThread.checkForUpdates();
		
		assertEquals(2, fileChanges.size());
		assertEquals(dirInRoot, fileChanges.get(0));
		assertEquals(fileInChildDir, fileChanges.get(1));
	}
	
	@Test
	public void newNestedDirectoriesCauseWatchKeysToBeCreatedForTheNewDirectory() throws Exception
	{		
		when(mockWatchService.createWatchKeyForDir(rootWatchDir.toPath())).thenReturn(rootWatchDirWatchKey);
		when(mockWatchService.createWatchKeyForDir(dirInRoot.toPath())).thenReturn(dirInRootWatchKey);
		when(mockWatchService.createWatchKeyForDir(nestedDir.toPath())).thenReturn(nestedDirWatchKey);

		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs, mockWatchService);
		modificationWatcherThread.init();
		
		@SuppressWarnings("unchecked")
		WatchEvent<Path> mkdirWatchEvent = mock(WatchEvent.class);
		when(mkdirWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(mkdirWatchEvent.context()).thenReturn(dirInRoot.toPath());
		dirInRoot.mkdir();
		
		@SuppressWarnings("unchecked")
		WatchEvent<Path> nestedMkdirWatchEvent = mock(WatchEvent.class);
		when(nestedMkdirWatchEvent.kind()).thenReturn(ENTRY_CREATE);
		when(nestedMkdirWatchEvent.context()).thenReturn(nestedDir.toPath());
		nestedDir.mkdir();
		
		when(rootWatchDirWatchKey.pollEvents()).thenReturn( Arrays.asList(mkdirWatchEvent) ).thenReturn( Arrays.asList() );
		when(dirInRootWatchKey.pollEvents()).thenReturn( Arrays.asList(nestedMkdirWatchEvent) ).thenReturn( Arrays.asList() );
		when(nestedDirWatchKey.pollEvents()).thenReturn( Arrays.asList() );

		modificationWatcherThread.checkForUpdates();
		modificationWatcherThread.checkForUpdates();
		
		verify(mockWatchService).createWatchKeyForDir(rootWatchDir.toPath());
		verify(mockWatchService).createWatchKeyForDir(dirInRoot.toPath());
		verify(mockWatchService).createWatchKeyForDir(nestedDir.toPath());
		verifyNoMoreInteractions(mockWatchService);
	}
	
	@Test // we use the protected methods on FileModificationWatcherThread here to avoid having a multithreaded test
	public void usingTheRealWatchServiceDetectsFileChanges() throws Exception {
		modificationWatcherThread = new FileModificationWatcherThread(mockBrjs);
		
		modificationWatcherThread.init();
		
		fileInRoot.createNewFile();
		
		for (int i = 0; i < MAX_UPDATE_CHECKS; i++) {
			modificationWatcherThread.checkForUpdates();
			if (fileChanges.size() > 0 && fileChanges.get(0).equals(fileInRoot)) {
				return;
			}
			Thread.sleep(THREAD_SLEEP_INTEVAL);
		}
		
		fail("Changes were not detected");
	}
	
}
