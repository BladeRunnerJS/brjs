package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.bladerunnerjs.testing.utility.SpecTestDirObserver;
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
		observer.setDirObserver( new Java7FileModificationService(dir).getModificationInfo(dir) );
		
		return builderChainer;
	}

	public void hasDetectedChanges()
	{
		observer.getDirObserver();
	}

}
