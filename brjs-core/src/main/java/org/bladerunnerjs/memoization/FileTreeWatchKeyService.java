package org.bladerunnerjs.memoization;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Map;


public class FileTreeWatchKeyService implements WatchKeyService
{
	
	private final java.nio.file.WatchService watchService;
	
	public FileTreeWatchKeyService() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}

	@Override
	public Map<WatchKey,Path> createWatchKeysForDir(Path dirPath, boolean isNewlyDiscovered) throws IOException {
		Map<WatchKey,Path> watchKeys = new HashMap<>();
		
		if (isNewlyDiscovered) {
			return watchKeys;
		}
		
		watchKeys.put(createWatchKeyForDir(dirPath), dirPath);
		
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
	
	private WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		WatchKey watchKey = dirPath.register(watchService,getExtendedWatchEventFileTreeEnum());
		return watchKey;
	}
	
	// com.sun.nio.file.ExtendedWatchEventModifier isn't a globally support class and may not be available so use reflection
	private static Kind<?> getExtendedWatchEventFileTreeEnum() {
		try {
			Class<?> c = Class.forName("com.sun.nio.file.ExtendedWatchEventModifier");
			Field f = c.getField("FILE_TREE");
			return (Kind<?>) f.get(c);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean isSupported() {
		return getExtendedWatchEventFileTreeEnum() != null;
	}
}
