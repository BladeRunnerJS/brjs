package org.bladerunnerjs.spec.node;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class NodeTest extends SpecTest {
	NodeProperties nodeProperties;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			nodeProperties = brjs.nodeProperties("plugin-name");
	}
	
	@Test
	public void observerisNotifiedWhenNodeIsFirstDiscoveredAfterManualCreation() {
		final String APP_NAME = "someApp";
		given(observer).observing(brjs)
			.and(brjs).hasDir("apps/"+APP_NAME);
		when(brjs).discoverApps();
		then(observer).notified( NodeReadyEvent.class, brjs.app("someApp") );
	}
	
	@Test
	public void persistentPropertiesStoredOnNodesCanBeRetrievedImmedidately() throws Exception {
		when(nodeProperties).setPersisentProperty("property-name", "property-value");
		then(nodeProperties).persistentPropertyHasValue("property-name", "property-value");
	}
	
	@Test
	public void persistentPropertiesStoredOnNodesCanBeRetrievedOnAFreshCopyOfTheModel() throws Exception {
		given(nodeProperties).persistentPropertyHasBeenSet("property-name", "property-value");
		when(brjs).hasBeenCreated();
		then(nodeProperties).persistentPropertyHasValue("property-name", "property-value");
	}
	
	@Test
	public void transientPropertiesStoredOnNodesCanBeRetrievedImmedidately() throws Exception {
		when(nodeProperties).setTransientProperty("property-name", "property-value");
		then(nodeProperties).transientPropertyHasValue("property-name", "property-value");
	}
	
	@Test
	public void transientPropertiesStoredOnNodesCannotBeRetrievedOnAFreshCopyOfTheModel() throws Exception {
		given(nodeProperties).transientPropertyHasBeenSet("property-name", "property-value");
		when(brjs).hasBeenCreated();
		then(nodeProperties).transientPropertyHasValue("property-value", null);
	}
	
	@Test
	public void transientPropertiesCanBeObjectsAndTheSameObjectIsReturned() throws Exception {
		Object myObj = new Object();
		given(nodeProperties).transientPropertyHasBeenSet("property-name", myObj);
		then(nodeProperties).transientPropertyIsSameAs("property-name", myObj);
	}
	
}
