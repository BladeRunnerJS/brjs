package org.bladerunnerjs.model.utility;

import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;


public class EventObserverEntry
{

	private Class<? extends Event> eventType;
	private EventObserver observer;

	public EventObserverEntry(Class<? extends Event> eventType, EventObserver observer)
	{
		this.eventType = eventType;
		this.observer = observer;
	}

	public void notifyObserver(Event event, Node node)
	{
		if ( eventType.isInstance(event) )
		{
			observer.onEventEmitted(event, node);
		}
	}

	public Class<? extends EventObserver> observerClass()
	{
		return observer.getClass();
	}
	
}
