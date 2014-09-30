package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.FileInfoAccessor;

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
	private TimeAccessor timeAccessor;

	private FileInfoAccessor fileInfoAccessor;

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
	public void initialise(File rootDir, TimeAccessor timeAccessor, FileInfoAccessor fileInfoAccessor) {
		try {
			this.rootDir = rootDir;
			this.timeAccessor = timeAccessor;
			this.fileInfoAccessor = fileInfoAccessor;
			watchDirectory(rootDir.getCanonicalFile(), null, timeAccessor.getTime());
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
			WatchingFileModificationInfo parent = (file.equals(rootDir)) ? null : getFileModificationInfo(file.getParentFile());
			fileModificationInfos.put(absoluteFilePath, new ProxyFileModificationInfo(this, parent, timeAccessor));
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
		WatchingFileModificationInfo fileModificationInfo = (file.isDirectory()) ? new Java7DirectoryModificationInfo(this, watchService, file, parentModificationInfo, timeAccessor, fileInfoAccessor) :
			new Java7FileModificationInfo(parentModificationInfo, file, timeAccessor);
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
