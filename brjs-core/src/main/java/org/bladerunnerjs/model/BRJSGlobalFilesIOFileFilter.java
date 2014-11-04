package org.bladerunnerjs.model;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;


public class BRJSGlobalFilesIOFileFilter implements IOFileFilter
{

	private BRJS brjs;

	public BRJSGlobalFilesIOFileFilter(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public boolean accept(File file)
	{
		return accept(file.getParentFile(), file.getName());
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return name.equals(".js-style") || name.endsWith(".class") || name.endsWith(".jar");
	}

}
