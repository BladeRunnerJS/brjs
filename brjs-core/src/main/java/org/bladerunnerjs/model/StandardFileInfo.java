package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.utility.StandardFileIterator;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;

public class StandardFileInfo implements FileInfo {
	private final FileModificationInfo fileModificationInfo;
	private final StandardFileIterator fileIterator;
	private final File file;
	private final FileModifiedChecker isDirectoryChecker;
	private final FileModifiedChecker existsChecker;
	private boolean isDirectory;
	private boolean exists;
	
	public StandardFileInfo(File file, BRJS brjs, FileModificationInfo fileModificationInfo) {
		this.fileModificationInfo = fileModificationInfo;
		fileIterator = new StandardFileIterator(brjs, fileModificationInfo, file);
		this.file = file;
		isDirectoryChecker = new InfoFileModifiedChecker(fileModificationInfo);
		existsChecker = new InfoFileModifiedChecker(fileModificationInfo);
	}
	
	@Override
	public boolean isDirectory() {
		if(isDirectoryChecker.hasChangedSinceLastCheck()) {
			isDirectory = file.isDirectory();
		}
		
		return isDirectory;
	}
	
	@Override
	public boolean exists() {
		if(existsChecker.hasChangedSinceLastCheck()) {
			exists = file.exists();
		}
		
		return exists;
	}
	
	@Override
	public long getLastModified() {
		return fileModificationInfo.getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		fileModificationInfo.resetLastModified();
		fileIterator.refresh(); // TODO: we shouldn't need a separate refresh mechanism since the interator should be using the FMI to determine when to recalculate stuff
	}
	
	@Override
	public List<File> files() {
		return fileIterator.files();
	}
	
	@Override
	public List<File> files(IOFileFilter fileFilter) {
		return fileIterator.files(fileFilter);
	}
	
	@Override
	public List<File> dirs() {
		return fileIterator.dirs();
	}
	
	@Override
	public List<File> nestedFiles() {
		return fileIterator.nestedFiles();
	}
}
