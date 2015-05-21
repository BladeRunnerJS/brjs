package org.bladerunnerjs.api;

public class FileObserverMessages
{
	public static final String FILE_CHANGED_MSG = "%s detected a '%s' event for '%s'. Incrementing the file version.";
	public static final String NEW_DIRECTORY_EVENT = "NEW_DIRECTORY";
	public static final String CHANGE_DIRECTORY_EVENT = "CHANGE_DIRECTORY";
	public static final String DELETE_DIRECTORY_EVENT = "DELETE_DIRECTORY";
	public static final String CREATE_FILE_EVENT = "CREATE_FILE";
	public static final String CHANGE_FILE_EVENT = "CHANGE_FILE";
	public static final String DELETE_FILE_EVENT = "DELETE_FILE";
}
