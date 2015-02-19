package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.memoization.WatchKeyService;
import org.bladerunnerjs.memoization.WatchKeyServiceFactory;

import static java.nio.file.StandardWatchEventKinds.*;


public class FileModificationWatcherThread extends Thread
{
	public static final String USING_WATCH_SERVICE_MSG = "%s using %s as the file watcher service";
	public static final String THREAD_IDENTIFIER = FileModificationWatcherThread.class.getSimpleName();
	public static final String FILE_CHANGED_MSG = THREAD_IDENTIFIER+" detected a '%s' event for '%s'. Incrementing the file version.";
	public static final String CANT_RESET_PATH_MSG = "A watch key could not be reset for the path '%s' but the directory or file still exists. "+
			"You might need to reset the process for file changes to be detected.";
	
	private Path directoryToWatch;
	private FileModificationRegistry fileModificationRegistry;

	private BRJS brjs;
	private WatchKeyServiceFactory watchKeyServiceFactory;
	private WatchKeyService watchKeyService;

	private final Map<WatchKey,Path> watchKeys = new HashMap<>();

	private Logger logger;

	
	public FileModificationWatcherThread(BRJS brjs, WatchKeyServiceFactory watchKeyServiceFactory) throws IOException
	{
		this.watchKeyServiceFactory = watchKeyServiceFactory;
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
    		
    		while(true) {
    			checkForUpdates();
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

	void init() throws IOException {
		// create the watch service in the init method so we get a 'too many open files' exception
		watchKeyService = watchKeyServiceFactory.createWatchService();
		logger = brjs.logger(this.getClass());
		logger.debug(USING_WATCH_SERVICE_MSG, this.getClass().getSimpleName(), watchKeyService.getClass().getSimpleName());
		watchKeys.putAll( watchKeyService.createWatchKeysForDir(directoryToWatch, false) );
	}
	
	void checkForUpdates() throws IOException, InterruptedException
	{
		WatchKey key = watchKeyService.waitForEvents();
		Path path = watchKeys.get(key);
		
		if (path == null) {
			return; // the watch service picked up an event that we didn't register for (possibly from another process/user of the WatchService
		}
		
		pollWatchKeyForEvents(watchKeys, path, key);
	}
	
	void tearDown() {
		for (WatchKey watchKey : watchKeys.keySet()) {
			watchKey.cancel();
		}
		Thread.interrupted();
		watchKeys.clear();
		try
		{
			if (watchKeyService != null)
			{
				watchKeyService.close();
			}
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	private void pollWatchKeyForEvents(Map<WatchKey,Path> watchKeys, Path watchPath, WatchKey watchKey) throws IOException
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
            	watchKeys.putAll( watchKeyService.createWatchKeysForDir(child, true) );
            }
            
			logger.debug(FILE_CHANGED_MSG, kind, childFile.getPath());
            fileModificationRegistry.incrementFileVersion(childFile);
            
            boolean isWatchKeyReset = watchKey.reset();
            if( !isWatchKeyReset ) {
            	if (!childFile.exists()) {
            		watchKey.cancel();
            		watchKeys.remove(watchPath);            		
            	} else {
            		logger.debug(CANT_RESET_PATH_MSG, watchPath);
            	}
			}
		}
	}
	
}