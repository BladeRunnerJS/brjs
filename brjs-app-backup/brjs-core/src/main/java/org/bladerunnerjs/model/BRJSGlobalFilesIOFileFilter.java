package org.bladerunnerjs.model;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.api.BRJS;


public class BRJSGlobalFilesIOFileFilter implements IOFileFilter
{

	public BRJSGlobalFilesIOFileFilter(BRJS brjs)
	{
	}

	@Override
	public boolean accept(File file)
	{
		return accept(file.getParentFile(), file.getName());
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return name.equals(".js-style") || name.equals("brjs.conf") || name.equals("app.conf");
	}

}
