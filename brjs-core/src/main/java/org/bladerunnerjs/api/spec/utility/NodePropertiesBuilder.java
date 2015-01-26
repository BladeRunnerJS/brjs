package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.engine.NodeProperties;


public class NodePropertiesBuilder {
	private final NodeProperties nodeProperties;
	
	public NodePropertiesBuilder(SpecTest modelTest, NodeProperties nodeProperties) {
		this.nodeProperties = nodeProperties;
	}
	
	public void persistentPropertyHasBeenSet(String propertyName, String propertyValue) throws Exception {
		nodeProperties.setPersisentProperty(propertyName, propertyValue);
	}

	public void transientPropertyHasBeenSet(String propertyName, Object propertyValue)
	{
		nodeProperties.setTransientProperty(propertyName, propertyValue);
	}
}
