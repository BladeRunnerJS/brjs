package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;

public class Java7FileModificationService implements FileModificationService, Runnable {
	private enum Status {
		RUNNING, STOPPING, STOPPED
	}
	
	public static final String THREAD_IDENTIFIER = "file-modification-service";
	
	private final WatchService watchService;
	private final Map<String, ProxyFileModificationInfo> fileModificationInfos = new ConcurrentHashMap<>();
	private final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
	
	private Status status = Status.RUNNING;
	private File rootDir;
	private final Logger logger;

	private BRJS brjs;
	
	public Java7FileModificationService(LoggerFactory loggerFactory) {
		try {
			logger = loggerFactory.getLogger(getClass());
			watchService = FileSystems.getDefault().newWatchService();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public File getRootDir() {
		return rootDir;
	}
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		try {
			this.brjs = brjs;
			this.rootDir = rootDir;
			watchDirectory(rootDir.getCanonicalFile(), null, new Date().getTime());
			brjs.addObserver( NodeReadyEvent.class, new FileModificationServiceNodeReadyObserver() );
			new Thread(this).start();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ProxyFileModificationInfo getFileModificationInfo(File file) {
		String absoluteFilePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(absoluteFilePath)) {
			fileModificationInfos.put(absoluteFilePath, new ProxyFileModificationInfo(this));
		}
		
		return fileModificationInfos.get(absoluteFilePath);
	}
	
	@Override
	public FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile) {
		return getFileModificationInfo(file);
	}
	
	@Override
	public void close() {
		if(status == Status.RUNNING) {
			status = Status.STOPPING;
			
			do {
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			} while(status == Status.STOPPING);
		}
	}
	
	@Override
	public void run() {
		try {
			Thread.currentThread().setName(THREAD_IDENTIFIER);
			
			while(status == Status.RUNNING) {
				for(ProxyFileModificationInfo fileModificationInfo : fileModificationInfos.values()) {
					fileModificationInfo.pollWatchEvents();
				}
				
				Thread.sleep(100);
			}
			
			for(ProxyFileModificationInfo fileModificationInfo : fileModificationInfos.values()) {
				fileModificationInfo.closeWatchListener();
			}
			
			// TODO: Waiting on Java bug fix http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8029516, github issue #385
			if(!isWindows) {
				 watchService.close();
			}
		}
		catch(InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			status = Status.STOPPED;
		}
	}
	
	void watchDirectory(File file, WatchingFileModificationInfo parentModificationInfo, long lastModified) {
		ProxyFileModificationInfo proxyFMI = getFileModificationInfo(file);
		WatchingFileModificationInfo fileModificationInfo = (file.isDirectory()) ? new Java7DirectoryModificationInfo(brjs, this, watchService, file, parentModificationInfo) :
			new Java7FileModificationInfo(parentModificationInfo, file);
		proxyFMI.setFileModificationInfo(fileModificationInfo);
		
		if(file.isDirectory()) {
			for(File childFile : file.listFiles()) {
				watchDirectory(childFile, fileModificationInfo, lastModified);
			}
		}
	}

	public Logger getLogger() {
		return logger;
	}
	
	private class FileModificationServiceNodeReadyObserver implements EventObserver {

		@Override
		public void onEventEmitted(Event event, Node node)
		{
			if (node instanceof App || node instanceof AssetContainer) {
    			File resetLastModifiedForFile = node.parentNode().dir();
				FileModificationInfo fileModificationInfo = getFileModificationInfo(resetLastModifiedForFile);
    			fileModificationInfo.resetLastModified();
			}
		}
		
	}
}
