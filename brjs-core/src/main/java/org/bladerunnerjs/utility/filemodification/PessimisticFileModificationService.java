package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;

public class PessimisticFileModificationService implements FileModificationService {
	Map<String, FileModificationInfo> fileModificationInfos = new HashMap<>();
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		// do nothing
	}
	
	@Override
	public FileModificationInfo getModificationInfo(File file) {
		String filePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(filePath)) {
			fileModificationInfos.put(filePath, new PessimisticFileModificationInfo(file));
		}
		
		return fileModificationInfos.get(filePath);
	}
	
	@Override
	public List<FileModificationInfo> getModificationInfoSet(File[] files) {
		List<FileModificationInfo> modificationInfoSet = new ArrayList<>();
		
		for(File file : files) {
			modificationInfoSet.add(getModificationInfo(file));
		}
		
		return modificationInfoSet;
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
