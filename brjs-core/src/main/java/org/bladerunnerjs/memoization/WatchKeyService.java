package org.bladerunnerjs.memoization;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent.Modifier;
import java.util.Map;


public interface WatchKeyService
{
	public WatchKey waitForEvents() throws InterruptedException;
	public Map<WatchKey,Path> createWatchKeysForDir(Path dirPath, boolean isNewlyDiscovered) throws IOException;
	public void close() throws IOException;

	public static Modifier getModifierEnum(String className, String fieldName) {
		try {
			Class<?> c = Class.forName(className);
			Field f = c.getField(fieldName);
			return (Modifier) f.get(c);
		} catch (Exception e) {
			return null;
		}
	}
	
}
