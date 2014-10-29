package org.bladerunnerjs.utility;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.utility.FileUtility;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FileUtilityTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void createTemporaryDirectory() throws Exception
	{
		File temporaryDirectory = null;
		
		try
		{
			temporaryDirectory = FileUtility.createTemporaryDirectory( this.getClass(), "fileutilitytest" );
			
			assertTrue(temporaryDirectory.exists());
			assertTrue(temporaryDirectory.getName().contains("fileutilitytest"));
		}
		finally
		{
			if(temporaryDirectory != null && temporaryDirectory.exists())
			{
				temporaryDirectory.delete();
			}
		}
	}
	
	@Test
	public void cantCreateATemporaryDirectoryWithLeadingSlash() throws IOException
	{
		exception.expect(IOException.class);
		exception.expectMessage("subFolderName can't contain a /");

		FileUtility.createTemporaryDirectory( this.getClass(), "/fileutilitytest" );
	}
}
