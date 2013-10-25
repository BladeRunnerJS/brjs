package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

public class JsSourceFileFinderTestHelper 
{
	
	public static void assertClassnameFileMappingsEquals(List<ClassnameFileMapping> expected, List<ClassnameFileMapping> actual) 
	{
		for (int i = 0; i < Math.max(expected.size(), actual.size()); i++) 
		{
			ClassnameFileMapping expectedMapping = (i < expected.size()) ? expected.get(i) : null;
			ClassnameFileMapping actualMapping = (i < actual.size()) ? actual.get(i) : null;
			assertEquals( expectedMapping.getClassname(), actualMapping.getClassname() );
			assertEquals( expectedMapping.getFile(), actualMapping.getFile() );
		}
	}
	
	public static void assertFilesSameAsClassMapping(List<File> expected, List<ClassnameFileMapping> actual) 
	{
		for (int i = 0; i < Math.max(expected.size(), actual.size()); i++) 
		{
			File expectedFile = (i < expected.size()) ? expected.get(i) : null;
			File actualFile = (i < actual.size()) ? actual.get(i).getFile() : null;
			assertEquals( i+"", expectedFile, actualFile );
		}
	}
	
	public static void assertFilesEquals(List<File> expected, List<File> actual) 
	{
		for (int i = 0; i < Math.max(expected.size(), actual.size()); i++) 
		{
			File expectedFile = (i < expected.size()) ? expected.get(i) : null;
			File actualFile = (i < actual.size()) ? actual.get(i) : null;
			assertEquals( i+"", expectedFile, actualFile );
		}
	}
}
