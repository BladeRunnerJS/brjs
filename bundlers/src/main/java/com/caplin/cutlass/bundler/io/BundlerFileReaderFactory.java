package com.caplin.cutlass.bundler.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;

import org.bladerunnerjs.utility.UnicodeReader;

import com.caplin.cutlass.EncodingAccessor;

public class BundlerFileReaderFactory
{
	public static Reader getBundlerFileReader(File file) throws IOException
	{
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		Reader bundlerFileReader = new UnicodeReader(bufferedInputStream, EncodingAccessor.getDefaultInputEncoding());
		
		return bundlerFileReader;
	}
}
