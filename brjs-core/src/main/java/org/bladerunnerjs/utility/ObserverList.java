package org.bladerunnerjs.utility;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;


public class ObserverList
{
	
	public class Messages {
		public static final String NODE_OBSERVER_EXCEPTION_MSG = "node observer '%s' threw an exception. the exception was: %s";
	}
	
	private List<EventObserverEntry> observers = new LinkedList<EventObserverEntry>();

	public void add(Class<? extends Event> eventType, EventObserver observer)
	{
		observers.add( new EventObserverEntry(eventType, observer) );
	}

	public void eventEmitted(Event event, Node node)
	{
		for (EventObserverEntry observerEntry : observers)
		{
			try
			{
				observerEntry.notifyObserver(event, node);
			}
			catch (Throwable ex)
			{
				node.root().logger(this.getClass()).warn(Messages.NODE_OBSERVER_EXCEPTION_MSG, observerEntry.observerClass(), ExceptionUtils.getStackTrace(ex));
			}
		}
	}

}
