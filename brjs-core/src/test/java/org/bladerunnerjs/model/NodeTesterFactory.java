package org.bladerunnerjs.model;

import org.bladerunnerjs.model.BRJSNode;

public class NodeTesterFactory<PN extends BRJSNode>
{
	private PN parentNode;
	private Class<PN> parentNodeClass;
	
	public NodeTesterFactory(PN parentNode, Class<PN> parentNodeClass)
	{
		this.parentNode = parentNode;
		this.parentNodeClass = parentNodeClass;
	}
	
	public <CN extends BRJSNode> NodeSetModelTester<PN, CN> createSetTester(Class<CN> childNodeClass, String childrenMethodName, String childMethodName)
	{
		return new NodeSetModelTester<>(parentNode, parentNodeClass, childNodeClass, childrenMethodName, childMethodName);
	}
	
	public <CN extends BRJSNode> NodeItemModelTester<PN, CN> createItemTester(Class<CN> childNodeClass, String itemMethodName, String childPath)
	{
		return new NodeItemModelTester<>(parentNode, parentNodeClass, childNodeClass, itemMethodName, childPath);
	}
}