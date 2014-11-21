package org.bladerunnerjs.memoization;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;


public class WindowsFileTreeWatchKeyService implements WatchKeyService
{
	
	private final java.nio.file.WatchService watchService;
	
	public WindowsFileTreeWatchKeyService() throws IOException {
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
		Modifier fileTree = getExtendedWatchEventFileTreeEnum();
		WatchKey watchKey = dirPath.register(watchService, new WatchEvent.Kind<?>[]{ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY}, fileTree);
		return watchKey;
	}
	
	// com.sun.nio.file.ExtendedWatchEventModifier isn't a globally support class and may not be available so use reflection
	private static Modifier getExtendedWatchEventFileTreeEnum() {
		return WatchKeyService.getModifierEnum("com.sun.nio.file.ExtendedWatchEventModifier", "FILE_TREE");
	}
	
	public static boolean isSupported() {
		return getExtendedWatchEventFileTreeEnum() != null && SystemUtils.IS_OS_WINDOWS;
	}
}
