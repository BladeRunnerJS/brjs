package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;


public class DeleteTempFileShutdownHook extends Thread
{
	private File tempFile;
	
	public DeleteTempFileShutdownHook(File tempFile)
	{
		this.tempFile = tempFile;
	}
	
	public void run() {
		if (!tempFile.exists())
		{
			return;
		}
		
		try
		{				
			if (tempFile.isDirectory())
			{
				FileUtils.deleteDirectoryFromBottomUp(tempFile);
			}
			else
			{
				tempFile.delete();
			}
			org.apache.commons.io.FileUtils.deleteQuietly(tempFile); // the above methods sometimes dont seem to delete properly - extra 'delete quietly' for good measure
		}
		catch (IOException e)
		{
			System.err.println("Unable to cleanup temporary dir: " + tempFile.getAbsolutePath() + "\n	- " + e.getMessage());
		}
	}
}