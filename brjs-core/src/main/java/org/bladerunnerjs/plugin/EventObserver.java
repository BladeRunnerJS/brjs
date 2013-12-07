package org.bladerunnerjs.plugin;

import org.bladerunnerjs.model.engine.Node;


public interface EventObserver
{
	public void onEventEmitted(Event event, Node node);
}
