package org.bladerunnerjs.model;

import java.io.File;

public interface FileInfoAccessor {
	FileInfo getFileInfo(File file);	
	FileInfo getFileSetInfo(File file, File primarySetFile);
}
