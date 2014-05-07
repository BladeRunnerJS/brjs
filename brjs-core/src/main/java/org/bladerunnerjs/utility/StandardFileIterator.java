package org.bladerunnerjs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;

public class StandardFileIterator implements FileIterator {
	private final IOFileFilter fileFilter = FileFilterUtils.and(FileFileFilter.FILE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")));
	private final IOFileFilter dirFilter = FileFilterUtils.and(DirectoryFileFilter.DIRECTORY, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")));
	private final FileModifiedChecker fileModificationChecker;
	private final File dir;
	private final RootNode brjs;
	private List<File> filesAndDirs;
	private List<File> files;
	private List<File> dirs;
	
	public StandardFileIterator(RootNode rootNode, FileModificationInfo fileModificationInfo, File dir) {
		this.brjs = rootNode;
		this.dir = dir;
		fileModificationChecker = new InfoFileModifiedChecker(fileModificationInfo);
	}
	
	@Override
	public List<File> filesAndDirs() {
		updateIfChangeDetected();
		return filesAndDirs;
	}
	
	@Override
	public List<File> filesAndDirs(IOFileFilter fileFilter) {
		List<File> filteredFiles = new ArrayList<>();
		
		for(File file : filesAndDirs()) {
			if(fileFilter.accept(file)) {
				filteredFiles.add(file);
			}
		}
		
		return filteredFiles;
	}
	
	@Override
	public List<File> files() {
		updateIfChangeDetected();
		
		if(files == null) {
			files = filesAndDirs(fileFilter);
		}
		
		return files;
	}
	
	@Override
	public List<File> dirs() {
		updateIfChangeDetected();
		
		if(dirs == null) {
			dirs = filesAndDirs(dirFilter);
		}
		
		return dirs;
	}
	
	@Override
	public List<File> nestedFilesAndDirs() {
		List<File> nestedFilesAndDirs = new ArrayList<>();
		populateNestedFilesAndDirs(this, nestedFilesAndDirs, brjs);
		return nestedFilesAndDirs;
	}
	
	@Override
	public List<File> nestedFiles() {
		List<File> nestedFiles = new ArrayList<>();
		
		for(File file : nestedFilesAndDirs()) {
			if(!file.isDirectory()) {
				nestedFiles.add(file);
			}
		}
		
		return nestedFiles;
	}
	
	@Override
	public List<File> nestedDirs() {
		List<File> nestedDirs = new ArrayList<>();
		
		for(File file : nestedFilesAndDirs()) {
			if(file.isDirectory()) {
				nestedDirs.add(file);
			}
		}
		
		return nestedDirs;
	}
	
	private void updateIfChangeDetected() {
		if(fileModificationChecker.hasChangedSinceLastCheck()) {
			filesAndDirs = Arrays.asList(dir.listFiles());
			files = null;
			dirs = null;
			Collections.sort(filesAndDirs, NameFileComparator.NAME_COMPARATOR);
		}
	}
	
	private static void populateNestedFilesAndDirs(FileIterator fileIterator, List<File> nestedFilesAndDirs, RootNode rootNode) {
		nestedFilesAndDirs.addAll(fileIterator.filesAndDirs());
		
		for(File dir : fileIterator.dirs()) {
			populateNestedFilesAndDirs(rootNode.getFileInfo(dir), nestedFilesAndDirs, rootNode);
		}
	}
}
