package org.bladerunnerjs.plugin.utility.filechange;

import java.io.File;

// TODO: although Mac definitely can't use a directory watcher, we may find that Windows, Linux and Solaris can, while still being accurate
public class AccurateFileObserverFactory implements FileObserverFactory {
	private final DirectoryObserver pessimisticDirectoryObserver = new PessimisticDirectoryObserver();
	
	@Override
	public FileObserver createFileObserver(File file) {
		return new ProbingFileObserver(file);
	}
	
	@Override
	public DirectoryObserver createDirectoryObserver(File dir) {
		return pessimisticDirectoryObserver;
	}
}
