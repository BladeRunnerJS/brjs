package org.bladerunnerjs.utility.filemodification;

public interface FileModificationInfo {
	long getLastModified();
	void resetLastModified();
}
