package org.bladerunnerjs.model.engine;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.model.engine.BladeRunnerDirectoryException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.events.AppDeployedEvent;
import org.bladerunnerjs.model.events.NodeDiscoveredEvent;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.ObserverList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NodeTest
{
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private static final String TEST_DIR = "src/test/resources/NodeTest";
	private RootNode mockRootNode = new MockRootNode();
	
	@Test
	public void rootNodeIsReturned() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
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
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		
		assertTrue(rootNode.childNode("valid-dir-name").isValidName());
		assertFalse(rootNode.childNode("invalid-dir-name-%$&@").isValidName());
	}
	
	@Test
	public void createPathShouldCauseAllNecesarrySubDirectoriesToBeCreated() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
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
	
	@Test
	public void createPathShouldThrowAnExceptionIfANullDirWasProvided() throws Exception
	{
		exception.expect(RuntimeException.class);
		exception.expectMessage("dir must not be null");
		
		Node node = new TestNode(mockRootNode, null, null);
		node.create();
	}
	
	@Test
	public void deleteShouldCauseTheDirectoryToBeDeletedIfItExists() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
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
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		
		rootDir.mkdir();
		
		assertTrue("temp dir exists", rootDir.exists());
		
		Node node = new TestNode(new TestRootNode(rootDir), null, rootDir);
		node.delete();
		
		assertFalse("temp dir does not exist", rootDir.exists());
	}
	
	@Test
	public void constructingARootNodeShouldLocateTheRootAncestorDirectory() throws Exception
	{
		File rootDir = FileUtils.createTemporaryDirectory( this.getClass(), "brjs-root-node" );
		TestRootNode rootNode = new TestRootNode(new File(rootDir, "child-dir"));
		
		assertEquals(rootDir.getPath(), rootNode.dir().getPath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedIfOneOfTheAncestorsIsACachedNode() throws Exception
	{
		File rootDir = FileUtils.createTemporaryDirectory( this.getClass(), "brjs-root-node" );
		File childDir = new File(rootDir, "child");
		File grandchildDir = new File(childDir, "grandchild");
		File greatGrandchildDir = new File(grandchildDir, "greatgrandchild");
		
		childDir.mkdir();
		grandchildDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		// add node to cache
		TestNode child = new TestNode(rootNode, null, childDir);
		rootNode.registerNode(child);
		
		TestNode locateAncestorNodeOfClass1 = rootNode.locateAncestorNodeOfClass(grandchildDir, TestNode.class);
		assertEquals(childDir.getPath(), locateAncestorNodeOfClass1.dir().getPath());
		
		TestNode locateAncestorNodeOfClass2 = rootNode.locateAncestorNodeOfClass(greatGrandchildDir, TestNode.class);
		assertEquals(childDir.getPath(), locateAncestorNodeOfClass2.dir().getPath());

	}
	
	@Test
	public void locateAncestorNodeOfClassShouldReturnNullIfNoneOfTheAncestorsAreCachedNodes() throws Exception
	{
		File rootDir = FileUtils.createTemporaryDirectory( this.getClass(), "brjs-root-node" );
		File childDir = new File(rootDir, "child");
		File grandchildDir = new File(childDir, "grandchild");
		
		childDir.mkdir();
		grandchildDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertNull(rootNode.locateAncestorNodeOfClass(childDir, TestNode.class));
		assertNull(rootNode.locateAncestorNodeOfClass(grandchildDir, TestNode.class));
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedTheImmediateAncestorIsARootNode() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.registerNode(rootNode);
		File childDir = new File(rootNode.dir(), "child-1");
		
		assertTrue(childDir.exists());
		assertEquals(childDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(childDir, TestChildNode.class).dir().getPath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedIfOneOfTheAncestorsIsARootNode() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.registerNode(rootNode);
		File grandchildDir = new File(TEST_DIR, "brjs-root-node/child-1/grandchild/1");
		
		assertTrue(grandchildDir.exists());
		TestGrandChildNode locatedGrandChild = rootNode.locateAncestorNodeOfClass(grandchildDir, TestGrandChildNode.class);
		assertEquals(grandchildDir.getAbsolutePath(), locatedGrandChild.dir().getPath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldSucceedIfOneOfTheDistantAncestorsIsARootNode() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.registerNode(rootNode);
		File greatGrandchildDir = new File(TEST_DIR, "brjs-root-node/child-1/grandchild/1/2-greatgrandchild");
		
		assertTrue(greatGrandchildDir.exists());
		assertEquals(greatGrandchildDir.getAbsolutePath(), rootNode.locateAncestorNodeOfClass(greatGrandchildDir, TestGreatGrandChildNode.class).dir().getPath());
	}
	
	@Test
	public void locateAncestorNodeOfClassShouldReturnNullIfNoneOfTheAncestorsAreRootNodes() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		File rootParent = new File(TEST_DIR).getParentFile();
		
		assertNull(rootNode.locateAncestorNodeOfClass(rootParent, TestRootNode.class));
	}
	
	@Test
	public void locateFirstAncestorNodeShouldWorkIfGivenTheNodesActualDir() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.registerNode(rootNode);
		assertEquals(rootNode.dir().getPath(), rootNode.locateFirstAncestorNode(rootNode.dir()).dir().getPath());
	}
	
	@Test
	public void locateFirstAncestorNodeShouldWorkIfGivenAChildDir() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.registerNode(rootNode);
		
		assertEquals(rootNode.dir().getPath(), rootNode.locateFirstAncestorNode(rootNode.file("child")).dir().getPath());
	}
	
	@Test
	public void locateFirstAncestorNodeShouldWorkIfGivenAGrandChildDir() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.registerNode(rootNode);
		
		assertEquals(rootNode.dir().getPath(), rootNode.locateFirstAncestorNode(rootNode.file("child")).dir().getPath());
	}
	
	@Test
	public void locateFirstAncestorNodeShouldReturnNullIfGivenAParentDir() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		
		assertNull(rootNode.locateFirstAncestorNode(rootNode.dir().getParentFile()));
	}
	
	@Test
	public void requestingANonExistentChildWorks() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		TestChildNode childNode = rootNode.childNode("non-existent");
		
		assertFalse(childNode.dirExists());
	}
	
	@Test
	public void requestingAnExistentChildWorks() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		TestChildNode childNode = rootNode.childNode("1");
		
		assertTrue(childNode.dirExists());
	}
	
	@Test
	public void theNameOfANonExistentChildNodeIsCorrect() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		TestChildNode childNode = rootNode.childNode("non-existent");
		
		assertEquals("non-existent", childNode.getName());
	}
	
	@Test
	public void theNameOfAnExistentChildNodeIsCorrect() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		TestChildNode childNode = rootNode.childNode("1");
		
		assertEquals("1", childNode.getName());
	}
	
	@Test
	public void requestingAllChildrenWorks() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		List<TestChildNode> children = rootNode.childNodes();
		
		assertEquals("there are two children", 2, children.size());
		assertEquals("child-1", children.get(0).dir().getName());
		assertEquals("child-2", children.get(1).dir().getName());
	}
	
	@Test
	public void requestingAllChildrenWhenSomeOfThemAreCachedWorks() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		TestChildNode child1 = rootNode.childNode("1");
		
		List<TestChildNode> children = rootNode.childNodes();
		
		assertEquals("there are two children", 2, children.size());
		assertEquals("child-1", children.get(0).dir().getName());
		assertEquals("child-2", children.get(1).dir().getName());
		assertEquals(child1, children.get(0));
	}
	
	@Test
	public void requestingAllChildrenDoesntReturnNonExistentItemsThatAreCached() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		rootNode.childNode("non-existent");
		
		List<TestChildNode> children = rootNode.childNodes();
		
		assertEquals("there are two children", 2, children.size());
		assertEquals("child-1", children.get(0).dir().getName());
		assertEquals("child-2", children.get(1).dir().getName());
	}
	
	@Test
	public void requestingAllChildrenAgainReturnsDifferentResultsIfTheFilesOnDiskHaveChanged() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		
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
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
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
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File child1 = new File(rootDir, "child-1");
		File child2 = new File(rootDir, "child2");
		
		rootDir.mkdir();
		child1.mkdir();
		child2.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(1, rootNode.childNodes().size());
	}
	
	@Test
	public void newNodesArentCreatedWhenTheItemIsAlreadyCached() throws Exception
	{
		TestRootNode rootNode = new TestRootNode(new File(TEST_DIR, "brjs-root-node"));
		Node childNode1 = rootNode.childNode("1");
		
		assertSame(childNode1, rootNode.childNode("1"));
		assertSame(childNode1, rootNode.locateAncestorNodeOfClass(rootNode.childNode("1").grandChildNode("2").dir(), TestChildNode.class));
		assertNotSame(childNode1, new TestChildNode(rootNode, childNode1.parentNode(), childNode1.dir(), "1"));
	}
	
	@Test
	public void cachedNonExistentItemsWillLaterBeUsedIfTheItemSubsequentlyComesIntoExistence() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
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
		rootNode.getFileModificationRegistry().incrementFileVersion(child2);
		
		assertEquals(2, rootNode.childNodes().size());
		assertSame(childNode1, rootNode.childNodes().get(0));
		assertSame(childNode2, rootNode.childNodes().get(1));
	}
	
	@Test
	public void existentSingleItemNodesCanBeNavigated() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File itemDir = new File(rootDir, "single-item");
		itemDir.mkdirs();
		TestRootNode rootNode = new TestRootNode(rootDir);
		rootNode.registerNode(rootNode);
		
		Node itemNode = rootNode.locateAncestorNodeOfClass(itemDir, TestItemNode.class);
		
		assertEquals(itemDir.getPath(), rootNode.itemNode().dir().getPath());
		assertSame(itemNode, rootNode.itemNode());
	}
	
	@Test
	public void nonExistentSingleItemNodesCanBeNavigated() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File itemDir = new File(rootDir, "single-item");
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(itemDir.getPath(), rootNode.itemNode().dir().getPath());
	}
	
	@Test
	public void nonExistentSingleItemNodesCanNotBeLocated() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File itemDir = new File(rootDir, "single-item");
		
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		Node singleItemNode = rootNode.locateAncestorNodeOfClass(itemDir, TestItemNode.class);
		assertNull(singleItemNode);
	}
	
	@Test
	public void existentSingleItemNodesCanBeLocated() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File itemDir = new File(rootDir, "single-item");
		
		itemDir.mkdirs();
		TestRootNode rootNode = new TestRootNode(rootDir);
		rootNode.registerNode(rootNode);
		
		Node singleItemNode = rootNode.locateAncestorNodeOfClass(itemDir, TestItemNode.class);
		assertEquals(itemDir.getPath(), singleItemNode.dir().getPath());
	}
	
	@Test
	public void nonExistentMultiLocationSingleItemNodesCanBeNavigated() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		rootDir.mkdir();
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(primaryItemDir.getPath(), rootNode.multiLocationItemNode().dir().getPath());
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeNavigatedWhenAtThePrimaryLocation() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		primaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(primaryItemDir.getPath(), rootNode.multiLocationItemNode().dir().getPath());
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeNavigatedWhenAtTheSecondaryLocation() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File secondaryItemDir = new File(rootDir, "single-item-secondary-location");
		secondaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(secondaryItemDir.getPath(), rootNode.multiLocationItemNode().dir().getPath());
	}
	
	@Test(expected=BladeRunnerDirectoryException.class)
	public void multiLocationSingleItemNodesThrowAnExceptionIfDefinedAtBothLocations() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
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
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		rootDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		Node itemNode = rootNode.locateAncestorNodeOfClass(primaryItemDir, TestMultiLocationItemNode.class);
		
		assertNull(itemNode);
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeLocatedWhenAtThePrimaryLocation() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File primaryItemDir = new File(rootDir, "single-item-primary-location");
		primaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		rootNode.registerNode(rootNode);
		Node itemNode = rootNode.locateAncestorNodeOfClass(primaryItemDir, TestMultiLocationItemNode.class);
		
		assertEquals(primaryItemDir.getPath(), itemNode.dir().getPath());
	}
	
	@Test
	public void multiLocationSingleItemNodesCanBeLocatedWhenAtTheSecondaryLocation() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File secondaryItemDir = new File(rootDir, "single-item-secondary-location");
		secondaryItemDir.mkdirs();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		rootNode.registerNode(rootNode);
		Node itemNode = rootNode.locateAncestorNodeOfClass(secondaryItemDir, TestMultiLocationItemNode.class);
		
		assertEquals(secondaryItemDir.getPath(), itemNode.dir().getPath());
	}
	
	@Test
	public void multiLocationItemSetNodesCanBeNavigatedWhenItemsPresentInOneSideOnly() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File primarySetDir = new File(rootDir, "set-primary-location");
		File child1Dir = new File(primarySetDir, "child-1");
		File child2Dir = new File(primarySetDir, "child-2");
		child1Dir.mkdirs();
		child2Dir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		List<TestChildNode> childNodes = rootNode.multiLocationChildNodes();
		
		assertEquals(2, childNodes.size());
		assertEquals(child1Dir.getPath(), childNodes.get(0).dir().getPath());
		assertEquals(child2Dir.getPath(), childNodes.get(1).dir().getPath());
	}
	
	@Test
	public void multiLocationItemSetCanBeNavigatedWhenListPlusList() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
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
		assertEquals(child1Dir.getPath(), childNodes.get(0).dir().getPath());
		assertEquals(child2Dir.getPath(), childNodes.get(1).dir().getPath());
		assertEquals(childADir.getPath(), childNodes.get(2).dir().getPath());
		assertEquals(childBDir.getPath(), childNodes.get(3).dir().getPath());
	}
	
	@Test
	public void multiLocationItemSetNodesCanBeNavigatedWhenItemPlusList() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
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
		assertEquals(child1Dir.getPath(), childNodes.get(0).dir().getPath());
		assertEquals(child2Dir.getPath(), childNodes.get(1).dir().getPath());
		assertEquals(singleItemSetDir.getPath(), childNodes.get(2).dir().getPath());
		assertSame(childNodes.get(2), rootNode.multiLocationChildNode("X"));
	}
	
	@Test(expected=BladeRunnerDirectoryException.class)
	public void multiLocationItemSetNodesThrowAnExceptionOnNavigationIfTheSameNameIsDefinedTwice() throws Exception
	{
		File tempDir = FileUtils.createTemporaryDirectory( NodeTest.class );
		File rootDir = new File(tempDir, "brjs-root-node");
		File primarySetDir = new File(rootDir, "set-primary-location");
		File singleItemSetDir = new File(rootDir, "set-single-item-location");
		File childXDir = new File(primarySetDir, "child-X");
		childXDir.mkdirs();
		singleItemSetDir.mkdir();
		
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		rootNode.multiLocationChildNodes();
	}
	
	@Test
	public void rootOutputDirShouldBeCorrect() throws Exception
	{
		File rootDir = new File(TEST_DIR, "brjs-root-node");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated").getAbsolutePath(), rootNode.storageDir("").getAbsolutePath());
	}
	
	@Test
	public void childOutputDirShouldBeCorrect() throws Exception
	{
		File rootDir = new File(TEST_DIR, "brjs-root-node");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/itemNode").getAbsolutePath(), rootNode.itemNode().storageDir("").getAbsolutePath());
	}
	
	@Test
	public void namedChildOutputDirShouldBeCorrect() throws Exception
	{
		File rootDir = new File(TEST_DIR, "brjs-root-node");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/childNode/1").getAbsolutePath(), rootNode.childNode("1").storageDir("").getAbsolutePath());
	}
	
	@Test
	public void nestedChildOutputDirsShouldBeCorrect() throws Exception
	{
		File rootDir = new File(TEST_DIR, "brjs-root-node");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/childNode/1/grandChildNode/2").getAbsolutePath(), rootNode.childNode("1").grandChildNode("2").storageDir("").getAbsolutePath());
	}
	
	@Test
	public void outputDirWithChildNodeAndPluginNameShouldBeCorrect() throws Exception
	{
		File rootDir = new File(TEST_DIR, "brjs-root-node");
		TestRootNode rootNode = new TestRootNode(rootDir);
		
		assertEquals(new File(rootDir, "generated/childNode/1/pluginName").getAbsolutePath(), rootNode.childNode("1").storageDir("pluginName").getAbsolutePath());
	}
	
	@Test
	public void observersGetNotifiedOnReady() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		rootNode.ready();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(rootNode) );
	}

	@Test
	public void multipleObserversGetNotifiedOnReady() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
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
	public void parentsObserversGetNotified() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
		TestNode node = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path/to-file" ) );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		node.ready();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(node) );
	}
	
	@Test
	public void notificationsBubbleUpToParentsParent() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
		TestNode midNode = new TestNode(rootNode, rootNode, new File(rootNode.dir(), "path" ) );
		TestNode lowerNode = new TestNode(rootNode, midNode, new File(midNode.dir(), "to-file" ) );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		lowerNode.ready();
		
		verify(observer).onEventEmitted( any(NodeReadyEvent.class), eq(lowerNode) );
	}
	
	@Test
	public void notificationsDontBubbleDown() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
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
	public void notificationsAreCalledOnCorrectObserversForLevelAndInCorrectOrder() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
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
	public void readyIsCalledOnInitIfDirExists() throws Exception
	{
		File nodeDir = new File(TEST_DIR, "brjs-root-node");
		TestRootNode rootNode = new TestRootNode( nodeDir );
		
		assertTrue(nodeDir.exists());

		EventObserver observer = mock(EventObserver.class);
		rootNode.addObserver(NodeReadyEvent.class, observer);
		
		rootNode.registerNode(rootNode);
		
		verify(observer).onEventEmitted( any(NodeDiscoveredEvent.class), eq(rootNode) );
	}
	
	@Test
	public void readyIsNotCalledOnInitIfDirDoesntExist() throws Exception
	{
		File nodeDir = new File(TEST_DIR, "root2");
		TestRootNode rootNode = new TestRootNode( nodeDir );
		EventObserver observer = mock(EventObserver.class);
		
		rootNode.addObserver(observer);
		
		assertFalse(nodeDir.exists());
		rootNode.registerNode(rootNode);
		
		verify(observer).onEventEmitted( any(NodeDiscoveredEvent.class), eq(rootNode) );
		verifyNoMoreInteractions(observer);
	}
	
	@Test
	public void exceptionsFromANodeObserverDoNotGetPropagatedAndAnErrorIsLogged() throws Exception
	{
		LogMessageStore logStore = new LogMessageStore(true);
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node"), new TestLoggerFactory(logStore) );
		
		EventObserver observer = mock(EventObserver.class);
		RuntimeException ex = new RuntimeException();
		doThrow(ex).when(observer).onEventEmitted(any(Event.class), eq(rootNode));
		
		rootNode.addObserver(observer);
		rootNode.ready();
		
		logStore.verifyWarnLogMessage(ObserverList.Messages.NODE_OBSERVER_EXCEPTION_MSG, observer.getClass(), ExceptionUtils.getStackTrace(ex));
	}
	
	@Test
	public void observersOnlyGetNotifiedForCorrectEvents() throws Exception
	{
		TestRootNode rootNode = new TestRootNode( new File(TEST_DIR, "brjs-root-node") );
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
	
	@Test
	public void exceptionShouldBeThrownIfDirIsNull()
	{
		exception.expect(RuntimeException.class);
		exception.expectMessage("dir must not be null");
		
		new TestNode(mockRootNode, null, null);		
	}
	
	// TODO: create 'locator' versions of all the multi-location set-node tests above
	
	
	class MyTestEvent implements Event { }
	class AnotherTestEvent implements Event { }
	
}