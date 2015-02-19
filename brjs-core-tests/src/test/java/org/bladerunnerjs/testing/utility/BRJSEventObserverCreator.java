package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractModelObserverPlugin;


public class BRJSEventObserverCreator extends AbstractModelObserverPlugin implements ModelObserverPlugin
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
