package org.bladerunnerjs.memoization;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;


public class DefaultWatchKeyService implements WatchKeyService
{
	
	protected final java.nio.file.WatchService watchService;

	public DefaultWatchKeyService() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}
	
	@Override
	public Map<WatchKey,Path> createWatchKeysForDir(Path dirPath, boolean isNewlyDiscovered) throws IOException {
		Map<WatchKey,Path> watchKeys = new HashMap<>();
		watchKeys.put(createWatchKeyForDir(dirPath), dirPath);
		
		File dir = dirPath.toFile();
		Collection<File> subDirs = FileUtils.listFilesAndDirs(dir, FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File subDir : subDirs) {
			if (subDir.isDirectory()) {
				watchKeys.put(createWatchKeyForDir(subDir.toPath()), subDir.toPath());
			}
		}
		
		return watchKeys;
	}
	
	@Override
	public WatchKey waitForEvents() throws InterruptedException
	{
		return watchService.take();
	}
	
	@Override
	public void close() throws IOException {
		watchService.close();
	}
	
	protected WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		WatchKey watchKey = dirPath.register(watchService,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
		return watchKey;
	}
}
