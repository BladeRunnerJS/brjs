package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NodePropertiesBuilder {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesBuilder(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void propertyHasBeenSet(String propertyName, String propertyValue) throws Exception {
		nodeProperties.setProperty(propertyName, propertyValue);
	}
}
