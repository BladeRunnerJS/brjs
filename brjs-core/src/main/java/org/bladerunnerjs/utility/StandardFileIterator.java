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
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;

public class StandardFileIterator implements FileIterator {
	private final IOFileFilter dirFilter = FileFilterUtils.and(DirectoryFileFilter.INSTANCE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")));
	private final FileModifiedChecker fileModificationChecker;
	private final File dir;
	private final RootNode brjs;
	private List<File> files;
	private List<File> dirs;
	
	public StandardFileIterator(RootNode rootNode, FileModificationInfo fileModificationInfo, File dir) {
		this.brjs = rootNode;
		this.dir = dir;
		fileModificationChecker = new InfoFileModifiedChecker(fileModificationInfo);
	}
	
	public void refresh() {
		files = null;
		dirs = null;
	}
	
	@Override
	public List<File> files() {
		updateIfChangeDetected();
		return files;
	}
	
	@Override
	public List<File> files(IOFileFilter fileFilter) {
		List<File> filteredFiles = new ArrayList<>();
		
		for(File file : files()) {
			if(fileFilter.accept(file)) {
				filteredFiles.add(file);
			}
		}
		
		return filteredFiles;
	}
	
	@Override
	public List<File> dirs() {
		updateIfChangeDetected();
		
		if(dirs == null) {
			dirs = files(dirFilter);
		}
		
		return dirs;
	}
	
	@Override
	public List<File> nestedFiles() {
		List<File> nestedFiles = new ArrayList<>();
		populateNestedFiles(this, nestedFiles, brjs);
		return nestedFiles;
	}
	
	private void updateIfChangeDetected() {
		if((fileModificationChecker.hasChangedSinceLastCheck()) || (files == null)) {
			// TODO: see if this guard can be removed (it currently prevents an exception that can be seen when running gradle testJava, but that doesn't fail the build)
			// once we've added support for automatically adding and removing watchers and file iterators as directories come and go
			if(dir.exists()) {
				files = Arrays.asList(dir.listFiles());
				dirs = null;
				Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
			}
		}
	}
	
	private static void populateNestedFiles(FileIterator fileIterator, List<File> nestedFiles, RootNode rootNode) {
		nestedFiles.addAll(fileIterator.files());
		
		for(File dir : fileIterator.dirs()) {
			populateNestedFiles(rootNode.getFileInfo(dir), nestedFiles, rootNode);
		}
	}
}
