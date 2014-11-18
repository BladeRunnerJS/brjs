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
	
	public FileModificationWatcherThread(BRJS brjs, WatchServiceFactory watchServiceFactory) throws IOException
	{
		fileWatcherService = watchServiceFactory.createWatchService();
		this.fileModificationRegistry = brjs.getFileModificationRegistry();
		directoryToWatch = brjs.dir().toPath();
		this.brjs = brjs;
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
		watchKeys = new HashMap<>();
		watchKeys.putAll( fileWatcherService.createWatchKeysForDir(directoryToWatch, false) );
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
	        	fileModificationRegistry.incrementAllFileVersions(); 
	            continue;
	        }

	        @SuppressWarnings("unchecked")
			WatchEvent<Path> ev = (WatchEvent<Path>)event;
	        Path filename = ev.context();

            Path child = watchPath.resolve(filename);
            
            File childFile = child.toFile();
			if (kind == ENTRY_CREATE && childFile.isDirectory()) {
            	watchKeys.putAll( fileWatcherService.createWatchKeysForDir(child, true) );
            }
            
            fileModificationRegistry.incrementFileVersion(childFile);
            
            boolean isWatchKeyReset = watchKey.reset();
            if( !isWatchKeyReset ) {
            	if (!childFile.exists()) {
            		watchKey.cancel();
            		watchKeys.remove(watchPath);            		
            	} else {
            		brjs.logger(this.getClass()).debug("A watch key could not be reset for the path '%s' but the directory or file still exists. "+
            				"You might need to reset the process for file changes to be detected.", watchPath);
            	}
			}
		}
	}
	
}