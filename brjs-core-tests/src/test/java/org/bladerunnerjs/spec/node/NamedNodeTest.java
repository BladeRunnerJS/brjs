package org.bladerunnerjs.spec.node;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;

import java.util.Arrays;
import java.util.Collection;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.events.NodeCreatedEvent;
import org.bladerunnerjs.api.model.events.NodeDeletedEvent;
import org.bladerunnerjs.api.model.events.NodeDiscoveredEvent;
import org.bladerunnerjs.api.model.events.NodeReadyEvent;
import org.bladerunnerjs.api.model.exception.modelupdate.DirectoryAlreadyExistsModelException;
import org.bladerunnerjs.api.model.exception.modelupdate.NoSuchDirectoryException;
import org.bladerunnerjs.api.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.BRJSNode;
import org.bladerunnerjs.model.NamedDirNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class NamedNodeTest extends SpecTest {
	private NamedNode node;
	private NamedNode badNode;
	private NamedDirNode nodeTemplate;
	private NamedNodeFactory namedNodeFactory;
	
	private String getTemplateName() {
		return ((BRJSNode) node).getTemplateName();
	}
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasBeenCreated();
			node = namedNodeFactory.createNamedNode(brjs, "node");
			badNode = namedNodeFactory.createNamedNode(brjs, "!$%&");
			nodeTemplate = brjs.sdkTemplateGroup("default").template(getTemplateName());
	}
	
	public NamedNodeTest(String testName, NamedNodeFactory namedNodeFactory) {
		this.namedNodeFactory = namedNodeFactory;
	}
	
	@Parameters(name="{0}")
	public static Collection<Object[]> namedNodeFactories() {
		NamedNodeFactory appFactory = new NamedNodeFactory() {
			@Override
			public NamedNode createNamedNode(BRJS brjs, String nodeName) {
				return brjs.app(nodeName);
			}
		};
		NamedNodeFactory aspectFactory = new NamedNodeFactory() {
			@Override
			public NamedNode createNamedNode(BRJS brjs, String nodeName) {
				return brjs.app("app1").aspect(nodeName);
			}
		};
		NamedNodeFactory bladeFactory = new NamedNodeFactory() {
			@Override
			public NamedNode createNamedNode(BRJS brjs, String nodeName) {
				return brjs.app("app1").bladeset("bs1").blade(nodeName);
			}
		};
		NamedNodeFactory bladesetFactory = new NamedNodeFactory() {
			@Override
			public NamedNode createNamedNode(BRJS brjs, String nodeName) {
				return brjs.app("app1").bladeset(nodeName);
			}
		};
		NamedNodeFactory technologyTestPackFactory = new NamedNodeFactory() {
			@Override
			public NamedNode createNamedNode(BRJS brjs, String nodeName) {
				return brjs.app("app1").aspect("aspect1").testType("type1").testTech(nodeName);
			}
		};

		
		NamedNodeFactory typedTestPackFactory = new NamedNodeFactory() {
			@Override
			public NamedNode createNamedNode(BRJS brjs, String nodeName) {
				return brjs.app("app1").aspect("aspect1").testType(nodeName);
			}
		};
		
		return Arrays.asList(new Object[][]{
			{"App", appFactory},
			{"Aspect", aspectFactory},
			{"Blade", bladeFactory},
			{"Bladeset", bladesetFactory},
			{"TechnologyTestPack", technologyTestPackFactory},
			{"TypedTestPack", typedTestPackFactory}
		});
	}
	
	
	
	@Test
	public void verifyNameTellsUsIfTheNameIsValid() throws Exception {
		given(node);
		then(node).nameIsValid();
	}
	
	@Test
	public void verifyNameTellsUsIfTheNameIsInvalid() throws Exception {
		given(badNode);
		then(badNode).nameIsInvalid();
	}
	
	@Test
	public void weCanCreateACorrectlyNamedNode() throws Exception {
		given(logging).enabled();
		when(node).create();
		then(node).dirExists()
			.and(logging).debugMessageReceived(NODE_CREATED_LOG_MSG, node.getClass().getSimpleName(), node.dir().getPath());
	}
	
	@Test
	public void createFailsIfTheNameIsInvalid() throws Exception {
		given(logging).enabled();
		when(badNode).create();
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, badNode.getClass().getSimpleName(), badNode.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, badNode.getName(), badNode.dir().getPath());
	}
	
	@Test
	public void weCanSeeWhenTheDirectoryExists() throws Exception {
		given(node).hasBeenCreated();
		then(node).dirExists();
	}
	
	@Test
	public void weCanSeeWhenTheDirectoryDoesntExist() throws Exception {
		given(node);
		then(node).dirDoesNotExist();
	}
	
	@Test
	public void createFailsIfTheDirectoryAlreadyExists() throws Exception {
		given(logging).enabled()
			.and(node).hasBeenCreated();
		when(node).create();
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, node.getClass().getSimpleName(), node.dir().getPath())
			.and(exceptions).verifyException(DirectoryAlreadyExistsModelException.class, node.dir().getPath());
	}
	
	@Test
	public void weCanDeleteANodeThatExists() throws Exception {
		given(node).hasBeenCreated()
			.and(logging).enabled();
		when(node).delete();
		then(node).dirDoesNotExist()
			.and(logging).debugMessageReceived(NODE_DELETED_LOG_MSG, node.getClass().getSimpleName(), node.dir().getPath());
	}
	
	@Test
	public void weCantDeleteANodeThatDoesntExist() throws Exception {
		given(logging).enabled();
		when(node).delete();
		then(logging).errorMessageReceived(NODE_DELETION_FAILED_LOG_MSG, node.getClass().getSimpleName(), node.dir().getPath())
			.and(exceptions).verifyException(NoSuchDirectoryException.class, node.dir().getPath());
	}
	
	@Test
	public void weCanCreateANodeUsingATemplate() throws Exception {
		given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(nodeTemplate).containsFile("some-file.blah");
		when(node).populate("default");
		then(node).dirExists()
			.and(node).hasFile("some-file.blah");
	}
	
	@Test
	public void populateFailsIfTheDirectoryNameIsInvalid() throws Exception {
		given(logging).enabled();
		when(badNode).populate("default");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, node.getClass().getSimpleName(), badNode.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, badNode.getName(), badNode.dir().getPath());
	}
	
	@Test
	public void weCanCreateANodeUsingATemplateWhichHasADirectory() throws Exception {
		given(brjs.sdkTemplateGroup("default")).templateGroupCreated()
			.and(nodeTemplate).containsFolder("the-dir");
		when(node).populate("default");
		then(node).dirExists()
			.and(node).hasDir("the-dir");
	}
	
	@Test
	public void cantPopulateALibraryIfAlreadyExist() throws Exception {
		given(node).hasBeenCreated()
			.and(logging).enabled();
		when(node).populate("default");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, node.getClass().getSimpleName(), node.dir().getPath())
			.and(exceptions).verifyException(DirectoryAlreadyExistsModelException.class, node.dir().getPath());
	}
	
	@Test
	public void creatingANodeCausesRootObserversToBeNotified() {
		given(observer).observing(brjs);
		when(node).create()
			.and(node).ready();
		then(observer).notified(NodeReadyEvent.class, node);
	}
	
	@Test
	public void observerIsNotifiedAutomaticallyOnCreate()
	{
		given(observer).observing(brjs);
		when(node).create();
		then(observer).notified(NodeCreatedEvent.class, node)
			.and(observer).noNotifications();
	}
	
	@Test
	public void observerIsNotifiedOnDelete()
	{
		given(observer).observing(brjs);
		when(node).create()
			.and(node).delete();
		then(observer).notified(NodeDeletedEvent.class, node);
	}
	
	@Test
	public void nodesAreNotifiedWhenANodeIsDiscovered() throws Exception
	{
		given(super.testSdkDirectory).containsFile("apps/myApp/appContent.txt")
			.and(observer).observing(brjs);
		when(brjs.userApp("myApp"));
		then(observer).notified(NodeDiscoveredEvent.class, brjs.userApp("myApp"));
	}
	
	@Test
	public void nodesAreNotNotifiedOfCeatedWhenANodeIsDiscovered() throws Exception
	{
		given(observer).observing(brjs);
		when(super.testSdkDirectory).containsFile("apps/myApp/appContent.txt")
			.and(brjs.userApp("myApp"));
		then(observer).notified(NodeDiscoveredEvent.class, brjs.userApp("myApp"))
			.and(observer).notified(NodeReadyEvent.class, brjs.userApp("myApp"))
			.and(observer).noNotifications();
	}
	
	private interface NamedNodeFactory {
		NamedNode createNamedNode(BRJS brjs, String nodeName);
	}
}
