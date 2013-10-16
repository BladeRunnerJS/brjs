package org.bladerunnerjs.specutil.engine;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;


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
		verify(observer).onEventEmitted( any(eventClass), eq(node) );
		
		return verifierChainer;
	}

	public VerifierChainer noNotifications()
	{
		verifyZeroInteractions(observer);
		
		return verifierChainer;
	}

}
