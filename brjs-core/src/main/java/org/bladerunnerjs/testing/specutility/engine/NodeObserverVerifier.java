package org.bladerunnerjs.testing.specutility.engine;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;


public class NodeObserverVerifier
{
	private final VerifierChainer verifierChainer;
	private EventObserver observer;
	
	public NodeObserverVerifier(SpecTest modelTest, EventObserver observer)
	{
		this.observer = observer;
		verifierChainer = new VerifierChainer(modelTest);
	}

	public VerifierChainer notified(Class<? extends Event> eventClass, Node node)
	{
		verify(observer).onEventEmitted( isA(eventClass), eq(node) );
		
		return verifierChainer;
	}

	public VerifierChainer noNotifications()
	{
		verifyZeroInteractions(observer);
		
		return verifierChainer;
	}

}
