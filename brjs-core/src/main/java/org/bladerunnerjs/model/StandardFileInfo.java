package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;

public class StandardFileInfo implements FileInfo {
	private final FileModificationInfo fileModificationInfo;
	private final FileIterator fileIterator;
	private final List<File> emptyFiles = new ArrayList<>(); // TODO: don't return empty files if we don't have to
	private boolean isDirectory;
	
	public StandardFileInfo(File file, BRJS brjs, FileModificationInfo fileModificationInfo) {
		this.fileModificationInfo = fileModificationInfo;
		isDirectory = file.isDirectory();
		fileIterator = (isDirectory) ? new FileIterator(brjs, fileModificationInfo, file) : null;
	}
	
	@Override
	public boolean isDirectory() {
		return isDirectory;
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
	public List<File> files() {
		return (fileIterator == null) ? emptyFiles : fileIterator.files();
	}
	
	@Override
	public List<File> files(IOFileFilter fileFilter) {
		return (fileIterator == null) ? emptyFiles : fileIterator.files(fileFilter);
	}
	
	@Override
	public List<File> dirs() {
		return (fileIterator == null) ? emptyFiles : fileIterator.dirs();
	}
	
	@Override
	public List<File> nestedFiles() {
		return (fileIterator == null) ? emptyFiles : fileIterator.nestedFiles();
	}
}
