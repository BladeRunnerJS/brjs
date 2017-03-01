package org.bladerunnerjs.utility.filefilter;

import java.io.File;
import org.apache.commons.io.filefilter.IOFileFilter;

public class ExcludeDirFileFilter implements IOFileFilter 
{
	protected final String pathElement;

	public ExcludeDirFileFilter(String... strings)
	{
		StringBuilder path = new StringBuilder(File.separator);
		for (String element : strings) {
			path.append(element);
			path.append(File.separator);
		}
		pathElement = path.toString();
	}
	
	public boolean accept(File fileOrDirectory)
	{		
		String pathName = fileOrDirectory.getAbsolutePath().toLowerCase();
		if (fileOrDirectory.isDirectory()) {
			pathName += File.separator;
		}
		return ! pathName.contains(pathElement);
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return accept(dir);
	}
}