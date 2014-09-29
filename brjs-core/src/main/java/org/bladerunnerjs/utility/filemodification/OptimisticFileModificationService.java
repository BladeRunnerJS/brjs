package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.FileInfoAccessor;

public class OptimisticFileModificationService implements FileModificationService {
	OptimisticFileModificationInfo optimisticFileModificationInfo;
	List<FileModificationInfo> fileModificationInfoSet = new ArrayList<>();
	
	@Override
	public void initialise(File rootDir, TimeAccessor timeAccessor, FileInfoAccessor fileInfoAccessor) {
		optimisticFileModificationInfo = new OptimisticFileModificationInfo(timeAccessor);
		fileModificationInfoSet.add(optimisticFileModificationInfo);
	}
	
	@Override
	public FileModificationInfo getFileModificationInfo(File file) {
		return optimisticFileModificationInfo;
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
