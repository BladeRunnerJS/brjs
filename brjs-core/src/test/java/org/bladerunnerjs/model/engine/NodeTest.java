package org.bladerunnerjs.model.engine;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.model.engine.BladeRunnerDirectoryException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.events.AppDeployedEvent;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.ObserverList;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NodeTest
{
	private static final String TEST_DIR = "src/test/resources/NodeTest";
	private RootNode mockRootNode = new MockRootNode();
	
	@Test
	public void rootNodeIsReturned()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		TestNode midNode = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path" ) );
		TestNode lowerNode = new TestNode(rootNode, midNode, new File(midNode.dir(), "to-file" ) );
		
		assertEquals( "wrong parent node returned", rootNode, lowerNode.root());
	}
	
	@Test
	public void fileShouldBeASubPathOfTheGivenDir()
	{
		Node node = new TestNode(mockRootNode, null, new File(TEST_DIR));
		
		assertEquals(new File(node.dir(), "path/to-file").getAbsolutePath(), node.file("path/to-file").getAbsolutePath());
	}
	
	@Test
	public void fileShouldReturnAnUnrootedFileIfDirIsNull()
	{
		Node node = new TestNode(mockRootNode, null, null);
		
		assertEquals(new File("path/to-file").getAbsolutePath(), node.file("path/to-file").getAbsolutePath());
	}
	
	@Test
	public void dirExistsShouldReturnTrueIfTheDirectoryExists()
	{
		Node node = new TestNode(mockRootNode, null, new File(TEST_DIR));
		
		assertTrue(node.dirExists());
	}
	
	@Test
	public void dirExistsShouldReturnFalseIfTheDirectoryDoesNotExist()
	{
		Node node = new TestNode(mockRootNode, null, new File(TEST_DIR, "non-existent-directory"));
		
		assertFalse(node.dirExists());
	}
	
	@Test
	public void dirExistsShouldNotThrowAnExceptionIfANullDirWasProvided()
	{
		Node node = new TestNode(mockRootNode, null, null);
		
		assertFalse(node.dirExists());
	}
	
	@Test
	public void containsFileShouldNotThrowAnExceptionIfDirIsNull()
	{
		Node node = new TestNode(mockRootNode, null, null);
		
		assertFalse(node.containsFile("some-file.txt"));
	}
	
	@Test
	public void containsFileShouldReturnTrueIfTheFileExists()
	{
		Node node = new TestNode(mockRootNode, null, new File(TEST_DIR));
		
		assertTrue(node.containsFile("some-file.txt"));
	}
	
	@Test
	public void containsFileShouldReturnFalseIfTheFileDoesNotExist()
	{
		Node node = new TestNode(mockRootNode, null, new File(TEST_DIR));
		
		assertFalse(node.containsFile("non-existent-file.txt"));
	}
	
	@Test
	public void validDirNamesCanBeCheckedBeforehand() throws Exception {
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		
		assertTrue(rootNode.childNode("valid-dir-name").isValidName());
		assertFalse(rootNode.childNode("invalid-dir-name-%$&@").isValidName());
	}
	
	@Test
	public void createPathShouldCauseAllNecesarrySubDirectoriesToBeCreated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File childDir = new File(tempDir, "child");
		File grandchildDir = new File(childDir, "grandchild");
		File greatGrandchildDir = new File(grandchildDir, "great-grandchild");
		
		assertTrue("temp dir exists", tempDir.exists());
		assertFalse("child dir does not exist", childDir.exists());
		assertFalse("grandchild dir does not exist", grandchildDir.exists());
		assertFalse("great grandchild dir does not exist", greatGrandchildDir.exists());
		
		Node node = new TestNode(mockRootNode, mockRootNode, grandchildDir);
		node.create();
		
		assertTrue("temp dir exists", tempDir.exists());
		assertTrue("child dir exists", childDir.exists());
		assertTrue("grandchild dir exists", grandchildDir.exists());
		assertFalse("great grandchild dir does not exist", greatGrandchildDir.exists());
	}
	
	@Test(expected=NullPointerException.class)
	public void createPathShouldThrowAnExceptionIfANullDirWasProvided() throws Exception
	{
		Node node = new TestNode(mockRootNode, null, null);
		node.create();
	}
	
	@Test
	public void deleteShouldCauseTheDirectoryToBeDeletedIfItExists() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File childDir = new File(rootDir, "child");
		
		childDir.mkdirs();
		
		assertTrue("temp dir exists", rootDir.exists());
		assertTrue("child dir exists", childDir.exists());
		
		Node node = new TestNode(new TestRootNode(rootDir), null, rootDir);
		node.delete();
		
		assertFalse("temp dir does not exist", rootDir.exists());
		assertFalse("child dir does not exist", childDir.exists());
	}
	
	@Test
	public void deleteShouldWorkEvenWhenTheDirectoryIsNonEmpty() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		
		rootDir.mkdir();
		
		assertTrue("temp dir exists", rootDir.exists());
		
		Node node = new TestNode(new TestRootNode(rootDir), null, rootDir);
		node.delete();
		
		assertFalse("temp dir does not exist", rootDir.exists());
	}
	
	@Test
	public void constructingARootNodeShouldLocateTheRootAncestorDirectory() throws Exception
	{
		File rootDir = FileUtility.createTemporaryDirectory("root");
		TestRootNode rootNode = new TestRootNode(new File(rootDir, "child-dir"));
		
		assertEquals(rootDir.getAbsolutePath(), rootNode.dir().getAbsolutePath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedIfOneOfTheAncestorsIsACachedNode() throws Exception
	{
		File rootDir = FileUtility.createTemporaryDirectory("root");
		File childDir = new File(rootDir, "child");
		File grandchildDir = new File(childDir, "grandchild");
		File greatGrandchildDir = new File(grandchildDir, "greatgrandchild");
		
		childDir.mkdir();
		grandchildDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		// add node to cache
		new TestNode(rootNode, null, childDir);
		
		assertEquals(childDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(grandchildDir, TestNode.class).dir().getAbsolutePath());
		assertEquals(childDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(greatGrandchildDir, TestNode.class).dir().getAbsolutePath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldReturnNullIfNoneOfTheAncestorsAreCachedNodes() throws Exception
	{
		File rootDir = FileUtility.createTemporaryDirectory("root");
		File childDir = new File(rootDir, "child");
		File grandchildDir = new File(childDir, "grandchild");
		
		childDir.mkdir();
		grandchildDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertNull(rootNode.locateAncestorNodeOfClass(childDir, TestNode.class));
		assertNull(rootNode.locateAncestorNodeOfClass(grandchildDir, TestNode.class));
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedTheImmediateAncestorIsARootNode()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		File childDir = new File(rootNode.dir(), "child-1");
		
		assertTrue(childDir.exists());
		assertEquals(childDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(childDir, TestChildNode.class).dir().getAbsolutePath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedIfOneOfTheAncestorsIsARootNode()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		File grandchildDir = new File(TEST_DIR, "root/child-1/grandchild/1");
		
		assertTrue(grandchildDir.exists());
		assertEquals(grandchildDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(grandchildDir, TestGrandChildNode.class).dir().getAbsolutePath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedIfOneOfTheDistantAncestorsIsARootNode()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		File greatGrandchildDir = new File(TEST_DIR, "root/child-1/grandchild/1/2-greatgrandchild");
		
		assertTrue(greatGrandchildDir.exists());
		assertEquals(greatGrandchildDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(greatGrandchildDir, TestGreatGrandChildNode.class).dir().getAbsolutePath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldReturnNullIfNoneOfTheAncestorsAreRootNodes()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		File rootParent = new File(TEST_DIR).getParentFile();
		
		assertNull(rootNode.locateAncestorNodeOfClass(rootParent, TestRootNode.class));
	}
	
	@Test
	public void locateFirstAncestorNodeShouldWorkIfGivenTheNodesActualDir()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		
		assertEquals(rootNode.dir().getAbsolutePath(), rootNode.locateFirstAncestorNode(rootNode.dir()).dir().getAbsolutePath());
	}
	
	@Test
	public void locateFirstAncestorNodeShouldWorkIfGivenAChildDir()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		
		assertEquals(rootNode.dir().getAbsolutePath(), rootNode.locateFirstAncestorNode(rootNode.file("child")).dir().getAbsolutePath());
	}
	
	@Test
	public void locateFirstAncestorNodeShouldWorkIfGivenAGrandChildDir()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		
		assertEquals(rootNode.dir().getAbsolutePath(), rootNode.locateFirstAncestorNode(rootNode.file("child")).dir().getAbsolutePath());
	}
	
	@Test
	public void locateFirstAncestorNodeShouldReturnNullIfGivenAParentDir()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		
		assertNull(rootNode.locateFirstAncestorNode(rootNode.dir().getParentFile()));
	}
	
	@Test
	public void requestingANonExistentChildWorks()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		TestChildNode childNode = rootNode.childNode("non-existent");
		
		assertFalse(childNode.dirExists());
	}
	
	@Test
	public void requestingAnExistentChildWorks()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		TestChildNode childNode = rootNode.childNode("1");
		
		assertTrue(childNode.dirExists());
	}
	
	@Test
	public void theNameOfANonExistentChildNodeIsCorrect()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		TestChildNode childNode = rootNode.childNode("non-existent");
		
		assertEquals("non-existent", childNode.getName());
	}
	
	@Test
	public void theNameOfAnExistentChildNodeIsCorrect()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		TestChildNode childNode = rootNode.childNode("1");
		
		assertEquals("1", childNode.getName());
	}
	
	@Test
	public void requestingAllChildrenWorks()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		List<TestChildNode> children = rootNode.childNodes();
		
		assertEquals("there are two children", 2, children.size());
		assertEquals("child-1", children.get(0).dir().getName());
		assertEquals("child-2", children.get(1).dir().getName());
	}
	
	@Test
	public void requestingAllChildrenWhenSomeOfThemAreCachedWorks()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		TestChildNode child1 = rootNode.childNode("1");
		
		List<TestChildNode> children = rootNode.childNodes();
		
		assertEquals("there are two children", 2, children.size());
		assertEquals("child-1", children.get(0).dir().getName());
		assertEquals("child-2", children.get(1).dir().getName());
		assertEquals(child1, children.get(0));
	}
	
	@Test
	public void requestingAllChildrenDoesntReturnNonExistentItemsThatAreCached()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		rootNode.childNode("non-existent");
		
		List<TestChildNode> children = rootNode.childNodes();
		
		assertEquals("there are two children", 2, children.size());
		assertEquals("child-1", children.get(0).dir().getName());
		assertEquals("child-2", children.get(1).dir().getName());
	}
	
	@Test
	public void requestingAllChildrenAgainReturnsDifferentResultsIfTheFilesOnDiskHaveChanged() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		rootNode.childNode("1").create();
		rootNode.childNode("2").create();
		assertEquals("before dir change", 2, rootNode.childNodes().size());
		
		rootNode.childNode("3").create();
		assertEquals("after dir change", 3, rootNode.childNodes().size());
	}
	
	@Test
	public void theChildrenOnlyIncludeDirectories() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File child1 = new File(rootDir, "child-1");
		File child2 = new File(rootDir, "child-2");
		
		rootDir.mkdir();
		child1.mkdir();
		child2.createNewFile();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(1, rootNode.childNodes().size());
	}
	
	@Test
	public void theChildrenOnlyIncludeDirectoriesThatMatchTheFileNameFilter() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File child1 = new File(rootDir, "child-1");
		File child2 = new File(rootDir, "child2");
		
		rootDir.mkdir();
		child1.mkdir();
		child2.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(1, rootNode.childNodes().size());
	}
	
	@Test
	public void newNodesArentCreatedWhenTheItemIsAlreadyCached()
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "root"));
		Node childNode1 = rootNode.childNode("1");
		
		assertSame(childNode1, rootNode.childNode("1"));
		assertSame(childNode1, rootNode.locateAncestorNodeOfClass(rootNode.childNode("1").grandChildNode("2").dir(), TestChildNode.class));
		assertNotSame(childNode1, new TestChildNode(rootNode, childNode1.parentNode(), childNode1.dir(), "1"));
	}
	
	@Test
	public void cachedNonExistentItemsWillLaterBeUsedIfTheItemSubsequentlyComesIntoExistence() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File child1 = new File(rootDir, "child-1");
		File child2 = new File(rootDir, "child-2");
		
		rootDir.mkdir();
		child1.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		Node childNode1 = rootNode.childNode("1");
		Node childNode2 = rootNode.childNode("2");
		
		assertEquals(1, rootNode.childNodes().size());
		assertSame(childNode1, rootNode.childNodes().get(0));
		
		child2.mkdir();
		
		assertEquals(2, rootNode.childNodes().size());
		assertSame(childNode1, rootNode.childNodes().get(0));
		assertSame(childNode2, rootNode.childNodes().get(1));
	}
	
	@Test
	public void existentSingleItemNodesCanBeNavigated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File itemDir = new File(rootDir, "single-item");
		itemDir.mkdirs();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		Node itemNode = rootNode.locateAncestorNodeOfClass(itemDir, TestItemNode.class);
		
		assertEquals(itemDir.getAbsolutePath(), rootNode.itemNode().dir().getAbsolutePath());
		assertSame(itemNode, rootNode.itemNode());
	}
	
	@Test
	public void nonExistentSingleItemNodesCanBeNavigated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File itemDir = new File(rootDir, "single-item");
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(itemDir.getAbsolutePath(), rootNode.itemNode().dir().getAbsolutePath());
	}
	
	@Test
	public void nonExistentSingleItemNodesCanNotBeLocated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File itemDir = new File(rootDir, "single-item");
		
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		Node singleItemNode = rootNode.locateAncestorNodeOfClass(itemDir, TestItemNode.class);
		assertNull(singleItemNode);
	}
	
	@Test
	public void existentSingleItemNodesCanBeLocated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File itemDir = new File(rootDir, "single-item");
		
		itemDir.mkdirs();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		Node singleItemNode = rootNode.locateAncestorNodeOfClass(itemDir, TestItemNode.class);
		assertEquals(itemDir.getAbsolutePath(), singleItemNode.dir().getAbsolutePath());
	}
	
	@Test
	public void nonExistentMultiLocationSingleItemNodesCanBeNavigated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(primaryItemDir.getAbsolutePath(), rootNode.multiLocationItemNode().dir().getAbsolutePath());
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeNavigatedWhenAtThePrimaryLocation() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		primaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(primaryItemDir.getAbsolutePath(), rootNode.multiLocationItemNode().dir().getAbsolutePath());
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeNavigatedWhenAtTheSecondaryLocation() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File secondaryItemDir = new File(rootDir, "single-item-secondary-location");
		secondaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(secondaryItemDir.getAbsolutePath(), rootNode.multiLocationItemNode().dir().getAbsolutePath());
	}
	
	@Test(expected=BladeRunnerDirectoryException.class)
	public void multiLocationSingleItemNodesThrowAnExceptionIfDefinedAtBothLocations() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		File secondaryItemDir = new File(rootDir, "single-item-secondary-location");
		primaryItemDir.mkdirs();
		secondaryItemDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		rootNode.multiLocationItemNode();
	}
	
	@Test
	public void nonExistentMultiLocationSingleItemNodesCanNotBeLocated() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		rootDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		Node itemNode = rootNode.locateAncestorNodeOfClass(primaryItemDir, TestMultiLocationItemNode.class);
		
		assertNull(itemNode);
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeLocatedWhenAtThePrimaryLocation() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		primaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		Node itemNode = rootNode.locateAncestorNodeOfClass(primaryItemDir, TestMultiLocationItemNode.class);
		
		assertEquals(primaryItemDir.getAbsolutePath(), itemNode.dir().getAbsolutePath());
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeLocatedWhenAtTheSecondaryLocation() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File secondaryItemDir = new File(rootDir, "single-item-secondary-location");
		secondaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		Node itemNode = rootNode.locateAncestorNodeOfClass(secondaryItemDir, TestMultiLocationItemNode.class);
		
		assertEquals(secondaryItemDir.getAbsolutePath(), itemNode.dir().getAbsolutePath());
	}
	
	@Test
	public void multiLocationItemSetNodesCanBeNavigatedWhenItemsPresentInOneSideOnly() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primarySetDir = new File(rootDir, "set-primary-location");
		File child1Dir = new File(primarySetDir, "child-1");
		File child2Dir = new File(primarySetDir, "child-2");
		child1Dir.mkdirs();
		child2Dir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		List<TestChildNode> childNodes = rootNode.multiLocationChildNodes();
		
		assertEquals(2, childNodes.size());
		assertEquals(child1Dir.getAbsolutePath(), childNodes.get(0).dir().getAbsolutePath());
		assertEquals(child2Dir.getAbsolutePath(), childNodes.get(1).dir().getAbsolutePath());
	}
	
	@Test
	public void multiLocationItemSetCanBeNavigatedWhenListPlusList() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primarySetDir = new File(rootDir, "set-primary-location");
		File secondarySetDir = new File(rootDir, "set-secondary-location");
		File child1Dir = new File(primarySetDir, "child-1");
		File child2Dir = new File(primarySetDir, "child-2");
		File childADir = new File(secondarySetDir, "child-A");
		File childBDir = new File(secondarySetDir, "child-B");
		child1Dir.mkdirs();
		child2Dir.mkdir();
		childADir.mkdirs();
		childBDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		List<TestChildNode> childNodes = rootNode.multiLocationChildNodes();
		
		assertEquals(4, childNodes.size());
		assertEquals(child1Dir.getAbsolutePath(), childNodes.get(0).dir().getAbsolutePath());
		assertEquals(child2Dir.getAbsolutePath(), childNodes.get(1).dir().getAbsolutePath());
		assertEquals(childADir.getAbsolutePath(), childNodes.get(2).dir().getAbsolutePath());
		assertEquals(childBDir.getAbsolutePath(), childNodes.get(3).dir().getAbsolutePath());
	}
	
	@Test
	public void multiLocationItemSetNodesCanBeNavigatedWhenItemPlusList() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primarySetDir = new File(rootDir, "set-primary-location");
		File singleItemSetDir = new File(rootDir, "set-single-item-location");
		File child1Dir = new File(primarySetDir, "child-1");
		File child2Dir = new File(primarySetDir, "child-2");
		child1Dir.mkdirs();
		child2Dir.mkdir();
		singleItemSetDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		List<TestChildNode> childNodes = rootNode.multiLocationChildNodes();
		
		assertEquals(3, childNodes.size());
		assertEquals(child1Dir.getAbsolutePath(), childNodes.get(0).dir().getAbsolutePath());
		assertEquals(child2Dir.getAbsolutePath(), childNodes.get(1).dir().getAbsolutePath());
		assertEquals(singleItemSetDir.getAbsolutePath(), childNodes.get(2).dir().getAbsolutePath());
		assertSame(childNodes.get(2), rootNode.multiLocationChildNode("X"));
	}
	
	@Test(expected=BladeRunnerDirectoryException.class)
	public void multiLocationItemSetNodesThrowAnExceptionOnNavigationIfTheSameNameIsDefinedTwice() throws Exception
	{
		File tempDir = FileUtility.createTemporaryDirectory("node-test");
		File rootDir = new File(tempDir, "root");
		File primarySetDir = new File(rootDir, "set-primary-location");
		File singleItemSetDir = new File(rootDir, "set-single-item-location");
		File childXDir = new File(primarySetDir, "child-X");
		childXDir.mkdirs();
		singleItemSetDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		rootNode.multiLocationChildNodes();
	}
	
	@Test
	public void rootOutputDirShouldBeCorrect()
	{
		File rootDir = new File(TEST_DIR, "root");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated").getAbsolutePath(), rootNode.storageDir("").getAbsolutePath());
	}
	
	@Test
	public void childOutputDirShouldBeCorrect()
	{
		File rootDir = new File(TEST_DIR, "root");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/itemNode").getAbsolutePath(), rootNode.itemNode().storageDir("").getAbsolutePath());
	}
	
	@Test
	public void namedChildOutputDirShouldBeCorrect()
	{
		File rootDir = new File(TEST_DIR, "root");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/childNode/1").getAbsolutePath(), rootNode.childNode("1").storageDir("").getAbsolutePath());
	}
	
	@Test
	public void nestedChildOutputDirsShouldBeCorrect()
	{
		File rootDir = new File(TEST_DIR, "root");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/childNode/1/grandChildNode/2").getAbsolutePath(), rootNode.childNode("1").grandChildNode("2").storageDir("").getAbsolutePath());
	}
	
	@Test
	public void outputDirWithChildNodeAndPluginNameShouldBeCorrect()
	{
		File rootDir = new File(TEST_DIR, "root");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/childNode/1/pluginName").getAbsolutePath(), rootNode.childNode("1").storageDir("pluginName").getAbsolutePath());
	}
	
	@Test
	public void observersGetNotifiedOnReady()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		rootNode.ready();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(rootNode) );
	}

	@Test
	public void multipleObserversGetNotifiedOnReady()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		EventObserver observer1 = mock(EventObserver.class);
		EventObserver observer2 = mock(EventObserver.class);
		
		rootNode.addObserver(observer1);
		rootNode.addObserver(observer2);
		rootNode.ready();
		
		InOrder inOrder = inOrder(observer1, observer2);
		inOrder.verify(observer1).onEventEmitted( any(NodeReadyEvent.class), eq(rootNode) );
		inOrder.verify(observer2).onEventEmitted( any(NodeReadyEvent.class), eq(rootNode) );
	}
	
	@Test
	public void parentsObserversGetNotified ()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		TestNode node = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path/to-file" ) );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		node.ready();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(node) );
	}
	
	@Test
	public void notificationsBubbleUpToParentsParent ()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		TestNode midNode = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path" ) );
		TestNode lowerNode = new TestNode(rootNode, midNode, new File(midNode.dir(), "to-file" ) );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		lowerNode.ready();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(lowerNode) );
	}
	
	@Test
	public void notificationsDontBubbleDown ()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		TestNode midNode = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path" ) );
		TestNode lowerNode = new TestNode(rootNode, midNode, new File(midNode.dir(), "to-file" ) );
		
		EventObserver rootObserver = mock(EventObserver.class);
		EventObserver lowerObserver = mock(EventObserver.class);
		
		rootNode.addObserver(rootObserver);
		lowerNode.addObserver(lowerObserver);
		midNode.ready();
		
		verify(rootObserver).onEventEmitted( any(NodeReadyEvent.class), eq(midNode) );
		verifyZeroInteractions(lowerObserver);
	}
	
	@Test
	public void notificationsAreCalledOnCorrectObserversForLevelAndInCorrectOrder ()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		TestNode midNode = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path" ) );
		TestNode lowerNode = new TestNode(rootNode, midNode, new File(midNode.dir(), "to-file" ) );
		
		EventObserver rootObserver = mock(EventObserver.class);
		EventObserver midObserver = mock(EventObserver.class);
		EventObserver lowerObserver = mock(EventObserver.class);
		
		rootNode.addObserver(rootObserver);
		midNode.addObserver(midObserver);
		lowerNode.addObserver(lowerObserver);
		lowerNode.ready();
		
		InOrder inOrder = inOrder(rootObserver, midObserver, lowerObserver);
		inOrder.verify(lowerObserver).onEventEmitted( any(NodeReadyEvent.class), eq(lowerNode) );
		inOrder.verify(midObserver).onEventEmitted( any(NodeReadyEvent.class), eq(lowerNode) );
		inOrder.verify(rootObserver).onEventEmitted( any(NodeReadyEvent.class), eq(lowerNode) );
	}
	
	@Test
	public void readyIsCalledOnInitIfDirExists ()
	{
		File nodeDir = new File(TEST_DIR, "root");
		TestRootNode rootNode = new TestRootNode( nodeDir );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		
		assertTrue(nodeDir.exists());
		rootNode.registerNode();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(rootNode) );
	}
	
	@Test
	public void readyIsNotCalledOnInitIfDirDoesntExist ()
	{
		File nodeDir = new File(TEST_DIR, "root2");
		TestRootNode rootNode = new TestRootNode( nodeDir );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		
		assertFalse(nodeDir.exists());
		rootNode.registerNode();
		
		verifyNoMoreInteractions(observer);
	}
	
	@Test
	public void exceptionsFromANodeObserverDoNotGetPropagatedAndAnErrorIsLogged()
	{
		LogMessageStore logStore = new LogMessageStore(true);
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root"), new TestLoggerFactory(logStore) );
		
		EventObserver observer = mock(EventObserver.class);
		RuntimeException ex = new RuntimeException();
		doThrow(ex).when(observer).onEventEmitted(any(Event.class), eq(rootNode));
		
		rootNode.addObserver(observer);
		rootNode.ready();
		
		logStore.verifyWarnLogMessage(ObserverList.Messages.NODE_OBSERVER_EXCEPTION_MSG, observer.getClass(), ExceptionUtils.getStackTrace(ex));
	}
	
	@Test
	public void observersOnlyGetNotifiedForCorrectEvents()
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "root") );
		TestNode midNode = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path" ) );
		TestNode lowerNode = new TestNode(rootNode, midNode, new File(midNode.dir(), "to-file" ) );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(MyTestEvent.class, observer);
		lowerNode.notifyObservers(new MyTestEvent(), lowerNode);
		lowerNode.notifyObservers(new AnotherTestEvent(), lowerNode);
		
		verify(observer).onEventEmitted( any(MyTestEvent.class), eq(lowerNode) );
		verifyNoMoreInteractions(observer);
	}
	
	@Test
	public void notifiesObserversListeningForEventsWhenNodeDeployedEventEmitted()
	{
		Class<? extends Event> listenForEvent = Event.class;
		Event theEvent = new NodeReadyEvent();

		ObserverList observers = new ObserverList();
		Node node = mock(Node.class);				
		EventObserver eventObserver = mock(EventObserver.class);
		observers.add(listenForEvent, eventObserver);
		
		observers.eventEmitted( theEvent, node);
		
		verify(eventObserver).onEventEmitted(theEvent, node);
	}
	
	@Test
	public void notifiesObserversListeningForAppDeployedEventsWhenAppDeployedEventEmitted()
	{
		Class<? extends Event> listenForEvent = AppDeployedEvent.class;
		Event theEvent = new AppDeployedEvent();

		ObserverList observers = new ObserverList();
		Node node = mock(Node.class);				
		EventObserver eventObserver = mock(EventObserver.class);
		observers.add(listenForEvent, eventObserver);
		
		observers.eventEmitted( theEvent, node);
		
		verify(eventObserver).onEventEmitted(theEvent, node);
	}
	
	@Test
	public void doesntNotifyObserversListeningforNodeDeployedWhenNodeReadyEmitted()
	{
		Class<? extends Event> listenForEvent = AppDeployedEvent.class;
		Event theEvent = new NodeReadyEvent();
		
		ObserverList observers = new ObserverList();
		Node node = mock(Node.class);				
		EventObserver eventObserver = mock(EventObserver.class);
		observers.add(listenForEvent, eventObserver);
		
		observers.eventEmitted( theEvent, node);
		
		verifyNoMoreInteractions(eventObserver);
	}
	
	@Test
	public void doesntNotifyObserversListeningforNodeDeployedWhenEventEmitted()
	{
		Class<? extends Event> listenForEvent = AppDeployedEvent.class;
		Event theEvent = new Event(){};
		
		ObserverList observers = new ObserverList();
		Node node = mock(Node.class);				
		EventObserver eventObserver = mock(EventObserver.class);
		observers.add(listenForEvent, eventObserver);
		
		observers.eventEmitted( theEvent, node);
		
		verifyNoMoreInteractions(eventObserver);
	}
	
	
	// TODO: create 'locator' versions of all the multi-location set-node tests above
	
	
	class MyTestEvent implements Event { }
	class AnotherTestEvent implements Event { }
	
}