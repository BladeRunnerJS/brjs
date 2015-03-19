package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;


public class PollingFileModificationObserverThread extends Thread
{

	public static final String THREAD_IDENTIFIER = PollingFileModificationObserverThread.class.getSimpleName();
	
	private File directoryToWatch;
	private FileModificationRegistry fileModificationRegistry;
	private FileAlterationListener fileModificationRegistryAlterationListener = new FileModificationRegistryAlterationListener();
	private FileAlterationMonitor monitor;
	
	public PollingFileModificationObserverThread(BRJS brjs) throws IOException
	{
		this.fileModificationRegistry = brjs.getFileModificationRegistry();
		directoryToWatch = brjs.dir().getUnderlyingFile();
		monitor = new FileAlterationMonitor(1000);
	}
	
	@Override
	public void run()
	{
		Thread.currentThread().setName(THREAD_IDENTIFIER);
		addObserverForDir(monitor, directoryToWatch);
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
    		fileModificationRegistry.incrementChildFileVersions(directory);
    	}
    	public void onDirectoryChange(final File directory) {
    		fileModificationRegistry.incrementChildFileVersions(directory);
		}
    	public void onDirectoryDelete(final File directory) {
    		fileModificationRegistry.incrementChildFileVersions(directory);
    	}
    	public void onFileCreate(final File file) {
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
    	public void onFileChange(final File file) {
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
    	public void onFileDelete(final File file) {
    		fileModificationRegistry.incrementChildFileVersions(file);
    	}
	}
	
}
