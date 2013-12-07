package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.testing.utility.MockLoggerFactory;


public class TestRootNode extends AbstractRootNode
{
	NodeMap<TestChildNode> childNodes = new NodeMap<>(TestChildNode.class, null, "^child-");
	NodeMap<TestChildNode> multiLocationChildNodes = new NodeMap<>(TestChildNode.class, "set-primary-location", "^child-");
	NodeItem<TestItemNode> itemNode = new NodeItem<>(TestItemNode.class, "single-item");
	NodeItem<TestMultiLocationItemNode> multiLocationItemNode = new NodeItem<>(TestMultiLocationItemNode.class, "single-item-primary-location");
	
	public TestRootNode(File dir)
	{
		this(dir, new MockLoggerFactory());
	}
	
	public TestRootNode(File dir, LoggerFactory loggerFactory)
	{
		super(dir, loggerFactory, null);
		
		multiLocationChildNodes.addAlternateLocation("set-secondary-location", "^child-");
		multiLocationChildNodes.addAdditionalNamedLocation("X", "set-single-item-location");
		multiLocationItemNode.addLegacyLocation("single-item-secondary-location");
	}
	
	@Override
	public boolean isRootDir(File dir)
	{
		return dir.getName().startsWith("root");
	}
	
	public List<TestChildNode> childNodes()
	{
		return children(childNodes);
	}
	
	public TestChildNode childNode(String childName)
	{
		return child(childNodes, childName);
	}
	
	public List<TestChildNode> multiLocationChildNodes()
	{
		return children(multiLocationChildNodes);
	}
	
	public TestChildNode multiLocationChildNode(String childName)
	{
		return child(multiLocationChildNodes, childName);
	}
	
	public TestItemNode itemNode()
	{
		return item(itemNode);
	}
	
	public TestMultiLocationItemNode multiLocationItemNode()
	{
		return item(multiLocationItemNode);
	}
}
