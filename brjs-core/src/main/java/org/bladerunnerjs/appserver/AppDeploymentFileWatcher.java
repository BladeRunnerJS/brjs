package org.bladerunnerjs.appserver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.utility.FileIterator;

import static org.bladerunnerjs.appserver.AppDeploymentFileWatcher.Messages.*;

public class AppDeploymentFileWatcher extends Thread
{
	
	private static final long CHECK_INTERVAL = 100;

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

	private List<FileIterator> watchDirIterators = new ArrayList<>();
	private volatile boolean running = true;
	
	// TODO: replace this with file watcher - recusive watching info here http://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java
	public AppDeploymentFileWatcher(BRJS brjs, BRJSApplicationServer appServer, File... rootWatchDirs)
	{
		logger = brjs.logger(LoggerType.APP_SERVER, this.getClass());
		
		this.appServer = appServer;
		this.brjs = brjs;
		
		for(File rootWatchDir : rootWatchDirs) {
			if (rootWatchDir.isDirectory())
			{
				watchDirIterators.add(brjs.getFileIterator(rootWatchDir));
			}
		}
	}
	
	@Override
	public void run()
	{
		while(running)
		{
			try
			{
				for (FileIterator watchDirIterator : watchDirIterators)
				{
					checkForNewApps(watchDirIterator);
				}
				Thread.sleep(CHECK_INTERVAL);
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

	private void checkForNewApps(FileIterator watchDirIterator)
	{
		for (File dir : watchDirIterator.dirs())
		{
			if (isAppDirWithDeployFile(dir)) 
			{
				deployApp(dir);
			}
		}
	}


	private boolean isAppDirWithDeployFile(File dir)
	{
		App app = brjs.locateAncestorNodeOfClass(dir, App.class);
		return app != null && ApplicationServerUtils.getDeployFileForApp(app).isFile();
	}

	private void deployApp(File appDir)
	{
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
