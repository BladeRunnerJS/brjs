package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PessimisticFileModificationInfo implements FileModificationInfo {
	private final File file;
	private long lastModified;
	private TimeAccessor timeAccessor;
	private String prevMd5Sum;
	private boolean filePreviouslyExisted = false;
	
	public PessimisticFileModificationInfo(File file, TimeAccessor timeAccessor) {
		this.file = file;
		this.timeAccessor = timeAccessor;
		resetLastModified();
	}
	
	@Override
	public long getLastModified() {
		if(file.exists()) {
			filePreviouslyExisted = true;
			
			if(file.isDirectory()) {
				resetLastModified();
			}
			else {
				String md5Sum = getMd5Sum();
				
				if(!md5Sum.equals(prevMd5Sum)) {
					resetLastModified();
				}
				
				prevMd5Sum = md5Sum;
			}
		}
		else if(filePreviouslyExisted) {
			filePreviouslyExisted = false;
			resetLastModified();
		}
		
		return lastModified;
	}
	
	@Override
	public void resetLastModified() {
		lastModified = timeAccessor.getTime();
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
