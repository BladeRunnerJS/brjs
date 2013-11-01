package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.model.BRJS;


public class MockModelObserver implements ModelObserverPlugin
{

	BRJS brjs;
	RuntimeException throwException;
	
	public MockModelObserver()
	{
		this(null);
	}
	
	public MockModelObserver(RuntimeException throwException)
	{
		this.throwException = throwException;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		if (throwException != null) { throw throwException; }
	}

}
