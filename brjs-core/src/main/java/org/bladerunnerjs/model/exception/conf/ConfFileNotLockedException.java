package org.bladerunnerjs.model.exception.conf;

import java.io.File;

public class ConfFileNotLockedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ConfFileNotLockedException(File appConfFile) {
		super("attempt to write to '" + appConfFile.getPath() + "' without first locking the file");
	}
}
