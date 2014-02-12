package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

public class BundlerFileReaderFactory
{
	public static Reader getBundlerFileReader(File file, String encoding) throws IOException
	{
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		Reader bundlerFileReader = new UnicodeReader(bufferedInputStream, encoding);
		
		return bundlerFileReader;
	}
}
