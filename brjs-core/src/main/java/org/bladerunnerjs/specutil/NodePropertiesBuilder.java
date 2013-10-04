package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NodePropertiesBuilder {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesBuilder(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void propertyHasBeenSet(String propertyName, String propertyValue) throws Exception {
		nodeProperties.setProperty(propertyName, propertyValue);
	}
}
