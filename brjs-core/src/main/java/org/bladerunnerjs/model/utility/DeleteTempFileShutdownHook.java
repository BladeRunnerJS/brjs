package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

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
				FileUtils.deleteDirectory(tempFile);
			}
			else
			{
				tempFile.delete();
			}
		}
		catch (IOException e)
		{
			System.err.println("Unable to cleanup temporary dir: " + tempFile.getAbsolutePath() + "\n	- " + e.getMessage());
		}
	}
}