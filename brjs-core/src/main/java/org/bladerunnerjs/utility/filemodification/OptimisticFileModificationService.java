package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;

public class OptimisticFileModificationService implements FileModificationService {
	OptimisticFileModificationInfo optimisticFileModificationInfo = new OptimisticFileModificationInfo();
	List<FileModificationInfo> fileModificationInfoSet = new ArrayList<>();
	
	{
		fileModificationInfoSet.add(optimisticFileModificationInfo);
	}
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		// do nothing
	}
	
	@Override
	public FileModificationInfo getModificationInfo(File file) {
		return optimisticFileModificationInfo;
	}
	
	@Override
	public List<FileModificationInfo> getModificationInfoSet(File[] files) {
		return fileModificationInfoSet;
	}
	
	@Override
	public void close() {
		// do nothing
	}
}
