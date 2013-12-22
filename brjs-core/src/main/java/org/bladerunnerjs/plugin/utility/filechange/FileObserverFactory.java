package org.bladerunnerjs.plugin.utility.filechange;

import java.io.File;

public interface FileObserverFactory {
	FileObserver createFileObserver(File file);
	DirectoryObserver createDirectoryObserver(File dir);
}
