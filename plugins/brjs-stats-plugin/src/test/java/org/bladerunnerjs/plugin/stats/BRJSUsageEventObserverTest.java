package org.bladerunnerjs.plugin.stats;


import static org.mockito.Mockito.*;
import io.keen.client.java.KeenCallback;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenProject;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.events.NewInstallEvent;
import org.junit.Before;
import org.junit.Test;


public class BRJSUsageEventObserverTest extends SpecTest
{

	private KeenClient mockKeenClient;
	private App app;
	private Aspect aspect;
	private BRJSUsageEventObserver usageObserver;
	private App sysApp;
	private Aspect sysAppAspect;

	@Before
	public void initTestObjects() throws Exception
	{
		mockKeenClient = mock(KeenClient.class);
		usageObserver = new BRJSUsageEventObserver(mockKeenClient);	
		
		given(brjs).hasModelObserverPlugins(usageObserver)
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.defaultAspect();
			sysApp = brjs.systemApp("sysapp");
			sysAppAspect = sysApp.defaultAspect();
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
	public void bundlesetsKeenIOEventIsNotTriggeredForSystemApps() throws Exception
	{
		given(sysApp).hasBeenCreated()
			.and(sysAppAspect).hasBeenCreated();
		when(sysAppAspect).bundleSetGenerated();
		verify(mockKeenClient, never()).addEventAsync( (KeenProject)eq(null), any(String.class), anyMapOf(String.class, Object.class), anyMapOf(String.class, Object.class), any(KeenCallback.class) );
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
