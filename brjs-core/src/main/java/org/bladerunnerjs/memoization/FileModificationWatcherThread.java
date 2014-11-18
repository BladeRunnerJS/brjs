package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;

import static java.nio.file.StandardWatchEventKinds.*;


public class FileModificationWatcherThread extends Thread
{
	private static final int THREAD_SLEEP_INTERVAL = 750;

	public static final String THREAD_IDENTIFIER = FileModificationWatcherThread.class.getSimpleName();
	
	private Path directoryToWatch;
	private FileModificationRegistry fileModificationRegistry;

	private BRJS brjs;
	private WatchService fileWatcherService;

	private Map<Path,WatchKey>  watchKeys;
	
	public FileModificationWatcherThread(BRJS brjs) throws IOException
	{
		this(brjs, null);
	}
	
	protected FileModificationWatcherThread(BRJS brjs, WatchService fileWatcherService)
	{
		this.fileModificationRegistry = brjs.getFileModificationRegistry();
		directoryToWatch = brjs.dir().toPath();
		this.brjs = brjs;
		this.fileWatcherService = fileWatcherService;
	}
	
	@Override
	public void run()
	{
		Thread.currentThread().setName(THREAD_IDENTIFIER);
		try {

    		init();
    		
    		while (!isInterrupted()) {
    			checkForUpdates();
    			Thread.sleep(THREAD_SLEEP_INTERVAL);
    		}
		} catch (InterruptedException ex) {
			// do nothing
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		finally {
			tearDown();
		}
	}

	protected void init() throws IOException {
		if (fileWatcherService == null) {
			fileWatcherService = new WatchService();
		}
		watchKeys = new HashMap<>();
		addWatchKeysForNestedDirs(directoryToWatch.toFile());
	}
	
	protected void checkForUpdates() throws IOException
	{
		List<Path> watchPaths = new ArrayList<>(watchKeys.keySet()); // create a duplicate so we can change the underlying map as we iterate over it
		for (Path watchPath : watchPaths) {
			WatchKey watchKey = watchKeys.get(watchPath);
			pollWatchKeyForEvents(watchKeys, watchPath, watchKey);
		}
	}
	
	protected void tearDown() {
		for (WatchKey watchKey : watchKeys.values()) {
			watchKey.cancel();
		}
		Thread.interrupted();
		watchKeys.clear();
		try
		{
			fileWatcherService.close();
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private void pollWatchKeyForEvents(Map<Path,WatchKey> watchKeys, Path watchPath, WatchKey watchKey) throws IOException
	{
		for (WatchEvent<?> event: watchKey.pollEvents()) {
	        WatchEvent.Kind<?> kind = event.kind();
	        if (kind == OVERFLOW) {
	        	// invalidate all files since the OVERFLOW event is only generated if there were too many events on the queue
	        	fileModificationRegistry.incrementFileVersion(brjs.dir()); 
	            continue;
	        }

	        @SuppressWarnings("unchecked")
			WatchEvent<Path> ev = (WatchEvent<Path>)event;
	        Path filename = ev.context();
	        
            Path child = watchPath.resolve(filename);
            
            File childFile = child.toFile();
			if (kind == ENTRY_CREATE && childFile.isDirectory()) {
            	watchKeys.put( child , fileWatcherService.createWatchKeyForDir(child) );
            }
            
            fileModificationRegistry.incrementFileVersion(childFile);
            
            boolean isWatchKeyReset = watchKey.reset();
            if( !isWatchKeyReset ) {
            	if (!childFile.exists()) {
            		watchKey.cancel();
            		watchKeys.remove(watchPath);            		
            	} else {
            		brjs.logger(this.getClass()).warn("A watch key could not be reset for the path '%s' but the directory or file still exists. "+
            				"You might need to reset the process for file changes to be detected.", watchPath);
            	}
			}
		}
	}

	private void addWatchKeysForNestedDirs(File dir) throws IOException
	{
		if (!dir.isDirectory()) {
			return;
		}
		Path dirPath = dir.toPath();
		watchKeys.put( dirPath, fileWatcherService.createWatchKeyForDir(dirPath) );
		for (File child : dir.listFiles()) {
			addWatchKeysForNestedDirs(child);
		}
	}
	
}