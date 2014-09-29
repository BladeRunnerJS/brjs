package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.FileInfoAccessor;

public class PessimisticFileModificationService implements FileModificationService {
	protected Map<String, FileModificationInfo> fileModificationInfos = new HashMap<>();
	private TimeAccessor timeAccessor;
	protected FileInfoAccessor fileInfoAccessor;
	
	@Override
	public void initialise(File rootDir, TimeAccessor timeAccessor, FileInfoAccessor fileInfoAccessor) {
		this.timeAccessor = timeAccessor;
		this.fileInfoAccessor = fileInfoAccessor;
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
