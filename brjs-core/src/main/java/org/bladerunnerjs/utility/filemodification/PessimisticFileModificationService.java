package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.FileInfoAccessor;

public class PessimisticFileModificationService implements FileModificationService {
	protected Map<String, FileModificationInfo> fileModificationInfos = new HashMap<>();
	private File rootDir;
	private TimeAccessor timeAccessor;
	protected FileInfoAccessor fileInfoAccessor;
	
	@Override
	public void initialise(File rootDir, TimeAccessor timeAccessor, FileInfoAccessor fileInfoAccessor) {
		this.rootDir = rootDir.getParentFile();
		this.timeAccessor = timeAccessor;
		this.fileInfoAccessor = fileInfoAccessor;
	}
	
	@Override
	public FileModificationInfo getFileModificationInfo(File file) {
		String filePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(filePath)) {
			FileModificationInfo parentInfo = (file.equals(rootDir)) ? null : getFileModificationInfo(file.getParentFile());
			fileModificationInfos.put(filePath, new PessimisticFileModificationInfo(file, parentInfo, timeAccessor));
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
