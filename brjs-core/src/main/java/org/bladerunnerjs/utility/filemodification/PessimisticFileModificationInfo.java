package org.bladerunnerjs.utility.filemodification;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

public class PessimisticFileModificationInfo implements FileModificationInfo {
	private final File file;
	private long lastModified = 0;
	private long prevFileLastModified = 0;
	private String prevMd5Sum;
	
	public PessimisticFileModificationInfo(File file) {
		this.file = file;
	}
	
	@Override
	public long getLastModified() {
		if(file.exists() && file.isFile()) {
			long fileLastModified = file.lastModified();
			
			if(fileLastModified != prevFileLastModified) {
				String md5Sum = getMd5Sum();
				
				if(!md5Sum.equals(prevMd5Sum)) {
					lastModified = new Date().getTime();
				}
				
				prevMd5Sum = md5Sum;
			}
			prevFileLastModified = fileLastModified;
		}
		else {
			// if it's a directory or the file doesn't exist then assume the worst, but only increment from a very low number, so if
			// a MemoizedValue also contains any explicit file references, then change in these will dictate whether we update or not
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
