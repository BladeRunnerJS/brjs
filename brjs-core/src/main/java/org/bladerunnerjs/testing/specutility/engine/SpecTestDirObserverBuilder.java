package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.bladerunnerjs.testing.utility.SpecTestDirObserver;
import org.bladerunnerjs.utility.filemodification.FileModificationService;
import org.bladerunnerjs.utility.filemodification.Java7FileModificationService;


public class SpecTestDirObserverBuilder
{
	private SpecTestDirObserver observer;
	private BuilderChainer builderChainer;
	
	
	public SpecTestDirObserverBuilder(SpecTest specTest, SpecTestDirObserver observer)
	{
		this.observer = observer;
		builderChainer = new BuilderChainer(specTest);
	}

	public BuilderChainer isObservingDir(File dir)
	{
		FileModificationService fileModificationService = new Java7FileModificationService();
		fileModificationService.setRootDir(dir);
		
		observer.setDirObserver( fileModificationService.getModificationInfo(dir) );
		
		return builderChainer;
	}

	public void hasDetectedChanges()
	{
		observer.getDirObserver();
	}

}
