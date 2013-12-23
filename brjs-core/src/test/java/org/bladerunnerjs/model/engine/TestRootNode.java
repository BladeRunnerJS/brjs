package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.testing.utility.MockLoggerFactory;
import org.bladerunnerjs.utility.DirectoryIterator;
import org.bladerunnerjs.utility.FileIterator;


public class TestRootNode extends AbstractRootNode
{
	NodeMap<TestChildNode> childNodes = new NodeMap<>(this, TestChildNode.class, null, "^child-");
	NodeMap<TestChildNode> multiLocationChildNodes = new NodeMap<>(this, TestChildNode.class, "set-primary-location", "^child-");
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
	public void registerNode(Node node) {
		try {
			super.registerNode(node);
		}
		catch(NodeAlreadyRegisteredException e) {
			// do nothing -- the node engine test code was designed at a time when we didn't fail fast if you registered multiple nodes for the same directory path
			// additionally, these tests are now of less importance now that the domain model is more thoroughly tested
		}
	};
	
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
	
	@Override
	public FileIterator createFileIterator(File dir, IOFileFilter fileFilter) {
		return null;
	}
	
	@Override
	public DirectoryIterator getDirectoryIterator(File dir) {
		return null;
	}
}
