package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;
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

	public BuilderChainer isObservingDir(File dir, BRJS brjs)
	{
		FileModificationService fileModificationService = new Java7FileModificationService(new StubLoggerFactory());
		fileModificationService.initialise(brjs, dir);
		
		observer.setDirObserver( fileModificationService.getModificationInfo(dir) );
		
		return builderChainer;
	}

	public void hasDetectedChanges()
	{
		observer.getDirObserver();
	}

}
