package org.bladerunnerjs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bladerunnerjs.plugin.utility.filechange.DirectoryObserver;
import org.bladerunnerjs.plugin.utility.filechange.FileObserverFactory;

public class FileIterator {
	private final IOFileFilter dirFilter = FileFilterUtils.and(DirectoryFileFilter.INSTANCE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")));
	private final DirectoryObserver directoryObserver;
	private final File dir;
	private List<File> files;
	
	public FileIterator(FileObserverFactory fileObserverFactory, File dir) {
		this.dir = dir;
		directoryObserver = fileObserverFactory.createDirectoryObserver(dir);
	}
	
	public void refresh() {
		files = null;
	}
	
	public List<File> files() {
		if((directoryObserver.hasChangedSinceLastCheck()) || (files == null)) {
			files = Arrays.asList(dir.listFiles());
			Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
		}
		
		return files;
	}
	
	public List<File> files(IOFileFilter fileFilter) {
		List<File> filteredFiles = new ArrayList<>();
		
		for(File file : files()) {
			if(fileFilter.accept(file)) {
				filteredFiles.add(file);
			}
		}
		
		return filteredFiles;
	}
	
	public List<File> dirs() {
		return files(dirFilter);
	}
}
