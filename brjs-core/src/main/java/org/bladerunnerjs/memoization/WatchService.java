package org.bladerunnerjs.memoization;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;


public class WatchService
{

	private java.nio.file.WatchService watchService;

	public WatchService() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}
	
	public WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		Modifier high = get_com_sun_nio_file_SensitivityWatchEventModifier_HIGH();
		return (high == null) ? dirPath.register(watchService,ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY) : dirPath.register(watchService, new WatchEvent.Kind<?>[]{ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY}, high);
	}
	
	public void close() throws IOException {
		watchService.close();
	}
	
	
	// from https://github.com/HotswapProjects/HotswapAgent/issues/41
	private Modifier get_com_sun_nio_file_SensitivityWatchEventModifier_HIGH() {
		try {
			Class<?> c = Class.forName("com.sun.nio.file.SensitivityWatchEventModifier");
			Field f = c.getField("HIGH");
			return (Modifier) f.get(c);
		} catch (Exception e) {
			return null;
		}
	}
}
