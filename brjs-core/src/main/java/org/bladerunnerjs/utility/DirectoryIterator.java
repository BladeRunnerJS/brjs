package org.bladerunnerjs.utility;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bladerunnerjs.plugin.utility.filechange.DirectoryObserver;
import org.bladerunnerjs.plugin.utility.filechange.FileObserverFactory;

public class DirectoryIterator {
	private final DirectoryObserver directoryObserver;
	private final File dir;
	private List<File> dirs;
	
	public DirectoryIterator(FileObserverFactory fileObserverFactory, File dir) {
		this.dir = dir;
		directoryObserver = fileObserverFactory.createDirectoryObserver(dir);
	}
	
	public List<File> dirs() {
		if((directoryObserver.hasChangedSinceLastCheck()) || (dirs == null)) {
			FileFilter filter = FileFilterUtils.and( DirectoryFileFilter.INSTANCE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")) );
			dirs = Arrays.asList(dir.listFiles(filter));
			Collections.sort(dirs, NameFileComparator.NAME_COMPARATOR);
		}
		
		return dirs;
	}
	
	public void close() {
		directoryObserver.close();
	}
}
