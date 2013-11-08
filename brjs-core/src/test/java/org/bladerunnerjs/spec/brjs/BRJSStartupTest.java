package org.bladerunnerjs.spec.brjs;

import static org.bladerunnerjs.model.BRJS.Messages.*;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLocatorUtils;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.testing.utility.BRJSEventObserverCreator;
import org.bladerunnerjs.testing.utility.ExceptionThrowingEventObserver;
import org.bladerunnerjs.testing.utility.MockCommand;
import org.bladerunnerjs.testing.utility.MockModelObserver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class BRJSStartupTest extends SpecTest {
	
	RuntimeException pluginException = new RuntimeException("FAIL!");

	CommandPlugin passingCommandPlugin;
	CommandPlugin failingCommandPlugin;
	
	ModelObserverPlugin passingModelObserverPlugin;
	ModelObserverPlugin failingModelObserverPlugin;
	
	EventObserver failingEventObserver = new ExceptionThrowingEventObserver(pluginException);
	ModelObserverPlugin failingEventObserverModelObserver = new BRJSEventObserverCreator(observer);
	
	
	@Before
	public void setup()
	{
		passingCommandPlugin = new MockCommand("passingCommand","","","");
		failingCommandPlugin = new MockCommand("failingCommand","","","",pluginException);
		passingModelObserverPlugin = new MockModelObserver();
		failingModelObserverPlugin = new MockModelObserver(pluginException);
		
		
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
	
	@Test
	public void fatalErrorIsEmittedIfAnyOfTheModelObserverPluginsCantBeCreated() {		
		given(logging).enabled()
			.and(brjs).hasModelObserver(failingModelObserverPlugin);
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_MODEL_OBSERVER_PLUGINS_LOG_MSG)
			.and(logging).errorMessageReceived(PluginLocatorUtils.Messages.INIT_PLGUIN_ERROR_MSG, failingModelObserverPlugin.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(pluginException))
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(CREATING_COMMAND_PLUGINS_LOG_MSG);
		
	}
	
	@Ignore //TODO: this doesnt verify the error log if model observers fail during node discovery
	@Test
	public void modelObserverExceptionsAreLoggedAsWarningsDuringNodeDiscovery() {
		given(logging).enabled()
			.and(brjs).hasModelObserver(failingEventObserverModelObserver);
		when(brjs).hasBeenCreated();
			then(logging).infoMessageReceived(CREATING_MODEL_OBSERVER_PLUGINS_LOG_MSG)
//				.and(logging).errorMessageReceived(PluginLocatorUtils.Messages.INIT_PLGUIN_ERROR_MSG, failingModelObserverPlugin.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(pluginException))
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(CREATING_COMMAND_PLUGINS_LOG_MSG);
	}
	
	@Test
	public void fatalErrorIsEmittedIfAnyOfTheCommandPluginsCantBeCreated() {
		given(logging).enabled()
    		.and(brjs).hasCommand(failingCommandPlugin);
    	when(brjs).hasBeenCreated();
    	then(logging).infoMessageReceived(CREATING_MODEL_OBSERVER_PLUGINS_LOG_MSG)
    		.and(logging).errorMessageReceived(PluginLocatorUtils.Messages.INIT_PLGUIN_ERROR_MSG, failingCommandPlugin.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(pluginException))
    		.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
    		.and(logging).infoMessageReceived(CREATING_COMMAND_PLUGINS_LOG_MSG);
	}
}