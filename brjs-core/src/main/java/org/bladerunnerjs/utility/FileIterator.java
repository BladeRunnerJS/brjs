package org.bladerunnerjs.utility;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;

public interface FileIterator {
	List<File> files();
	List<File> files(IOFileFilter fileFilter);
	List<File> dirs();
	List<File> nestedFiles();
}