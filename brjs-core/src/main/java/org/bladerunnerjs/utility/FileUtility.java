package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

public class FileUtility {
	public static File createTemporaryDirectory(String prependedFolderName) throws IOException
	{
		if (prependedFolderName.contains("/"))
		{
			throw new IOException("prependedFolderName can't contain a /");
		}
		final File tempdir = File.createTempFile(prependedFolderName, "");
		tempdir.delete();
		tempdir.mkdir();
		Runtime.getRuntime().addShutdownHook(new DeleteTempFileShutdownHook(tempdir));
		File tempSubDir = new File(tempdir, prependedFolderName);
		tempSubDir.mkdir();
		return tempSubDir;
	}
}
