package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;

import static java.nio.file.StandardWatchEventKinds.*;


public class FileModificationWatcherThread extends Thread
{
	public static final String THREAD_IDENTIFIER = FileModificationWatcherThread.class.getSimpleName();
	
	private Path directoryToWatch;

	private FileModificationRegistry fileModificationRegistry;

	private WatchService watchService;

	private HashMap<Path, WatchKey> watchKeys;	
	
	public FileModificationWatcherThread(BRJS brjs) throws IOException
	{
		this.fileModificationRegistry = brjs.getFileModificationRegistry();
		directoryToWatch = brjs.dir().toPath();
		watchService = FileSystems.getDefault().newWatchService();
	}
	
	@Override
	public void run()
	{
		Thread.currentThread().setName(THREAD_IDENTIFIER);
		try {
			watchKeys = new HashMap<>();
			addWatchKeysForNestedDirs(watchService, watchKeys, directoryToWatch.toFile());
    		
    		while (!isInterrupted()) {
    			checkForUpdates();
    			Thread.sleep(1000);
    		}
		} catch (InterruptedException ex) {
			// do nothing
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		for (WatchKey watchKey : watchKeys.values()) {
			watchKey.cancel();
		}
		watchKeys.clear();
		Thread.interrupted();
	}

	private void checkForUpdates() throws IOException
	{
		List<Path> watchPaths = new ArrayList<>(watchKeys.keySet()); // create a duplicate so we can change the underlying map as we iterate over it
		for (Path watchPath : watchPaths) {
			WatchKey watchKey = watchKeys.get(watchPath);
			pollWatchKeyForEvents(watchService, watchKeys, watchPath, watchKey);
		}
	}

	private void pollWatchKeyForEvents(WatchService watchService, Map<Path,WatchKey> watchKeys, Path watchPath, WatchKey watchKey) throws IOException
	{
		for (WatchEvent<?> event: watchKey.pollEvents()) {
	        WatchEvent.Kind<?> kind = event.kind();
	        
	        if (kind == OVERFLOW) {
	            continue;
	        }

	        @SuppressWarnings("unchecked")
			WatchEvent<Path> ev = (WatchEvent<Path>)event;
	        Path filename = ev.context();
	        
            Path child = watchPath.resolve(filename);
            
            if (kind == ENTRY_CREATE && child.toFile().isDirectory()) {
            	watchKeys.put( child , createWatchKeyForDir(watchService, child) );
            }
            
            fileModificationRegistry.incrementFileVersion(child.toFile());
            
            if(!watchKey.reset()) {
            	watchKeys.remove(watchPath);
			}
		}
	}

	private void addWatchKeysForNestedDirs(WatchService watchService, Map<Path,WatchKey> watchKeys, File dir) throws IOException
	{
		if (!dir.isDirectory()) {
			return;
		}
		Path dirPath = dir.toPath();
		watchKeys.put( dirPath, createWatchKeyForDir(watchService, dirPath) );
		for (File child : dir.listFiles()) {
			addWatchKeysForNestedDirs(watchService, watchKeys, child);
		}
	}

	private WatchKey createWatchKeyForDir(WatchService watchService, Path dirPath) throws IOException {
		return dirPath.register(watchService,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
	}
	
}