package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;


public class PollingFileModificationObserverThread extends Thread
{

	public static final String THREAD_IDENTIFIER = PollingFileModificationObserverThread.class.getSimpleName();
	public static final String FILE_CHANGED_MSG = THREAD_IDENTIFIER+" detected a '%s' event for '%s'. Incrementing the file version.";
	public static final String THREAD_INIT_MESSAGE = "%s configured with a polling interval of '%s'.";
	
	private List<File> directoriesToWatch;
	private FileModificationRegistry fileModificationRegistry;
	private FileAlterationListener fileModificationRegistryAlterationListener = new FileModificationRegistryAlterationListener();
	private FileAlterationMonitor monitor;
	
	private Logger logger;
	
	public PollingFileModificationObserverThread(BRJS brjs, int interval) throws IOException
	{
		this.fileModificationRegistry = brjs.getFileModificationRegistry();
		directoriesToWatch = new ArrayList<File>();
		directoriesToWatch.add(brjs.dir());
		if (!brjs.appsFolder().getAbsolutePath().startsWith(brjs.dir().getAbsolutePath())) {
			directoriesToWatch.add(brjs.appsFolder());
		}
		monitor = new FileAlterationMonitor(interval);
		logger = brjs.logger(this.getClass());
		logger.debug(THREAD_INIT_MESSAGE, this.getClass().getSimpleName(), interval);
	}
	
	@Override
	public void run()
	{
		Thread.currentThread().setName(THREAD_IDENTIFIER);
		for (File directoryToWatch : directoriesToWatch) {
			addObserverForDir(monitor, directoryToWatch);
		}
        try {
        	monitor.start();
        	while(true) {
        		Thread.sleep(60000);
        	}
        } catch (InterruptedException iEx) {
        	// do nothing
        } catch (Exception ex)
		{
			throw new RuntimeException(ex);
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
    		logger.debug(FILE_CHANGED_MSG, "NEW_DIRECTORY", directory.getPath());
    		fileModificationRegistry.incrementChildFileVersions(directory);
    	}
    	public void onDirectoryChange(final File directory) {
    		logger.debug(FILE_CHANGED_MSG, "CHANGE_DIRECTORY", directory.getPath());
    		fileModificationRegistry.incrementChildFileVersions(directory);
		}
    	public void onDirectoryDelete(final File directory) {
    		logger.debug(FILE_CHANGED_MSG, "DELETE_DIRECTORY", directory.getPath());
    		fileModificationRegistry.incrementChildFileVersions(directory);
    	}
    	public void onFileCreate(final File file) {
    		logger.debug(FILE_CHANGED_MSG, "CREATE_FILE", file.getPath());
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
    	public void onFileChange(final File file) {
    		logger.debug(FILE_CHANGED_MSG, "CHANGE_FILE", file.getPath());
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
    	public void onFileDelete(final File file) {
    		logger.debug(FILE_CHANGED_MSG, "DELETE_FILE", file.getPath());
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
	}
	
}
