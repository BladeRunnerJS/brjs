package org.bladerunnerjs.memoization;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;

import org.apache.commons.lang3.SystemUtils;


public class MacHighSensitivityWatchKeyService extends DefaultWatchKeyService
{
	
	public MacHighSensitivityWatchKeyService() throws IOException
	{
		super();
	}

	protected WatchKey createWatchKeyForDir(Path dirPath) throws IOException {
		Modifier high = getHighSensitivityWatchEventModifier();
		WatchKey watchKey = dirPath.register(watchService, new WatchEvent.Kind<?>[]{ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY}, high);
		return watchKey;
	}
	
	// com.sun.nio.file.SensitivityWatchEventModifier isn't a globally support class and may not be available so use reflection
	private static Modifier getHighSensitivityWatchEventModifier() {
		return WatchKeyServiceUtility.getModifierEnum("com.sun.nio.file.SensitivityWatchEventModifier", "HIGH");
	}	
	
	public static boolean isSupported() {
		return getHighSensitivityWatchEventModifier() != null && SystemUtils.IS_OS_MAC;
	}
}
