package org.bladerunnerjs.spec.brjs;

import static org.bladerunnerjs.model.BRJS.Messages.*;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.utility.PluginLocatorUtils;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.BRJSEventObserverCreator;
import org.bladerunnerjs.testing.utility.ExceptionThrowingEventObserver;
import org.bladerunnerjs.testing.utility.MockCommandPlugin;
import org.bladerunnerjs.testing.utility.MockModelObserverPlugin;
import org.bladerunnerjs.utility.ObserverList;
import org.bladerunnerjs.utility.ObserverList.Messages;
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
	ModelObserverPlugin failingEventObserverModelObserver = new BRJSEventObserverCreator(failingEventObserver);
	
	
	@Before
	public void setup()
	{
		passingCommandPlugin = new MockCommandPlugin("passingCommand","","","");
		failingCommandPlugin = new MockCommandPlugin("failingCommand","","","",pluginException);
		passingModelObserverPlugin = new MockModelObserverPlugin();
		failingModelObserverPlugin = new MockModelObserverPlugin(pluginException);
		
		
	}
	
	@Test
	public void informativeInitializationLogsAreEmittedAtStartup() {
		given(logging).enabled();
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_PLUGINS_LOG_MSG)
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG);
	}
	
	@Test
	public void pluginsThatHaveBeenFoundAreListed() {			
		given(logging).enabled()
			.and(brjs).hasCommandPlugins(passingCommandPlugin)
			.and(brjs).hasModelObserverPlugins(passingModelObserverPlugin);
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_PLUGINS_LOG_MSG)
			.and(logging).debugMessageReceived(PLUGIN_FOUND_MSG, passingModelObserverPlugin.getClass().getCanonicalName())
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG)
			.and(logging).debugMessageReceived(PLUGIN_FOUND_MSG, passingCommandPlugin.getClass().getCanonicalName());
	}
	
	@Test
	public void fatalErrorIsEmittedIfAnyOfTheModelObserverPluginsCantBeCreated() {
		given(logging).enabled()
			.and(brjs).hasModelObserverPlugins(failingModelObserverPlugin);
		when(brjs).hasBeenCreated();
		then(logging).infoMessageReceived(CREATING_PLUGINS_LOG_MSG)
			.and(logging).errorMessageReceived(PluginLocatorUtils.Messages.INIT_PLUGIN_ERROR_MSG, failingModelObserverPlugin.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(pluginException))
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG);
		
	}
	
	@Test @Ignore //TODO: how do we handle exceptions with stack traces?
	public void modelObserverExceptionsAreLoggedAsWarningsDuringNodeDiscovery() {
		given(logging).enabled()
			.and(brjs).hasModelObserverPlugins(failingEventObserverModelObserver);
		when(brjs).hasBeenCreated();
			then(logging).infoMessageReceived(CREATING_PLUGINS_LOG_MSG)
				.and(logging).warnMessageReceived(ObserverList.Messages.NODE_OBSERVER_EXCEPTION_MSG, failingModelObserverPlugin.getClass(), unquoted("java.lang.RuntimeException: FAIL!"))
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG);
	}
	
	@Test
	public void fatalErrorIsEmittedIfAnyOfTheCommandPluginsCantBeCreated() throws Exception {
		given(logging).enabled()
			.and(brjs).hasCommandPlugins(failingCommandPlugin);
		when(brjs).hasBeenCreated()
			.and(brjs).runCommand("help", "failingCommand");
		then(logging).infoMessageReceived(CREATING_PLUGINS_LOG_MSG)
			.and(logging).errorMessageReceived(PluginLocatorUtils.Messages.INIT_PLUGIN_ERROR_MSG, failingCommandPlugin.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(pluginException))
			.and(logging).infoMessageReceived(PERFORMING_NODE_DISCOVERY_LOG_MSG)
			.and(logging).infoMessageReceived(MAKING_PLUGINS_AVAILABLE_VIA_MODEL_LOG_MSG);
	}
	
}