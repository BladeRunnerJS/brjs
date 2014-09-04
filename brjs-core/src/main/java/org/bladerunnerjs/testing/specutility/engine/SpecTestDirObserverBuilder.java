package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.testing.utility.SpecTestDirObserver;


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
		observer.getFileModificationService().initialise(brjs, dir);
		observer.setDirObserver( observer.getFileModificationService().getModificationInfo(dir) );
		
		return builderChainer;
	}

	public void hasDetectedChanges()
	{
		observer.getDirObserver();
	}

}
