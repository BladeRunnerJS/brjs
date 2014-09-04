package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;

public class ReadWriteCompatiblePessimisticFileModificationService extends PessimisticFileModificationService {
	private BRJS brjs;
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		super.initialise(brjs, rootDir);
		this.brjs = brjs;
	}
	
	@Override
	public List<FileModificationInfo> getModificationInfoSet(File[] files) {
		List<FileModificationInfo> modificationInfoSet = new ArrayList<>();
		PrimaryFileModificationInfo primaryFileModificationInfo = null;
		boolean isFirstFile = true;
		
		for(File file : files) {
			if(isFirstFile) {
				isFirstFile = false;
				primaryFileModificationInfo = new PrimaryFileModificationInfo(brjs.getFileInfo(file), getModificationInfo(file));
				modificationInfoSet.add(primaryFileModificationInfo);
			}
			else {
				modificationInfoSet.add(new SecondaryFileModificationInfo(primaryFileModificationInfo, file, getModificationInfo(file)));
			}
		}
		
		return modificationInfoSet;
	}
}
