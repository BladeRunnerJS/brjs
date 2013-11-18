package org.bladerunnerjs.spec.brjs;

import static org.bladerunnerjs.model.BRJS.Messages.*;

import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Ignore;
import org.junit.Test;


public class BRJSStartupTest extends SpecTest {
	@Test
	public void informativeInitializationLogsAreEmittedAtStartup() {
		given(logging).enabled();
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_PLUGINS_LOG_MSG)
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG);
	}
	
	@Ignore
	@Test
	public void fatalErrorIsEmittedIfAnyOfTheModelObserverPluginsCantBeCreated() {
	}
	
	@Ignore
	@Test
	public void modelObserverExceptionsAreLoggedAsWarningsDuringNodeDiscovery() {
	}
	
	@Ignore
	@Test
	public void fatalErrorIsEmittedIfAnyOfTheCommandPluginsCantBeCreated() {
	}
}