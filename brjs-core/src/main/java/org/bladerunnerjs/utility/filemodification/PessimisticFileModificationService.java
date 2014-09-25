package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;

public class PessimisticFileModificationService implements FileModificationService {
	Map<String, FileModificationInfo> fileModificationInfos = new HashMap<>();
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		// do nothing
	}
	
	@Override
	public FileModificationInfo getFileModificationInfo(File file) {
		String filePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(filePath)) {
			fileModificationInfos.put(filePath, new PessimisticFileModificationInfo(file));
		}
		
		return fileModificationInfos.get(filePath);
	}
	
	@Override
	public FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile) {
		return getFileModificationInfo(file);
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
