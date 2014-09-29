package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;

public class OptimisticFileModificationService implements FileModificationService {
	OptimisticFileModificationInfo optimisticFileModificationInfo;
	List<FileModificationInfo> fileModificationInfoSet = new ArrayList<>();
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		optimisticFileModificationInfo = new OptimisticFileModificationInfo(brjs.getTimeAccessor());
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
