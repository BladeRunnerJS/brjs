package org.bladerunnerjs.memoization;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;


public class HighSensitivityWatchKeyService extends DefaultWatchKeyService
{
	
	public HighSensitivityWatchKeyService() throws IOException
	{
		super();
	}

	@Override
	public WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		Modifier high = getHighSensitivityWatchEventModifier();
		WatchKey watchKey = dirPath.register(watchService, new WatchEvent.Kind<?>[]{ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY}, high);
		return watchKey;
	}
	
	// from https://github.com/HotswapProjects/HotswapAgent/issues/41
	// com.sun.nio.file.SensitivityWatchEventModifier isn't a globally support class and may not be available so use reflection
	private static Modifier getHighSensitivityWatchEventModifier() {
		try {
			Class<?> c = Class.forName("com.sun.nio.file.SensitivityWatchEventModifier");
			Field f = c.getField("HIGH");
			return (Modifier) f.get(c);
		} catch (Exception e) {
			return null;
		}
	}	
	
	public static boolean isSupported() {
		return getHighSensitivityWatchEventModifier() != null;
	}
}
