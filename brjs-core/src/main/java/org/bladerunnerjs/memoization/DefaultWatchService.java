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


public class DefaultWatchService implements WatchService
{

	protected java.nio.file.WatchService watchService;

	public DefaultWatchService() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}

	@Override
	public Map<Path,WatchKey> createWatchKeysForDir(Path dirPath, boolean isNewlyDiscovered) throws IOException {
		Map<Path,WatchKey> watchKeys = new HashMap<>();
		watchKeys.put(dirPath, createWatchKeyForDir(dirPath));
		
		File dir = dirPath.toFile();
		Collection<File> subDirs = FileUtils.listFilesAndDirs(dir, FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		for (File subDir : subDirs) {
			watchKeys.put(subDir.toPath(), createWatchKeyForDir(subDir.toPath()));
		}
		
		return watchKeys;
	}
	
	@Override
	public void close() throws IOException {
		watchService.close();
	}
	
	@Override
	public WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		WatchKey watchKey = dirPath.register(watchService,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
		return watchKey;
	}
}
