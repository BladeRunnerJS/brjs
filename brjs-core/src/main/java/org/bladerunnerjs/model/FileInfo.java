package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;

public interface FileInfo extends FileModificationInfo, FileIterator {
	boolean exists();
	boolean isDirectory();
	List<File> filesAndDirs();
	List<File> filesAndDirs(IOFileFilter fileFilter);
	List<File> files();
	List<File> dirs();
	List<File> nestedFilesAndDirs();
}
