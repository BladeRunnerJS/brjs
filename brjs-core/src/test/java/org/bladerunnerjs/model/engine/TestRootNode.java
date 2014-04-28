package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.StandardFileInfo;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.testing.utility.MockLoggerFactory;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationInfo;


public final class TestRootNode extends AbstractRootNode
{
	NodeList<TestChildNode> childNodes = new NodeList<>(this, TestChildNode.class, null, "^child-");
	NodeList<TestChildNode> multiLocationChildNodes = new NodeList<>(this, TestChildNode.class, "set-primary-location", "^child-");
	NodeItem<TestItemNode> itemNode = new NodeItem<>(this, TestItemNode.class, "single-item");
	NodeItem<TestMultiLocationItemNode> multiLocationItemNode = new NodeItem<>(this, TestMultiLocationItemNode.class, "single-item-primary-location");
	private final IO io = new IO();
	
	public TestRootNode(File dir) throws InvalidSdkDirectoryException
	{
		this(dir, new MockLoggerFactory());
		
		registerInitializedNode();
	}
	
	public TestRootNode(File dir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		super(dir, loggerFactory, null);
		
		multiLocationChildNodes.addAlternateLocation("set-secondary-location", "^child-");
		multiLocationChildNodes.addAdditionalNamedLocation("X", "set-single-item-location");
		multiLocationItemNode.addLegacyLocation("single-item-secondary-location");
		
		registerInitializedNode();
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
		return childNodes.list();
	}
	
	public TestChildNode childNode(String childName)
	{
		return childNodes.item(childName);
	}
	
	public List<TestChildNode> multiLocationChildNodes()
	{
		return multiLocationChildNodes.list();
	}
	
	public TestChildNode multiLocationChildNode(String childName)
	{
		return multiLocationChildNodes.item(childName);
	}
	
	public TestItemNode itemNode()
	{
		return itemNode.item();
	}
	
	public TestMultiLocationItemNode multiLocationItemNode()
	{
		return multiLocationItemNode.item();
	}
	
	@Override
	public FileInfo getFileInfo(File dir) {
		return new StandardFileInfo(dir, null, new PessimisticFileModificationInfo());
	}
	
	@Override
	public IO io() {
		return io ;
	}
}
