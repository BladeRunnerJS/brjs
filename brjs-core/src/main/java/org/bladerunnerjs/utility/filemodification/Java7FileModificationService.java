package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Java7FileModificationService implements FileModificationService, Runnable {
	private final WatchService watchService;
	private final ConcurrentMap<String, Java7FileModificationInfo> fileModificationInfos = new ConcurrentHashMap<>();
	private final PessimisticFileModificationInfo pessimisticFileModificationInfo = new PessimisticFileModificationInfo();
	private final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
	private boolean running = true;
	
	public Java7FileModificationService() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void setRootDir(File rootDir) {
		try {
			initializeWatchers(rootDir.getCanonicalFile(), null);
			new Thread(this).start();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public FileModificationInfo getModificationInfo(File file) {
		FileModificationInfo fileModificationInfo = fileModificationInfos.get(file.getAbsolutePath());
		return (fileModificationInfo != null) ? fileModificationInfo : pessimisticFileModificationInfo;
	}
	
	@Override
	public void close() {
		running = false;
	}
	
	@Override
	public void run() {
		try {
			while(running) {
				for(Java7FileModificationInfo fileModificationInfo : fileModificationInfos.values()) {
					fileModificationInfo.doPoll();
				}
				
				Thread.sleep(100);
			}
			
			for(Java7FileModificationInfo fileModificationInfo : fileModificationInfos.values()) {
				fileModificationInfo.close();
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
	
	private void initializeWatchers(File dir, Java7FileModificationInfo parentModificationInfo) {
		Java7FileModificationInfo fileModificationInfo = new Java7FileModificationInfo(watchService, dir, parentModificationInfo);
		fileModificationInfos.put(dir.getAbsolutePath(), fileModificationInfo);
		
		for(File file : dir.listFiles()) {
			if(file.isDirectory()) {
				initializeWatchers(file, fileModificationInfo);
			}
		}
	}
}
