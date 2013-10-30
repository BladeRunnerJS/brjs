package org.bladerunnerjs.spec.brjs;

import static org.bladerunnerjs.model.BRJS.Messages.*;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.testing.utility.MockCommand;
import org.bladerunnerjs.testing.utility.MockModelObserver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BRJSStartupTest extends SpecTest {
	
	CommandPlugin passingCommandPlugin;
	CommandPlugin failingCommandPlugin;
	
	ModelObserverPlugin passingModelObserverPlugin;
	ModelObserverPlugin failingModelObserverPlugin;
	
	
	@Before
	public void setup()
	{
		passingCommandPlugin = new MockCommand("passingCommand","","","");
		failingCommandPlugin = new MockCommand("failingCommand","","","",true);
		passingModelObserverPlugin = new MockModelObserver();
		failingModelObserverPlugin = new MockModelObserver(true);
	}
	
	@Test
	public void informativeInitializationLogsAreEmittedAtStartup() {
		given(logging).enabled();
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_MODEL_OBSERVER_PLUGINS_LOG_MSG)
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(CREATING_COMMAND_PLUGINS_LOG_MSG);
	}
	
	@Test
	public void pluginsThatHaveBeenFoundAreListed() {		
		given(logging).enabled()
			.and(brjs).hasCommand(passingCommandPlugin)
			.and(brjs).hasModelObserver(passingModelObserverPlugin);
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_MODEL_OBSERVER_PLUGINS_LOG_MSG)
			.and(logging).debugMessageReceived(PLUGIN_FOUND_MSG, passingModelObserverPlugin.getClass().getCanonicalName())
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(CREATING_COMMAND_PLUGINS_LOG_MSG)
			.and(logging).debugMessageReceived(PLUGIN_FOUND_MSG, passingCommandPlugin.getClass().getCanonicalName());
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