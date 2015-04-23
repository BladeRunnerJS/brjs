package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;


public class ValidAppDirFileFilter implements IOFileFilter
{

	public static final String NON_APP_DIR_FOUND_MSG = "The directory '%s' doesn't represent an app but was found in the apps directory at '%s'."+
			"For performance and memory reasons it's recommended that only app directories are contained within the apps folder.";
	
	private IOFileFilter validAppDirContentsFilter;
	private Logger logger;

	
	public ValidAppDirFileFilter(BRJS brjs) {
		this.validAppDirContentsFilter = new WildcardFileFilter(Arrays.asList(AppConf.FILE_NAME, "*.html", "*.jsp", "*-aspect", "*-bladeset", "blades", "WEB-INF"));
		logger = brjs.logger(this.getClass());
	}

	@Override
	public boolean accept(File file)
	{
		File[] childFiles = file.listFiles();
		if (childFiles != null && childFiles.length > 0)
		{
    		for (File childFile : childFiles) {
    			if (validAppDirContentsFilter.accept(childFile)) {
    				return true;
    			}
    		}
		}
		logger.warn( NON_APP_DIR_FOUND_MSG, file.getAbsolutePath(), file.getParentFile().getAbsolutePath() );
		return false;
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return accept(new File(dir, name));
	}
	
}
