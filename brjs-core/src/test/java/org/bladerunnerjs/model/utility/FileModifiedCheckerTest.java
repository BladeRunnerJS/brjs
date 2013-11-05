package org.bladerunnerjs.model.utility;

import java.io.File;

import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FileModifiedCheckerTest
{

	File file;
	FileModifiedChecker checker;
	
	long t0 = (long) 0;
	long t1 = (long) 1000;
	long t2 = (long) 9999999;
	
	@Before
	public void setup()
	{
		file = mock(File.class);
		checker = new FileModifiedChecker(file);
	}
	
	@Test
	public void firstCheckReturnsTrue()
	{
		assertTrue("first check should be true", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void subsequentChecksReturnFalseIfFileNotChanged()
	{
		when(file.lastModified()).thenReturn(t0);
		assertTrue("first check should be true", checker.fileModifiedSinceLastCheck());
		assertFalse("should be false, modified time not changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void subsequentChecksReturnTrueIfFileChanged()
	{
		when(file.lastModified()).thenReturn(t0);
//		assertTrue("first check should be true", checker.fileModified());
		when(file.lastModified()).thenReturn(t1);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void trueOnlyReturnedWhenFileChanges()
	{
		when(file.lastModified()).thenReturn(t0);
		assertTrue("first check should be true", checker.fileModifiedSinceLastCheck());
		assertFalse("should be false, modified time not changed", checker.fileModifiedSinceLastCheck());
		when(file.lastModified()).thenReturn(t1);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
		assertFalse("should be false, modified time not changed", checker.fileModifiedSinceLastCheck());
		when(file.lastModified()).thenReturn(t2);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
	}
	
	
}
