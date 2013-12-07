package org.bladerunnerjs.model.utility;

import java.io.File;

import org.bladerunnerjs.utility.FileModifiedChecker;
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
	
	long size0 = (long) 0;
	long size1 = (long) 1000;
	long size2 = (long) 9999999;
	
	
	
	@Before
	public void setup()
	{
		file = mock(File.class);
		checker = new FileModifiedChecker(file);
	}
	
	@Test
	public void firstCheckReturnsTrue()
	{
		when(file.lastModified()).thenReturn(t0);
		assertTrue("first check should be true", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void subsequentChecksReturnFalseIfFileNotChanged()
	{
		firstCheckReturnsTrue();
		assertFalse("should be false, modified time not changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void subsequentChecksReturnTrueIfFileChanged()
	{
		firstCheckReturnsTrue();
		when(file.lastModified()).thenReturn(t1);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void trueOnlyReturnedWhenFileChanges()
	{
		firstCheckReturnsTrue();
		assertFalse("should be false, modified time not changed", checker.fileModifiedSinceLastCheck());
		when(file.lastModified()).thenReturn(t1);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
		assertFalse("should be false, modified time not changed", checker.fileModifiedSinceLastCheck());
		when(file.lastModified()).thenReturn(t2);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test /* Note: this scenario is unlikely to happen in 'production', it is to prevent problems if source control changes lastModified back to a previous time */
	public void trueIfFileModifiedDecreases()
	{
		when(file.lastModified()).thenReturn(t1);
		assertTrue("first check should be true", checker.fileModifiedSinceLastCheck());
		when(file.lastModified()).thenReturn(t0);
		assertTrue("should be true, modified time changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void trueIfFileSizeIncreases()
	{
		when(file.length()).thenReturn(size0);
		firstCheckReturnsTrue();		
		when(file.length()).thenReturn(size1);
		assertTrue("should be true, file size changed", checker.fileModifiedSinceLastCheck());
	}
	
	@Test
	public void trueIfFileSizeDecreases()
	{
		when(file.length()).thenReturn(size1);
		firstCheckReturnsTrue();
		when(file.length()).thenReturn(size0);
		assertTrue("should be true, file size changed", checker.fileModifiedSinceLastCheck());
	}
	
	
}
