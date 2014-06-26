package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
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
	private String canonicalPath = null;
	
	public StandardFileInfo(File file, BRJS brjs, FileModificationInfo fileModificationInfo) {
		this.fileModificationInfo = fileModificationInfo;
		fileIterator = new StandardFileIterator(brjs, fileModificationInfo, file);
		this.file = file;
		isDirectoryChecker = new InfoFileModifiedChecker(fileModificationInfo);
		existsChecker = new InfoFileModifiedChecker(fileModificationInfo);
		
		try {
			canonicalPath = file.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
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
	public String canonicalPath() {
		
		return canonicalPath;
	}
	
	@Override
	public long getLastModified() {
		return fileModificationInfo.getLastModified();
	}
	
	@Override
	public void resetLastModified() {
		fileModificationInfo.resetLastModified();
	}
	
	@Override
	public List<File> filesAndDirs() {
		return fileIterator.filesAndDirs();
	}
	
	@Override
	public List<File> filesAndDirs(IOFileFilter fileFilter) {
		return fileIterator.filesAndDirs(fileFilter);
	}
	
	@Override
	public List<File> files() {
		return fileIterator.files();
	}
	
	@Override
	public List<File> dirs() {
		return fileIterator.dirs();
	}
	
	@Override
	public List<File> nestedFilesAndDirs() {
		return fileIterator.nestedFilesAndDirs();
	}
	
	@Override
	public List<File> nestedFiles() {
		return fileIterator.nestedFiles();
	}
	
	@Override
	public List<File> nestedDirs() {
		return fileIterator.nestedDirs();
	}
}
