package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;


public class ExceptionThrowingEventObserver implements EventObserver
{
	
	RuntimeException throwExceptopn;
	
	public ExceptionThrowingEventObserver(RuntimeException throwExceptopn)
	{
		this.throwExceptopn = throwExceptopn;
	}
	
	@Override
	public void onEventEmitted(Event event, Node node)
	{
		throw throwExceptopn;
	}
}
