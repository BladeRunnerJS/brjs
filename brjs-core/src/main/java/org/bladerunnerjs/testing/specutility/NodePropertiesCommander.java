package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class NodePropertiesCommander {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesCommander(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void setPersisentProperty(String propertyName, String propertyValue) throws Exception {
		nodeProperties.setPersisentProperty(propertyName, propertyValue);
	}

	public void setTransientProperty(String propertyName, String propertyValue)
	{
		nodeProperties.setTransientProperty(propertyName, propertyValue);
	}
}
