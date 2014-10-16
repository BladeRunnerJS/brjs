package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.BRJS;

import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.FileUtility;

/**
 * A file modification service used by spec tests. 
 *
 */
public class SpecTestFileModificationService implements FileModificationService {
	
	protected Map<String, FileModificationInfo> fileModificationInfos = new HashMap<>();
	private File rootDir;
	private TimeAccessor timeAccessor;
	private RootNode rootNode;
	
	
	public void initialise(File rootDir, TimeAccessor timeAccessor, RootNode rootNode) {
		this.rootDir = FileUtility.getCanonicalFile(rootDir.getParentFile());
		this.timeAccessor = timeAccessor;
		this.rootNode = rootNode;
	}

	@Override
	public FileModificationInfo getFileModificationInfo(File file) {
		return getFileModificationInfoForCanonicalisedFile( FileUtility.getCanonicalFileWhenPossible(file) );
	}
	
	@Override
	public FileModificationInfo getFileSetModificationInfo(File file, File primarySetFile) {
		String fileKey = file.getPath() + ":" + primarySetFile.getPath();
		
		if(!fileModificationInfos.containsKey(fileKey)) {
			FileModificationInfo fileModificationInfo;
			
			if(file.equals(primarySetFile)) {
				fileModificationInfo = new PrimaryFileModificationInfo( ((BRJS)rootNode).getFileInfoAccessor().getFileInfo(primarySetFile), getFileModificationInfo(primarySetFile));
			}
			else {
				fileModificationInfo = new SecondaryFileModificationInfo((PrimaryFileModificationInfo) getFileSetModificationInfo(primarySetFile, primarySetFile), file, getFileModificationInfo(file));
			}
			
			fileModificationInfos.put(fileKey, fileModificationInfo);
		}
		
		return fileModificationInfos.get(fileKey);
	}
	
	@Override
	public void close() {
		// do nothing
	}
	
	private FileModificationInfo getFileModificationInfoForCanonicalisedFile(File file) {
		String filePath = file.getAbsolutePath();
		
		if(!fileModificationInfos.containsKey(filePath)) {
			FileModificationInfo parentInfo = (file.equals(rootDir)) ? null : getFileModificationInfoForCanonicalisedFile(file.getParentFile());
			fileModificationInfos.put(filePath, new PessimisticFileModificationInfo(file, parentInfo, timeAccessor));
		}
		
		return fileModificationInfos.get(filePath);
	}
	
}
