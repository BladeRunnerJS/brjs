package org.bladerunnerjs.api;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent.Kind;

public class FileObserverMessages
{
	public static final String FILE_CHANGED_MSG = "%s detected a '%s' event for '%s'. Incrementing the file version.";
	public static final String CREATE_DIRECTORY_EVENT = "NEW_DIRECTORY";
	public static final String CHANGE_DIRECTORY_EVENT = "CHANGE_DIRECTORY";
	public static final String DELETE_DIRECTORY_EVENT = "DELETE_DIRECTORY";
	public static final String CREATE_FILE_EVENT = "CREATE_FILE";
	public static final String CHANGE_FILE_EVENT = "CHANGE_FILE";
	public static final String DELETE_FILE_EVENT = "DELETE_FILE";
	
	public static String eventMessage(Kind<?> event, File file) {
		if (file.isDirectory()) {
			if (event == StandardWatchEventKinds.ENTRY_CREATE) {
				return CREATE_DIRECTORY_EVENT;
			} else if (event == StandardWatchEventKinds.ENTRY_DELETE) {
				return DELETE_DIRECTORY_EVENT;
			} else if (event == StandardWatchEventKinds.ENTRY_MODIFY) {
				return CHANGE_DIRECTORY_EVENT;
			}
		} else {
			if (event == StandardWatchEventKinds.ENTRY_CREATE) {
				return CREATE_FILE_EVENT;
			} else if (event == StandardWatchEventKinds.ENTRY_DELETE) {
				return DELETE_FILE_EVENT;
			} else if (event == StandardWatchEventKinds.ENTRY_MODIFY) {
				return CHANGE_FILE_EVENT;
			}
		}
		return "UNKNOWN";
	}
}
