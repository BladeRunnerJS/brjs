package org.bladerunnerjs.model;

import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;

public interface FileInfo extends FileModificationInfo, FileIterator {
	boolean exists();
	boolean isDirectory();
}
