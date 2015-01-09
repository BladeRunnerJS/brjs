package org.bladerunnerjs.plugin.stats;


import static org.mockito.Mockito.*;

import io.keen.client.java.KeenCallback;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenProject;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.events.BundleSetCreatedEvent;
import org.bladerunnerjs.model.events.CommandExecutedEvent;
import org.bladerunnerjs.model.events.NewInstallEvent;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class BRJSUsageEventObserverTest extends SpecTest
{

	private KeenClient mockKeenClient;
	private App app;
	private Aspect aspect;
	private BRJSUsageEventObserver usageObserver;

	@Before
	public void initTestObjects() throws Exception
	{
		mockKeenClient = mock(KeenClient.class);
		usageObserver = new BRJSUsageEventObserver(mockKeenClient);	
		
		given(brjs).hasModelObserverPlugins(usageObserver)
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.defaultAspect();
			brjs.bladerunnerConf().setAllowAnonymousStats(true);
			brjs.bladerunnerConf().write();
	}	
	
	@Test
	public void bundlesetsKeenIOEventIsTriggeredOnBundlesetCreated() throws Exception
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated();
		when(aspect).bundleSetGenerated();
		verify(mockKeenClient).addEventAsync( (KeenProject)eq(null), eq("bundlesets"), anyMapOf(String.class, Object.class), anyMapOf(String.class, Object.class), any(KeenCallback.class) );
	}
	
	@Test
	public void commandsKeenIOEventIsTriggeredOnCommandExecuted() throws Exception
	{
		when(brjs).runCommand("help");
		verify(mockKeenClient).addEventAsync( (KeenProject)eq(null), eq("commands"), anyMapOf(String.class, Object.class), anyMapOf(String.class, Object.class), any(KeenCallback.class) );
	}
	
	@Test
	public void installsKeenIOEventIsTriggeredOnNewInstall() throws Exception
	{
		usageObserver.onEventEmitted(new NewInstallEvent(), brjs); // we can't easily trigger this via the model since its performed by the static main method
		verify(mockKeenClient).addEventAsync( (KeenProject)eq(null), eq("installs"), anyMapOf(String.class, Object.class), anyMapOf(String.class, Object.class), any(KeenCallback.class) );
	}
	
}
