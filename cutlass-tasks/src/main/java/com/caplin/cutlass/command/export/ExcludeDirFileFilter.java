package com.caplin.cutlass.command.export;

import java.io.File;
import java.io.IOException;

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
		try {
			String pathName = fileOrDirectory.getCanonicalPath().toLowerCase();
			if (fileOrDirectory.isDirectory()) {
				pathName += File.separator;
			}
			return ! pathName.contains(pathElement);
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return accept(dir);
	}
	
}