package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PessimisticFileModificationInfo implements FileModificationInfo {
	private final File file;
	private long lastModified = 0;
	private String prevMd5Sum;
	private boolean filePreviouslyExisted = false;
	
	public PessimisticFileModificationInfo(File file) {
		this.file = file;
	}
	
	@Override
	public long getLastModified() {
		if(file.exists()) {
			filePreviouslyExisted = true;
			
			if(file.isDirectory()) {
				++lastModified;
			}
			else {
				String md5Sum = getMd5Sum();
				
				if(!md5Sum.equals(prevMd5Sum)) {
					++lastModified;
				}
				
				prevMd5Sum = md5Sum;
			}
		}
		else if(filePreviouslyExisted) {
			filePreviouslyExisted = false;
			++lastModified;
		}
		
		return lastModified;
	}
	
	@Override
	public void resetLastModified() {
		// do nothing
	}
	
	private String getMd5Sum() {
		String md5Sum;
		
		try(FileInputStream fis = new FileInputStream(file)) {
			md5Sum = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		return md5Sum;
	}
}
