package org.bladerunnerjs.api.plugin;

import org.bladerunnerjs.model.engine.Node;

/**
 * Used to specify the behaviour after the firing of an {@link Event} on the specified {@link Node}.
 */

public interface EventObserver
{
	public void onEventEmitted(Event event, Node node);
}
