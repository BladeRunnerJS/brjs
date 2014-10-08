package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.FileInfoAccessor;

/**
 * 
 *
 */
public class ReadWriteCompatiblePessimisticFileModificationService extends PessimisticFileModificationService {
	@Override
	public void initialise(File rootDir, TimeAccessor timeAccessor, FileInfoAccessor fileInfoAccessor) {
		super.initialise(rootDir, timeAccessor, fileInfoAccessor);
	}
	
	@Override
	public FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile) {
		String fileKey = file.getPath() + ":" + primarySetFile.getPath();
		
		if(!fileModificationInfos.containsKey(fileKey)) {
			FileModificationInfo fileModificationInfo;
			
			if(file.equals(primarySetFile)) {
				fileModificationInfo = new PrimaryFileModificationInfo(fileInfoAccessor.getFileInfo(primarySetFile), getFileModificationInfo(primarySetFile));
			}
			else {
				fileModificationInfo = new SecondaryFileModificationInfo((PrimaryFileModificationInfo) getFileSetModificationInfo(primarySetFile, primarySetFile), file, getFileModificationInfo(file));
			}
			
			fileModificationInfos.put(fileKey, fileModificationInfo);
		}
		
		return fileModificationInfos.get(fileKey);
	}
}
