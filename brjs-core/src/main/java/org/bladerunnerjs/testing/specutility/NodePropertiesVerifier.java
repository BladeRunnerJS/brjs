package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NodePropertiesVerifier {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesVerifier(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void persistentPropertyHasValue(String propertyName, String propertyValue) throws Exception {
		assertEquals(propertyValue, nodeProperties.getPersisentProperty(propertyName));
	}

	public void transientPropertyHasValue(String propertyName, String propertyValue)
	{
		assertEquals(propertyValue, nodeProperties.getTransientProperty(propertyName));
	}

	public void transientPropertyIsSameAs(String propertyName, Object propertyValue)
	{
		assertSame(propertyValue, nodeProperties.getTransientProperty(propertyName));
	}
}
