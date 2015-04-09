package org.bladerunnerjs.api.model.exception.modelupdate;

/**
 * This is the superclass for 
 * @see org.bladerunnerjs.api.model.exception.modelupdate.DirectoryAlreadyExistsModelException
 * @see org.bladerunnerjs.api.model.exception.modelupdate.NoSuchDirectoryException
*/ 

public class ModelUpdateException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ModelUpdateException(String message) {
		super(message);
	}

	public ModelUpdateException(Throwable e) {
		super(e);
	}
	
	public ModelUpdateException(String message, Throwable e) {
		super(message + "; " + e.getMessage(), e);
	}
}