package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NodePropertiesCommander {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesCommander(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void setProperty(String propertyName, String propertyValue) throws Exception {
		nodeProperties.setProperty(propertyName, propertyValue);
	}
}
