package org.bladerunnerjs.model;

import org.bladerunnerjs.model.events.BundleSetCreatedEvent;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BundleSetCreatorTest extends SpecTest
{
	
	private App app;
	private Aspect aspect;
	private EventObserver mockEventObserver;

	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
		app = brjs.app("app1");
		aspect = app.defaultAspect();
		mockEventObserver = mock(EventObserver.class);
		
		aspect.addObserver(BundleSetCreatedEvent.class, mockEventObserver);
	}
	
	@Test
	public void bundleSetCreatedEventIsTriggered() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated();
		when(aspect).bundleSetGenerated();
		verify(mockEventObserver).onEventEmitted(any(BundleSetCreatedEvent.class), eq(aspect));
	}
}
