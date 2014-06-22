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
import org.bladerunnerjs.model.BRJS;

public class Java7FileModificationService implements FileModificationService, Runnable {
	public static final String THREAD_IDENTIFIER = "file-modification-service";
	
	private final WatchService watchService;
	private final Map<String, ProxyFileModificationInfo> fileModificationInfos = new ConcurrentHashMap<>();
	private final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
	private boolean running = true;
	private File rootDir;
	private final Logger logger;
	
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
			this.rootDir = rootDir;
			watchDirectory(rootDir.getCanonicalFile(), null, new Date().getTime());
			new Thread(this).start();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ProxyFileModificationInfo getModificationInfo(File file) {
		String absoluteFilePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(absoluteFilePath)) {
			fileModificationInfos.put(absoluteFilePath, new ProxyFileModificationInfo(this));
		}
		
		return fileModificationInfos.get(absoluteFilePath);
	}
	
	@Override
	public void close() {
		running = false;
	}
	
	@Override
	public void run() {
		try {
			Thread.currentThread().setName(THREAD_IDENTIFIER);
			
			while(running) {
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
	}
	
	void watchDirectory(File file, WatchingFileModificationInfo parentModificationInfo, long lastModified) {
		ProxyFileModificationInfo proxyFMI = getModificationInfo(file);
		WatchingFileModificationInfo fileModificationInfo = (file.isDirectory()) ? new Java7DirectoryModificationInfo(this, watchService, file, parentModificationInfo) :
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
}
