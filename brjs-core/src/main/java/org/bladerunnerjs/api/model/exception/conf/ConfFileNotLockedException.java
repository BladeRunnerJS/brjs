package org.bladerunnerjs.api.model.exception.conf;

import java.io.File;

/**
 * Thrown when a configuration file is being written to without being locked beforehand. 
*/ 

public class ConfFileNotLockedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ConfFileNotLockedException(File appConfFile) {
		super("attempt to write to '" + appConfFile.getPath() + "' without first locking the file");
	}
}
