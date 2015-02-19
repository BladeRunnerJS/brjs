package org.bladerunnerjs.api.model.exception.conf;

/**
 * Thrown when the locking of a configuration file is being attempted when it has already been locked beforehand. 
*/ 

public class ConfFileAlreadyLockedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
}
