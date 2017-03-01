package org.bladerunnerjs.api.spec.engine;

import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;


public class NodeObserverCommander
{

	private EventObserver observer;
	private CommanderChainer commanderChainer;

	public NodeObserverCommander(SpecTest specTest, EventObserver observer)
	{
		this.observer = observer;
		commanderChainer = new CommanderChainer(specTest);
	}

	public CommanderChainer observing(Node node)
	{
		node.addObserver(observer);
		
		return commanderChainer;
	}

}
