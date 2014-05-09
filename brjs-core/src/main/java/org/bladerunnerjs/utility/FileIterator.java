package org.bladerunnerjs.utility;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

public interface FileIterator {
	List<File> filesAndDirs();
	List<File> filesAndDirs(IOFileFilter fileFilter);
	List<File> files();
	List<File> dirs();
	List<File> nestedFilesAndDirs();
	List<File> nestedFiles();
	List<File> nestedDirs();
}