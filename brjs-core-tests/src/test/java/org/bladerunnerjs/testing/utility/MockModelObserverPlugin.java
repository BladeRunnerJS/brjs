package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractModelObserverPlugin;


public class MockModelObserverPlugin extends AbstractModelObserverPlugin implements ModelObserverPlugin
{

	BRJS brjs;
	RuntimeException throwException;
	
	public MockModelObserverPlugin()
	{
		this(null);
	}
	
	public MockModelObserverPlugin(RuntimeException throwException)
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
