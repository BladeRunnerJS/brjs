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
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.FileModificationService;

public class FileIterator {
	
	
	private final IOFileFilter dirFilter = FileFilterUtils.and(FastDirectoryFileFilter.INSTANCE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")));
	private final FileModifiedChecker fileModificationChecker;
	private final File dir;
	private final RootNode brjs;
	private List<File> files;
	private List<File> dirs;
	
	public FileIterator(RootNode rootNode, FileModificationService fileModificationService, File dir) {
		this.brjs = rootNode;
		this.dir = dir;
		fileModificationChecker = new InfoFileModifiedChecker(fileModificationService.getModificationInfo(dir));
	}
	
	public void refresh() {
		files = null;
		dirs = null;
	}
	
	public List<File> files() {
		updateIfChangeDetected();
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
		updateIfChangeDetected();
		
		if(dirs == null) {
			dirs = files(dirFilter);
		}
		
		return dirs;
	}
	
	public List<File> nestedFiles() {
		List<File> nestedFiles = new ArrayList<>();
		populateNestedFiles(nestedFiles);
		return nestedFiles;
	}
	
	private void updateIfChangeDetected() {
		if((fileModificationChecker.hasChangedSinceLastCheck()) || (files == null)) {
			files = Arrays.asList(dir.listFiles());
			dirs = null;
			Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
		}
	}
	
	private void populateNestedFiles(List<File> nestedFiles) {
		nestedFiles.addAll(files());
		
		for(File dir : dirs()) {
			brjs.getFileIterator(dir).populateNestedFiles(nestedFiles);
		}
	}
}
