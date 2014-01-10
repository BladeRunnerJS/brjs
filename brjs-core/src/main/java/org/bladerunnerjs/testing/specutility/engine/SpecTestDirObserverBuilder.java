package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

import org.bladerunnerjs.plugin.utility.filechange.WatchingDirectoryObserver;
import org.bladerunnerjs.testing.utility.SpecTestDirObserver;


public class SpecTestDirObserverBuilder
{

	private WatchService watcher;
	{
		try {
			watcher = FileSystems.getDefault().newWatchService();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private SpecTestDirObserver observer;
	private BuilderChainer builderChainer;
	
	
	public SpecTestDirObserverBuilder(SpecTest specTest, SpecTestDirObserver observer)
	{
		this.observer = observer;
		builderChainer = new BuilderChainer(specTest);
	}

	public BuilderChainer isObservingDir(File dir)
	{
		WatchingDirectoryObserver dirObserver = new WatchingDirectoryObserver(watcher, dir);
		observer.setDirObserver( dirObserver );
		
		return builderChainer;
	}

	public void hasDetectedChanges()
	{
		observer.getDirObserver();
	}

}
