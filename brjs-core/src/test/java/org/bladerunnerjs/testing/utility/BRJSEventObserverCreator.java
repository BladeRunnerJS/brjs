package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.model.BRJS;


public class BRJSEventObserverCreator implements ModelObserverPlugin
{
	EventObserver observer;
	
	public BRJSEventObserverCreator(EventObserver observer)
	{
		this.observer = observer;
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		brjs.addObserver(observer);
	}
}
