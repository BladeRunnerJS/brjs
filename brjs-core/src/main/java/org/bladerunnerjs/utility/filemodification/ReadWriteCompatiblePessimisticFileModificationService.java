package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.BRJS;

public class ReadWriteCompatiblePessimisticFileModificationService extends PessimisticFileModificationService {
	private BRJS brjs;
	
	@Override
	public void initialise(BRJS brjs, File rootDir) {
		super.initialise(brjs, rootDir);
		this.brjs = brjs;
	}
	
	@Override
	public FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile) {
		String fileKey = file.getPath() + ":" + primarySetFile.getPath();
		
		if(!fileModificationInfos.containsKey(fileKey)) {
			FileModificationInfo fileModificationInfo;
			
			if(file.equals(primarySetFile)) {
				fileModificationInfo = new PrimaryFileModificationInfo(brjs.getFileInfo(primarySetFile), getFileModificationInfo(primarySetFile));
			}
			else {
				fileModificationInfo = new SecondaryFileModificationInfo((PrimaryFileModificationInfo) getFileSetModificationInfo(primarySetFile, primarySetFile), file, getFileModificationInfo(file));
			}
			
			fileModificationInfos.put(fileKey, fileModificationInfo);
		}
		
		return fileModificationInfos.get(fileKey);
	}
}
