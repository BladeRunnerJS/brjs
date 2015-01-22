package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;


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
