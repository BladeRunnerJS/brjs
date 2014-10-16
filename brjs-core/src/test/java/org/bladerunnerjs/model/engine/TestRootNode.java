package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.memoization.FileModificationRegistry;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.memoization.MemoizedFileAccessor;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.StandardFileInfo;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationInfo;
import org.bladerunnerjs.utility.filemodification.TestTimeAccessor;
import org.bladerunnerjs.utility.filemodification.TimeAccessor;


public final class TestRootNode extends AbstractRootNode
{
	NodeList<TestChildNode> childNodes = new NodeList<>(this, TestChildNode.class, null, "^child-");
	NodeList<TestChildNode> multiLocationChildNodes = new NodeList<>(this, TestChildNode.class, "set-primary-location", "^child-");
	NodeItem<TestItemNode> itemNode = new NodeItem<>(this, TestItemNode.class, "single-item");
	NodeItem<TestMultiLocationItemNode> multiLocationItemNode = new NodeItem<>(this, TestMultiLocationItemNode.class, "single-item-primary-location");
	private TimeAccessor timeAccessor = new TestTimeAccessor();
	private FileModificationRegistry fileModificationRegistry = new FileModificationRegistry(new File("."));
	private final IO io = new IO();
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
	public void registerNode(Node node) {
		try {
			super.registerNode(node);
		}
		catch(NodeAlreadyRegisteredException ex) {
			throw new RuntimeException(ex);
		}
	};
	
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
	public FileInfo getFileInfo(File dir) {
		return new StandardFileInfo(dir, new PessimisticFileModificationInfo(dir, null, timeAccessor), null, null);
	}
	
	@Override
	public FileInfo getFileSetInfo(File file, File primarySetFile) {
		return getFileInfo(file);
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
	
}
