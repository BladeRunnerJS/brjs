package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.base.AbstractModelObserverPlugin;


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
