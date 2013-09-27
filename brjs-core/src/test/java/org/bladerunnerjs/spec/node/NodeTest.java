package org.bladerunnerjs.spec.node;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.specutil.engine.SpecTest;
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
	public void propertiesStoredOnNodesCanBeRetrievedImmedidately() throws Exception {
		when(nodeProperties).setProperty("property-name", "property-value");
		then(nodeProperties).propertyHasValue("property-name", "property-value");
	}
	
	@Test
	public void propertiesStoredOnNodesCanBeRetrievedOnAFreshCopyOfTheModel() throws Exception {
		given(nodeProperties).propertyHasBeenSet("property-name", "property-value");
		when(brjs).hasBeenCreated();
		then(nodeProperties).propertyHasValue("property-name", "property-value");
	}
}
