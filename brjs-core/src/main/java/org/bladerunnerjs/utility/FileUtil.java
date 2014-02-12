package org.bladerunnerjs.utility;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class FileUtil {
	private final String characterEncoding;
	
	public FileUtil(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	
	public void write(File file, String content) throws IOException {
		write(file, content, false);
	}
	
	public void write(File file, String content, boolean append) throws IOException {
		FileUtils.write(file, content, characterEncoding, append);
	}
	
	public String readFileToString(File file) throws IOException {
		String fileContents = null;
		
		try(Reader reader = new UnicodeReader(new BufferedInputStream(new FileInputStream(file)), characterEncoding)) {
			fileContents = IOUtils.toString(reader);
		}
		
		return fileContents;
	}
	
	public void writeStringToFile(File file, String content) throws IOException {
		FileUtils.write(file, content, characterEncoding);
	}
}
