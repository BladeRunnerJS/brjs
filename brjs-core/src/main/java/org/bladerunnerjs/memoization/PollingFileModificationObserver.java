package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.FileObserver;
import org.bladerunnerjs.api.FileObserverMessages;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;
import org.bladerunnerjs.utility.FileObserverFactory;


public class PollingFileModificationObserver implements FileObserver
{
	public static final String INIT_MESSAGE = "%s configured with a polling interval of '%s'.";
	
	private List<File> directoriesToWatch;
	private FileModificationRegistry fileModificationRegistry;
	private FileAlterationListener fileModificationRegistryAlterationListener = new FileModificationRegistryAlterationListener();
	private FileAlterationMonitor monitor;
	
	private Logger logger;
	private int interval;
	
	public PollingFileModificationObserver(BRJS brjs, int interval) {
		fileModificationRegistry = brjs.getFileModificationRegistry();
		this.directoriesToWatch = FileObserverFactory.getBrjsRootDirs(brjs);
		logger = brjs.logger(this.getClass());
		this.interval = interval;
	}
	
	@Override
	public void start() throws IOException
	{
		if (monitor != null) {
			throw new IllegalStateException(this.getClass().getSimpleName()+" monitor has already been started");
		}
		monitor = new FileAlterationMonitor(interval);
		logger.debug(INIT_MESSAGE, this.getClass().getSimpleName(), interval);
		for (File directoryToWatch : directoriesToWatch) {
			addObserverForDir(monitor, directoryToWatch);
		}
		try {
			monitor.start();
		} catch (Exception ex) {
			throw new IOException(ex);
		}
	}
	
	@Override
	public void stop() throws IOException, InterruptedException
	{
		try
		{
			if (monitor != null) {
				monitor.stop();
				monitor = null;
			}
		}
		catch (Exception ex)
		{
			throw new IOException(ex);
		}
	}
	
	
	private void addObserverForDir(FileAlterationMonitor monitor, File directoryToWatch)
	{
		FileAlterationObserver observer = new FileAlterationObserver(directoryToWatch);
		observer.addListener(fileModificationRegistryAlterationListener);
		monitor.addObserver(observer);
	}
	

	public class FileModificationRegistryAlterationListener extends FileAlterationListenerAdaptor implements FileAlterationListener
	{
    	public void onDirectoryCreate(final File directory) {
    		onChange(FileObserverMessages.NEW_DIRECTORY_EVENT, directory);
    	}
    	public void onDirectoryChange(final File directory) {
    		onChange(FileObserverMessages.CHANGE_DIRECTORY_EVENT, directory);
		}
    	public void onDirectoryDelete(final File directory) {
    		onChange(FileObserverMessages.DELETE_DIRECTORY_EVENT, directory);
    	}
    	public void onFileCreate(final File file) {
    		onChange(FileObserverMessages.CREATE_FILE_EVENT, file);
    	}
    	public void onFileChange(final File file) {
    		onChange(FileObserverMessages.CHANGE_FILE_EVENT, file);
    	}
    	public void onFileDelete(final File file) {
    		onChange(FileObserverMessages.DELETE_FILE_EVENT, file);
    	}
    	public void onChange(String eventMessage, File file) {
    		logger.debug(FileObserverMessages.FILE_CHANGED_MSG, PollingFileModificationObserver.class.getSimpleName(), eventMessage, file.getPath());
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
	}
	
}
