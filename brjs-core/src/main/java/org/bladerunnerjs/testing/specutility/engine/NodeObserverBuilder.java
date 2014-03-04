package org.bladerunnerjs.testing.specutility.engine;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.EventObserver;



public class NodeObserverBuilder
{
	private BuilderChainer builderChainer;
	private EventObserver observer;

	public NodeObserverBuilder(SpecTest modelTest, EventObserver observer)
	{
		this.observer = observer;
		builderChainer = new BuilderChainer(modelTest);
	}

	public BuilderChainer observing(Node node)
	{
		node.addObserver(observer);
		return builderChainer;
	}

	public BuilderChainer allNotificationsHandled()
	{
		reset(observer);
		return builderChainer;
	}

}
