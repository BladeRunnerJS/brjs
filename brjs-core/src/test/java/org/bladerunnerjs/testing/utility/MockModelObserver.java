package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.model.BRJS;


public class MockModelObserver implements ModelObserverPlugin
{

	BRJS brjs;
	boolean failOnSetBRJS;
	
	public MockModelObserver()
	{
		this(false);
	}
	
	public MockModelObserver(boolean failOnSetBRJS)
	{
		this.failOnSetBRJS = failOnSetBRJS;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

}
