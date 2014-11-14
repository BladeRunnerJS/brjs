package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.memoization.FileModificationRegistry;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.memoization.MemoizedFileAccessor;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;


public final class TestRootNode extends AbstractRootNode
{
	NodeList<TestChildNode> childNodes = new NodeList<>(this, TestChildNode.class, null, "^child-");
	NodeList<TestChildNode> multiLocationChildNodes = new NodeList<>(this, TestChildNode.class, "set-primary-location", "^child-");
	NodeItem<TestItemNode> itemNode = new NodeItem<>(this, TestItemNode.class, "single-item");
	NodeItem<TestMultiLocationItemNode> multiLocationItemNode = new NodeItem<>(this, TestMultiLocationItemNode.class, "single-item-primary-location");
	private FileModificationRegistry fileModificationRegistry = new FileModificationRegistry(this, new File("."), FalseFileFilter.INSTANCE);
	private final IO io = new IO( FalseFileFilter.INSTANCE );
	private MemoizedFileAccessor memoizedFileAccessor = new MemoizedFileAccessor(this);
	
	public TestRootNode(File dir) throws InvalidSdkDirectoryException
	{
		this(dir, new StubLoggerFactory());
	}
	
	public TestRootNode(File dir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		super(dir, loggerFactory);
		
		multiLocationChildNodes.addAlternateLocation("set-secondary-location", "^child-");
		multiLocationChildNodes.addAdditionalNamedLocation("X", "set-single-item-location");
		multiLocationItemNode.addLegacyLocation("single-item-secondary-location");
	}
	
	@Override
	public boolean isRootDir(File dir)
	{
		return dir.getName().contains("brjs-root-node");
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
	public IO io() {
		return io ;
	}

	@Override
	public FileModificationRegistry getFileModificationRegistry()
	{
		return fileModificationRegistry;
	}

	@Override
	public MemoizedFile getMemoizedFile(File file)
	{
		return memoizedFileAccessor.getMemoizedFile(file);
	}

	@Override
	public MemoizedFile getMemoizedFile(File dir, String filePath)
	{
		return getMemoizedFile( new File(dir, filePath) );
	}
}
