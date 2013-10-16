package com.caplin.cutlass.io;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

@SuppressWarnings("resource")
public class UnicodeReaderTest
{
	private static final String resourceDir = "src/test/resources/UnicodeReaderTest";
	private static final String testFileContents = "test-€";
	
	@Test
	public void testUtf8FileWithoutBOMCanBeReadCorrectly() throws IOException
	{
		UnicodeReader bundlerFileReader = new UnicodeReader(new FileInputStream(new File(resourceDir, "utf8FileWithoutBom.txt")), "UTF-8");
		BufferedReader br = new BufferedReader(bundlerFileReader);
		assertEquals(testFileContents, removeLineEndingSymbols(br.readLine()));
	}
	
	@Test
	public void testUtf8FileWithBOMCanBeReadCorrectly() throws IOException
	{
		UnicodeReader bundlerFileReader = new UnicodeReader(new FileInputStream(new File(resourceDir, "utf8FileWithBom.txt")), "UTF-8");
		BufferedReader br = new BufferedReader(bundlerFileReader);
		assertEquals(testFileContents, removeLineEndingSymbols(br.readLine()));
	}
	
	@Test
	public void testReadingEmptyFileAndReportingCorrectNumberOfBytesRead() throws IOException
	{
		File emptyFile = new File(resourceDir, "emptyFile.txt");
		int buffLength = 8192;
		char[] buff = new char[buffLength];
		
		UnicodeReader bundlerFileReader = new UnicodeReader(new FileInputStream(emptyFile));
		int bytesReadWithBundlerFileReader = bundlerFileReader.read(buff, 0, buffLength);
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(emptyFile));
		int bytesReadWithInputStreamReader = isr.read(buff, 0, buffLength);
		
		assertEquals(bytesReadWithInputStreamReader, bytesReadWithBundlerFileReader);
	}
	
	@Test
	public void testReading1ByteFileAndReportingCorrectNumberOfBytesRead() throws IOException
	{
		File emptyFile = new File(resourceDir, "1byteFile.txt");
		int buffLength = 8192;
		char[] buff = new char[buffLength];
		
		UnicodeReader bundlerFileReader = new UnicodeReader(new FileInputStream(emptyFile));
		int bytesReadWithBundlerFileReader = bundlerFileReader.read(buff, 0, buffLength);
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(emptyFile));
		int bytesReadWithInputStreamReader = isr.read(buff, 0, buffLength);
		
		assertEquals(bytesReadWithInputStreamReader, bytesReadWithBundlerFileReader);
	}
	
	@Test
	public void testReading4ByteFileAndReportingCorrectNumberOfBytesRead() throws IOException
	{
		File emptyFile = new File(resourceDir, "4byteFile.txt");
		int buffLength = 8192;
		char[] buff = new char[buffLength];
		
		UnicodeReader bundlerFileReader = new UnicodeReader(new FileInputStream(emptyFile));
		int bytesReadWithBundlerFileReader = bundlerFileReader.read(buff, 0, buffLength);
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(emptyFile));
		int bytesReadWithInputStreamReader = isr.read(buff, 0, buffLength);
		
		assertEquals(bytesReadWithInputStreamReader, bytesReadWithBundlerFileReader);
	}
	
	private String removeLineEndingSymbols(String s)
	{
		return s.replaceAll("(\\r|\\n)", "");
	}
}
