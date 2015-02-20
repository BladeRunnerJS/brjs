package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class FileHandleTest {
	@Test
	public void fileTest() throws IOException, InterruptedException {
		FileInputStream fis = new FileInputStream(new File("/home/dominicc/test-file")); // a path to some file on your machine
		
		try(InputStreamReader reader = new InputStreamReader(fis)) {
			int nextChar;
			
			while((nextChar = reader.read()) != -1) {
				System.out.println((char) nextChar);
			}
			
			reader.close(); // does this line cause the file handle to be released?
		}
	}
}
