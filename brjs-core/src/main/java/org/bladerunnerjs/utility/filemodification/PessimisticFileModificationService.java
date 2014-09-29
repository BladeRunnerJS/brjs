package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;

public class PessimisticFileModificationService implements FileModificationService {
	protected Map<String, FileModificationInfo> fileModificationInfos = new HashMap<>();
	private TimeAccessor timeAccessor;
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		timeAccessor = brjs.getTimeAccessor();
	}
	
	@Override
	public FileModificationInfo getFileModificationInfo(File file) {
		String filePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(filePath)) {
			fileModificationInfos.put(filePath, new PessimisticFileModificationInfo(file, timeAccessor));
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
