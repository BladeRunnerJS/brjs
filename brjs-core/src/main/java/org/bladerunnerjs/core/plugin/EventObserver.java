package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.model.engine.Node;


public interface EventObserver
{
	public void onEventEmitted(Event event, Node node);
}
