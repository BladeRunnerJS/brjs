package org.bladerunnerjs.appserver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;

import static org.bladerunnerjs.appserver.AppDeploymentFileWatcher.Messages.*;

public class AppDeploymentFileWatcher extends Thread
{
	private static final long DEFAULT_CHECK_INTERVAL = 100;
	
	//TOOD: these messages arent tested in our spec tests
	public class Messages
	{
		public static final String WATCHING_INTERUPTED_MSG = "%s was interupted when watching for new app deployments. New app auto deployment will stop";
		public static final String ERROR_DEPLOYING_APP_MSG = "There was an error deploying the app '%s'. Restart the server to deploy this app. The exception was: %s";
		public static final String ERROR_CREATING_WATCHER = "Error creating watcher for directory '%s',"+
				" new apps under this directory will probably not be depoyed. Reset the server to deploy these apps. The exception was: %s";
		public static final String NEW_DIR_MESSAGE = "%s found a new dir to watch. Adding a watcher for the directory '%s'";
		public static final String NEW_APP_MESSAGE = "%s found the new app '%s', attempting to deploy it.";
	}
	
	private Logger logger;
	private BRJSApplicationServer appServer;
	private BRJS brjs;

	private List<MemoizedFile> watchDirs;
	private volatile boolean running = true;
	private final long checkInterval;
	
	public AppDeploymentFileWatcher(BRJS brjs, BRJSApplicationServer appServer, long checkInterval, MemoizedFile... rootWatchDirs)
	{
		logger = brjs.logger(this.getClass());
		
		this.appServer = appServer;
		this.brjs = brjs;
		
		watchDirs = Arrays.asList(rootWatchDirs);
		this.checkInterval = (checkInterval > 0) ? checkInterval : DEFAULT_CHECK_INTERVAL;
	}
	
	@Override
	public void run()
	{
		while(running)
		{
			try
			{
				for (MemoizedFile watchDir : watchDirs)
				{
					checkForNewApps(watchDir);
				}
				Thread.sleep(checkInterval);
			}
			catch (InterruptedException e)
			{
				logger.warn(WATCHING_INTERUPTED_MSG, this.getClass().getSimpleName());
			}
		}
	}
	
	public void terminate() throws InterruptedException {
		running = false;
		join();
	}

	private void checkForNewApps(MemoizedFile watchDir)
	{
		File underlyingFile = watchDir.getUnderlyingFile(); // get the underlying file so listFiles isnt cached
		if (!underlyingFile.isDirectory()) {
			return;
		}
		for (File dir : underlyingFile.listFiles())
		{
			if (isAppDirWithDeployFile(watchDir, dir)) 
			{
				deployApp(watchDir, dir);
			}
		}
	}


	private boolean isAppDirWithDeployFile(File rootWatchDir, File dir)
	{
		if (!dir.isDirectory()) {
			return false;
		}
		return ApplicationServerUtils.getDeployFile(dir).isFile(); // get the underlying file so listFiles isnt cached
	}

	private void deployApp(File rootWatchDir, File appDir)
	{
		brjs.getFileModificationRegistry().incrementFileVersion(rootWatchDir);
		App app = brjs.locateAncestorNodeOfClass(appDir, App.class);
		try 
		{
			logger.debug(NEW_APP_MESSAGE, this.getClass().getSimpleName(), app.getName());
			appServer.deployApp(app);
		}
		catch (Exception ex)
		{
			logger.warn(ERROR_DEPLOYING_APP_MSG, app.getName(), ex.toString());
		}
	}
	
}
