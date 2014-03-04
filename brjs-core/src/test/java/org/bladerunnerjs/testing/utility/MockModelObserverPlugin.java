package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.base.AbstractModelObserverPlugin;


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
