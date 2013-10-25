br.thirdparty('jsunitextensions');

PresentationNodeTest = TestCase("PresentationNodeTest");

PresentationNodeTest.prototype.test_nodes = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	assertEquals([node.child, node.child.grandchild], node.nodes().getNodesArray());
};

PresentationNodeTest.prototype.test_UpwardRefsAllowed = function()
{
	var pm = new br.presenter.testing.node.UpwardRefsPModel();
	pm._$setPath({});
	var pResult = pm.nodes().getNodesArray();
	assertEquals([pm.child, pm.child.oGrandChild], pResult);
};

PresentationNodeTest.prototype.test_DuplicateRefsInPMFail = function()
{
	var pm = new br.presenter.testing.node.UpwardRefsPModel();
	pm.duplicate = pm.child;
	assertFails("'child' and 'duplicate' are both references to the same instance in PresentationModel." , function() {
		pm._$setPath({});
	} );
};


PresentationNodeTest.prototype.test_nodesWithNameFilter = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	assertEquals("1", [node.child], node.nodes("child").getNodesArray());
	assertEquals("2", [node.child.grandchild], node.nodes("grandchild").getNodesArray());
};

PresentationNodeTest.prototype.test_nodesWithWildcardNameFilter = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	assertEquals([node.child, node.child.grandchild], node.nodes("*").getNodesArray());
};

PresentationNodeTest.prototype.test_nodesWithPropertyNameFilter = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	assertEquals("1a", [], node.nodes("*", ["property1"]).getNodesArray());
	
	assertEquals("2a", [node.child], node.nodes("*", ["property2"]).getNodesArray());
	assertEquals("2b", [node.child], node.nodes("*", ["property3"]).getNodesArray());
	
	assertEquals("3a", [node.child.grandchild], node.nodes("*", ["property4"]).getNodesArray());
	
	assertEquals("4a", [], node.nodes("*", ["property5"]).getNodesArray());
};

PresentationNodeTest.prototype.test_nodesWithMultiplePropertyNameFilters = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	assertEquals("1", [], node.nodes("*", ["property1", "property2"]).getNodesArray());
	assertEquals("2", [node.child], node.nodes("*", ["property2", "property3"]).getNodesArray());
	assertEquals("3", [], node.nodes("*", ["property3", "property4"]).getNodesArray());
};

PresentationNodeTest.prototype.test_nodesWithPropertyValueFilter = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNode();
	
	assertEquals("1a", [], oNode.nodes("*", {property1:"p1"}).getNodesArray());
	assertEquals("1b", [], oNode.nodes("*", {property1:"some-other-value"}).getNodesArray());
	
	assertEquals("2a", [oNode.child], oNode.nodes("*", {property2:"p2"}).getNodesArray());
	assertEquals("2b", [], oNode.nodes("*", {property2:"some-other-value"}).getNodesArray());
	
	assertEquals("3a", [oNode.child], oNode.nodes("*", {property2:"p2", property3:"p3"}).getNodesArray());
	assertEquals("3b", [], oNode.nodes("*", {property1:"p1", property2:"p2"}).getNodesArray());
	
	assertEquals("4a", [], oNode.nodes("*", {property2:"p2", property3:"some-other-value"}).getNodesArray());
};

PresentationNodeTest.prototype.test_properties = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	var oProperties = node.properties();
	assertEquals(6, oProperties.getSize());
};

PresentationNodeTest.prototype.test_propertiesWithNameFilter = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	var oProperties = node.properties("property2");
	assertEquals(1, oProperties.getSize());
};

PresentationNodeTest.prototype.test_propertiesWithNameValueMatchFilter = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	var oProperties = node.properties("property2", "p2");
	assertEquals(1, oProperties.getSize());
};
PresentationNodeTest.prototype.test_propertiesWithNameValueNoMatchFilter = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	var oProperties = node.properties("property2", "xxx");
	assertEquals(0, oProperties.getSize());
};

PresentationNodeTest.prototype.test_nodesTraversingNodeLists = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	var pResult = oNode.nodes().getNodesArray();
	assertEquals([oNode.m_oOnlyChild, oNode.m_oOnlyChild.grandchild, oNode.children, oNode.m_oOnlyChild], pResult);
};

PresentationNodeTest.prototype.test_nodesTraversingNodeListsWithNameFilter = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	assertEquals("1", [oNode.children], oNode.nodes("children").getNodesArray());
	assertEquals("2", [oNode.m_oOnlyChild.grandchild], oNode.nodes("grandchild").getNodesArray());
};

PresentationNodeTest.prototype.test_propertiesTraversingNodeLists = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	
	var oProperties = oNode.properties();
	assertEquals(9, oProperties.getSize());
};	

PresentationNodeTest.prototype.test_propertiesTraversingNodeListsWithNameFilter = function()
{
	var oNode = new br.presenter.testing.node.RootPresentationNodeContainingList();
	var oProperties = oNode.properties("property2");
	assertEquals(2, oProperties.getSize());
};

PresentationNodeTest.prototype.test_paths = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	var eTemplate = document.createElement("div");
	
	oPresentationModel._$setPath("");
	
	assertEquals("1a", "", oPresentationModel.getPath());
	assertEquals("1b", "child", oPresentationModel.child.getPath());
	assertEquals("1c", "child.grandchild", oPresentationModel.child.grandchild.getPath());
};

PresentationNodeTest.prototype.test_PropertyPathsAreCleared = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	var eTemplate = document.createElement("div");
	
	oPresentationModel._$setPath("");
	assertEquals("1d", "child.grandchild.property4", oPresentationModel.child.grandchild.property4.getPath());
	
	oPresentationModel._$clearPropertiesPath();

	assertEquals("1a", undefined, oPresentationModel.property1.getPath());
	assertEquals("1b", undefined, oPresentationModel.child.property2.getPath());
	assertEquals("1c", undefined, oPresentationModel.child.property3.getPath());
	assertEquals("1d", undefined, oPresentationModel.child.grandchild.property4.getPath());
};

PresentationNodeTest.prototype._addPrivateDependencies = function(node)
{
	node.m_oProperty4 = node.child.grandchild.property4;
	node.child.m_oUpwards = node;
	node.child.grandchild.m_oUpwards = node.child;
	node.child.grandchild.m_oProperty1 = node.property1;
};

PresentationNodeTest.prototype.test_privateDependenciesDontBreakNodesMethod = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	this._addPrivateDependencies(node);
	
	assertEquals(3, node.nodes().getNodesArray().length);
	assertEquals(3, node.child.nodes().getNodesArray().length);
	assertEquals(3, node.child.grandchild.nodes().getNodesArray().length);
};

PresentationNodeTest.prototype.test_privateDependenciesDontBreakPropertiesMethod = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	this._addPrivateDependencies(node);
	

	assertEquals(11, node.properties().getSize());
	assertEquals(11, node.child.properties().getSize());
	assertEquals(10, node.child.grandchild.properties().getSize());
};

PresentationNodeTest.prototype._addWrappedDependencies = function(node)
{
	node.property4 = {w:node.child.grandchild.property4};
	node.child.upwards = {w:node};
	node.child.grandchild.upwards = {w:node.child};
	node.child.grandchild.property1 = {w:node.property1};
};

PresentationNodeTest.prototype.test_wrappedDependenciesDontBreakNodesMethod = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	this._addWrappedDependencies(node);
	
	assertEquals(2, node.nodes().getNodesArray().length);
	assertEquals(1, node.child.nodes().getNodesArray().length);
	assertEquals(0, node.child.grandchild.nodes().getNodesArray().length);
};

PresentationNodeTest.prototype.test_wrappedDependenciesDontBreakPropertiesMethod = function()
{
	var node = new br.presenter.testing.node.RootPresentationNode();
	this._addWrappedDependencies(node);

	assertEquals(6, node.properties().getSize());
	assertEquals(4, node.child.properties().getSize());
	assertEquals(1, node.child.grandchild.properties().getSize());
};

PresentationNodeTest.prototype.test_NodePathsAreCleared = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	
	oPresentationModel._$setPath("");
	assertEquals("1d", "child.grandchild.property4", oPresentationModel.child.grandchild.property4.getPath());
	assertEquals("1b", "child", oPresentationModel.child.getPath());
	assertEquals("1d", "child.grandchild", oPresentationModel.child.grandchild.getPath());
	
	oPresentationModel._$clearNodePaths();

	assertEquals("1a", undefined, oPresentationModel.property1.getPath());
	assertEquals("1b", undefined, oPresentationModel.child.getPath());
	assertEquals("1c", undefined, oPresentationModel.child.property3.getPath());
	assertEquals("1d", undefined, oPresentationModel.child.grandchild.getPath());
};

PresentationNodeTest.prototype.test_PrivateMemberVariablesDontHavePathSet = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	
	oPresentationModel._$setPath("");
	assertEquals("1a", "child.m_oPrivateProperty2", oPresentationModel.child.m_oPrivateProperty2.getPath());
	assertEquals("1b", "child.grandchild", oPresentationModel.child.grandchild.getPath());
};

PresentationNodeTest.prototype.test_FailIfWeHaveTwoPublicReferenceToTheSamePresentationNodeInAPresentationModel = function()
{
	var oPresentationModel = new br.presenter.testing.node.RootPresentationNode();
	
	oPresentationModel.child.publicCopyOfPresentationNode = oPresentationModel.child.grandchild;
	
	assertFails("Expected exception for double public reference of same Presentation Node." , function() {
		//When
		oPresentationModel._$setPath("");
	} );
};

PresentationNodeTest.prototype.test_allListenersFromNodesAndChildNodesGetRemoved = function()
{
	var nListeners = 0;
	var fAddListener = br.presenter.property.WritableProperty.prototype.addListener;
	var fRemoveAllListeners = br.presenter.property.WritableProperty.prototype.removeAllListeners;
	
	br.presenter.property.WritableProperty.prototype.addListener = function()
	{
		nListeners++;
	}
	br.presenter.property.WritableProperty.prototype.removeAllListeners = function()
	{
		nListeners--;
	}
	
	var oPresentationNode = new br.presenter.testing.node.RootPresentationNode();
	oPresentationNode.property1.addListener();
	oPresentationNode.child.property2.addListener();
	oPresentationNode.child.property3.addListener();
	oPresentationNode.child.grandchild.property4.addListener();
	oPresentationNode.m_oPrivateProperty1.addListener();
	oPresentationNode.child.m_oPrivateProperty2.addListener();
	
	oPresentationNode.removeChildListeners();
	
	br.presenter.property.WritableProperty.prototype.addListener = fAddListener;
	br.presenter.property.WritableProperty.prototype.removeAllListeners = fRemoveAllListeners;
	
	assertEquals("No listeners should be remaining", 0, nListeners);
};
