package org.bladerunnerjs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.plugin.utility.filechange.DirectoryObserver;
import org.bladerunnerjs.plugin.utility.filechange.FileObserverFactory;

public class FileIterator {
	private final DirectoryObserver directoryObserver;
	private final File dir;
	private final IOFileFilter fileFilter;
	private List<File> files;
	
	public FileIterator(FileObserverFactory fileObserverFactory, File dir, IOFileFilter fileFilter) {
		this.dir = dir;
		this.fileFilter = fileFilter;
		directoryObserver = fileObserverFactory.createDirectoryObserver(dir);
	}
	
	public List<File> files() {
		if((directoryObserver.hasChangedSinceLastCheck()) || (files == null)) {
			files = new ArrayList<File>(FileUtils.listFiles(dir, fileFilter, FalseFileFilter.INSTANCE));
			Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
		}
		
		return files;
	}
}
