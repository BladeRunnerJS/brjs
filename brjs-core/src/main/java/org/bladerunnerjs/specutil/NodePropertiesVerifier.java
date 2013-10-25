package org.bladerunnerjs.specutil;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NodePropertiesVerifier {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesVerifier(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void propertyHasValue(String propertyName, String propertyValue) throws Exception {
		assertEquals(propertyValue, nodeProperties.getProperty(propertyName));
	}
}
