br.thirdparty('mock4js');

NodeListTest = TestCase("NodeListTest");

NodeListTest.prototype.setUp = function ()
{
	this.rootPresentationNode = new br.presenter.testing.node.RootPresentationNode();
	this.limitedRootNode = new br.presenter.testing.node.LimitedRootNode();

	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

NodeListTest.prototype.tearDown = function ()
{
	Mock4JS.verifyAllMocks();
};

NodeListTest.prototype.test_nodes = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	assertEquals([oNode.m_oOnlyChild, oNode.m_oOnlyChild.grandchild], oNode.children.nodes().getNodesArray());
};

NodeListTest.prototype.test_nodesWithNameFilter = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	
	assertEquals("1a", [oNode.m_oOnlyChild], oNode.children.nodes("0").getNodesArray());
	assertEquals("1b", [], oNode.children.nodes("1").getNodesArray());
	
	assertEquals("2a", [oNode.m_oOnlyChild.grandchild], oNode.children.nodes("grandchild").getNodesArray());
};

NodeListTest.prototype.test_properties = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	var oProperties = oNode.children.properties();
	assertEquals(4, oProperties.getSize());
};

NodeListTest.prototype.test_propertiesWithNameFilter = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	var oProperties = oNode.children.properties("property2");
	assertEquals(1, oProperties.getSize());
};

NodeListTest.prototype.test_cannotPutObjectsThatAreNotPresentationNodesInNodeList = function()
{
	var pPresentationNodes = [{}, {}];
	assertException("Exception on list of non-PNs", function(){
		var oNodeList = new br.presenter.node.NodeList(pPresentationNodes);
	}, Error.UNIMPLEMENTED_INTERFACE);
};

NodeListTest.prototype.test_canSpecifyALimitingClassForANodeList = function ()
{
	var pPresentationNodes = [this.limitedRootNode.parentNode1, this.limitedRootNode.parentNode2];
	var oNodeList = new br.presenter.node.NodeList(pPresentationNodes, br.presenter.testing.node.LimitedParentNode);
	assertEquals(2, oNodeList.getPresentationNodesArray().length);
};

NodeListTest.prototype.test_cannotPutNodesThatDontImplementTheSpecifiedClassInALimitedNodeList = function ()
{
	var pPresentationNodes = [this.limitedRootNode.descendantNode1, this.limitedRootNode.parentNode1];
	assertException("NodeList only accepts the specified type of Node", function() {
		var oNodeList = new br.presenter.node.NodeList(pPresentationNodes, br.presenter.testing.node.LimitedDescendantNode);
	}, Error.UNIMPLEMENTED_INTERFACE);
};

NodeListTest.prototype.test_canPutSubclassesOfTheSpecifiedClassIntoALimitedNodeList = function ()
{
	var pPresentationNodes = [this.limitedRootNode.descendantNode1, this.limitedRootNode.descendantNode2, this.limitedRootNode.parentNode1];
	var oNodeList = new br.presenter.node.NodeList(pPresentationNodes, br.presenter.testing.node.LimitedParentNode);
	assertEquals(3, oNodeList.getPresentationNodesArray().length);
};

NodeListTest.prototype.test_anExceptionIsThrownWhenAddingAListenerWhichIsNotAnInstanceOfNodeListListener = function ()
{
	var oNodeList = new br.presenter.node.NodeList();
	assertException("Exception on bad listener object", function(){
		oNodeList.addListener({});
	}, br.Errors.LEGACY);
};

NodeListTest.prototype.test_canSuccessfullyAddAndRemoveListeners = function()
{
	var oNodeList = new br.presenter.node.NodeList();
	var oNodeListListener = new br.presenter.node.NodeListListener();
	oNodeList.addListener(oNodeListListener);
	oNodeList.removeListener(oNodeListListener);
};

NodeListTest.prototype.test_weCanAddAndRemoveMultipleListeners = function()
{
	var oNodeList = new br.presenter.node.NodeList();
	var oNodeListListener1 = new br.presenter.node.NodeListListener();
	var oNodeListListener2 = new br.presenter.node.NodeListListener();
	oNodeList.addListener(oNodeListListener1);
	oNodeList.addListener(oNodeListListener2);
	oNodeList.removeAllListeners();
};

NodeListTest.prototype.test_onNodeListChangedIsInvokedWhenTheNodeListChanges = function()
{
	var oNodeListListenerMock = mock(br.presenter.node.NodeListListener);

	// no listeners have been added yet, so won't be informed about first list
	var oNodeList = new br.presenter.node.NodeList([this.rootPresentationNode, this.limitedRootNode]);

	oNodeList.addListener(oNodeListListenerMock.proxy());
	oNodeListListenerMock.expects(once()).onNodeListChanged();
	oNodeList.updateList([]);
};

NodeListTest.prototype.test_onNodeListChangedIsInvokedForAllListenersWhenTheNodeListChanges = function()
{
	var oNodeListListenerMock1 = mock(br.presenter.node.NodeListListener);
	var oNodeListListenerMock2 = mock(br.presenter.node.NodeListListener);

	// no listeners have been added yet, so won't be informed about first list
	var oNodeList = new br.presenter.node.NodeList([this.rootPresentationNode, this.limitedRootNode]);

	oNodeList.addListener(oNodeListListenerMock1.proxy()).addListener(oNodeListListenerMock2.proxy());
	oNodeListListenerMock1.expects(once()).onNodeListChanged();
	oNodeListListenerMock2.expects(once()).onNodeListChanged();
	oNodeList.updateList([]);
};

NodeListTest.prototype.test_onNodeListChangedIsInvokedForAllListenersWhenTheNodeListChanges = function()
{
	var oNodeListListenerMock1 = mock(br.presenter.node.NodeListListener);
	var oNodeListListenerMock2 = mock(br.presenter.node.NodeListListener);

	// no listeners have been added yet, so won't be informed about first list
	var oNodeList = new br.presenter.node.NodeList([this.rootPresentationNode, this.limitedRootNode]);

	oNodeList.addListener(oNodeListListenerMock1.proxy()).addListener(oNodeListListenerMock2.proxy());
	oNodeListListenerMock1.expects(once()).onNodeListChanged();
	oNodeListListenerMock2.expects(once()).onNodeListChanged();
	oNodeList.updateList([]);

	oNodeList.removeListener(oNodeListListenerMock1.proxy());
	oNodeListListenerMock1.expects(never()).onNodeListChanged();
	oNodeListListenerMock2.expects(once()).onNodeListChanged();
	oNodeList.updateList([]);
};

NodeListTest.prototype.test_canInvokeTheListenerImmediately = function()
{
	var oNodeListListenerMock = mock(br.presenter.node.NodeListListener);
	var oNodeList = new br.presenter.node.NodeList();

	oNodeListListenerMock.expects(once()).onNodeListChanged();
	oNodeList.addListener(oNodeListListenerMock.proxy(), true);
};

// Taken from PropertyTest
NodeListTest.prototype.getListenerClass = function()
{
	var fListenerClass = function()
	{
	};

	fListenerClass.prototype.invocationMethod = function()
	{
	};
	return fListenerClass;
};

NodeListTest.prototype.test_canAddAndRemoveAChangeOnlyListener = function()
{
	var oListenerMock = mock(this.getListenerClass());
	var oNodeList = new br.presenter.node.NodeList([this.rootPresentationNode]);
	var oNodeListListener = oNodeList.addChangeListener(oListenerMock.proxy(), "invocationMethod");

	oListenerMock.expects(once()).invocationMethod();
	oNodeList.updateList([]);

	oNodeList.removeListener(oNodeListListener);
};

NodeListTest.prototype.test_specifyingANonExistentChangeListenerMethodCausesAnException = function()
{
	var oListenerMock = mock(this.getListenerClass());
	var oNodeList = new br.presenter.node.NodeList();

	assertException("Exception when method non-existent on listener", function(){
		oNodeList.addChangeListener(oListenerMock.proxy(), "noSuchMethod");
	}, br.Errors.LEGACY);
};


NodeListTest.prototype.test_canInvokeAChangeOnlyListenerImmediately = function()
{
	var oListenerMock = mock(this.getListenerClass());
	var oNodeList = new br.presenter.node.NodeList();

	oListenerMock.expects(once()).invocationMethod();
	oNodeList.addChangeListener(oListenerMock.proxy(), "invocationMethod", true);
};

NodeListTest.prototype.test_clearNodeRecursivelyClearsNodePaths = function ()
{
	
	var pPresentationNodes = [this.limitedRootNode.descendantNode1, this.limitedRootNode.descendantNode2, this.limitedRootNode.parentNode1];
	var oNodeList = new br.presenter.node.NodeList(pPresentationNodes, br.presenter.testing.node.LimitedParentNode);
	var pNodes = oNodeList.getPresentationNodesArray();
	assertEquals(3,undefined, pNodes[0].getPath());
	pNodes = oNodeList.getPresentationNodesArray();
	oNodeList._copiesAndChecksNodesAndClearsNodePaths(pNodes);
	assertEquals(3,undefined, pNodes[0].getPath());
	assertEquals(3,undefined, pNodes[0].getPath());
};

NodeListTest.prototype.test_nodesThatAreAddedViaConstructorWithPathsHaveThemClearedDown = function ()
{
	this.limitedRootNode._$setPath(); //It's the PresentationModel so the top level node.
	assertEquals("descendantNode1", this.limitedRootNode.descendantNode1.getPath());
	
	var pPresentationNodes = [this.limitedRootNode.parentNode1];
	var oNodeList = new br.presenter.node.NodeList(pPresentationNodes, br.presenter.testing.node.LimitedParentNode);
	
	var pNodes = oNodeList.getPresentationNodesArray();
	assertEquals(3,undefined, pNodes[0].getPath());
};

NodeListTest.prototype.test_nodesThatAreAddedViaupdateListHaveTheirPathsSetUp = function()
{
	var unit = new br.presenter.node.NodeList();
	unit._$setPath("nodelist",{});
	
	var node1 = new br.presenter.testing.node.ChildPresentationNode();
	unit.updateList([node1]);

	var pNodes = unit.getPresentationNodesArray();
	assertEquals("nodelist.0", pNodes[0].getPath());
	assertEquals("nodelist.0.grandchild", pNodes[0].grandchild.getPath());
	assertEquals("nodelist.0.grandchild.property4", pNodes[0].grandchild.property4.getPath());
};

NodeListTest.prototype.test_nodesThatAreAddedViaupdateListHaveTheirPathsSetUpOnlyIfTheNodeListHasAPath = function()
{
	this.rootPresentationNode._$setPath(); //It's the PresentationModel so the top level node.
	
	var oNodeList = new br.presenter.node.NodeList();
	
	var pPresentationNodes = [this.rootPresentationNode.child];
	oNodeList.updateList(pPresentationNodes);

	var pNodes = oNodeList.getPresentationNodesArray();
	assertEquals(undefined, pNodes[0].getPath());
	assertEquals(undefined, pNodes[0].grandchild.getPath());
	assertEquals(undefined, pNodes[0].grandchild.property4.getPath());
	
	oNodeList._$setPath("nodelist");
	
	assertEquals("nodelist.0", pNodes[0].getPath());
	assertEquals("nodelist.0.grandchild", pNodes[0].grandchild.getPath());
	assertEquals("nodelist.0.grandchild.property4", pNodes[0].grandchild.property4.getPath());
};