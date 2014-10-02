package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.utility.filemodification.FileModificationService;

public class BRJSFileInfoAccessor implements FileInfoAccessor {
	private final Map<String, BRJSFileInfo> fileInfos = new TreeMap<>();
	private final LoggerFactory loggerFactory;
	private FileModificationService fileModificationService;
	
	public BRJSFileInfoAccessor(FileModificationService fileModificationService, LoggerFactory loggerFactory) {
		this.fileModificationService = fileModificationService;
		this.loggerFactory = loggerFactory;
	}
	
	@Override
	public FileInfo getFileInfo(File file) {
		String filePath = file.getPath();
		
		if(!fileInfos.containsKey(filePath)) {
			fileInfos.put(filePath, new BRJSFileInfo(file, fileModificationService, this, loggerFactory));
		}
		
		return fileInfos.get(filePath);
	}
	
	@Override
	public FileInfo getFileSetInfo(File file, File primarySetFile) {
		String filePathsIdentifier = file.getPath() + ":" + primarySetFile.getPath();
		
		if(!fileInfos.containsKey(filePathsIdentifier)) {
			fileInfos.put(filePathsIdentifier, new BRJSFileInfo(file, primarySetFile, fileModificationService, this, loggerFactory));
		}
		
		return fileInfos.get(filePathsIdentifier);
	}
	
	public void setFileModificationService(FileModificationService fileModificationService) {
		this.fileModificationService = fileModificationService;
		
		// TODO: find out why we we get a ConcurrentModificationException if we don't duplicate fileInfos.values() 
		for(BRJSFileInfo fileInfo : new ArrayList<>(fileInfos.values())) {
			fileInfo.reset(fileModificationService);
		}
	}
}
