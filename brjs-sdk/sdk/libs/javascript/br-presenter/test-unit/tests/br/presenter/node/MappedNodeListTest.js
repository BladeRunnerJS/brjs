(function() {
    var Errors = require("br/Errors");
    var MappedNodeList = require("br/presenter/node/MappedNodeList");
    var SimplePresentationNode = require("br/presenter/testing/node/SimplePresentationNode");
    var RootPresentationNode = require("br/presenter/testing/node/RootPresentationNode");
    var Core = require("br/Core");
    var Mock4JS = require("mock4js");

    MappedNodeListTest = TestCase("MappedNodeListTest");

    MappedNodeListTest.prototype.setUp = function ()
    {
        this.rootPresentationNode = new RootPresentationNode();
        
        Mock4JS.addMockSupport(window);
        Mock4JS.clearMocksToVerify();
    };

    MappedNodeListTest.prototype.tearDown = function ()
    {
        Mock4JS.verifyAllMocks();
    };

    MappedNodeListTest.prototype._createAChildNode = function(vValue)
    {
        return new SimplePresentationNode(vValue);
    };

    MappedNodeListTest.prototype.test_canConstructAMappedNodeListWithAMapOfNodes = function()
    {
        var mNodes = {"node1": this._createAChildNode("n1"), "node2": this._createAChildNode("n2")};
        var oMapList = new MappedNodeList(mNodes);
        assertEquals("n1", oMapList["node1"].property.getValue());
        assertEquals("n2", oMapList["node2"].property.getValue());
    };

    MappedNodeListTest.prototype.test_cannotConstructAMappedNodeListWithAnArray = function()
    {
        var pBadNodes = [this._createAChildNode("n1"), this._createAChildNode("n2")];
        assertException("Only arrays of key-node pairs allowed", function() {
            var oMapList = new MappedNodeList(pBadNodes);
        }, Errors.INVALID_PARAMETERS);
    };

    MappedNodeListTest.prototype.test_constructingAMappedNodeListWithAnArrayOfNonPairsThrowsAnException = function()
    {
        var pBadNodes = [[this._createAChildNode("n1")], [this._createAChildNode("n2")]];
        assertException("Only arrays of key-node pairs allowed", function() {
            var oMapList = new MappedNodeList(pBadNodes);
        }, Errors.INVALID_PARAMETERS);

        pBadNodes = [["n1", this._createAChildNode("n1"), "blah"], ["n2", this._createAChildNode("n2"), "blah"]];
        assertException("Only arrays of key-node pairs allowed", function() {
            var oMapList = new MappedNodeList(pBadNodes);
        }, Errors.INVALID_PARAMETERS);
    };

    MappedNodeListTest.prototype.test_canUpdateValuesInAMappedNodeList = function()
    {
        var pNodes = {"node1": this._createAChildNode("n1"), "node2": this._createAChildNode("n2")};
        var oMapList = new MappedNodeList(pNodes);
        assertEquals("n1", oMapList["node1"].property.getValue());
        assertEquals("n2", oMapList["node2"].property.getValue());

        oMapList.updateList({"node3": this._createAChildNode("n3")});
        assertUndefined(oMapList["node1"]);
        assertUndefined(oMapList["node2"]);
        assertEquals("n3", oMapList["node3"].property.getValue());
    };

	MappedNodeListTest.prototype.test_addingAValueDoesntBreakExistingPaths = function()
	{
		var pNodes = {
		"node1": this._createAChildNode("n1"), 
		"node2": this._createAChildNode("n2")
		};
		var oMapList = new MappedNodeList(pNodes);
		
		this.rootPresentationNode.oMapList = oMapList;
		this.rootPresentationNode._$setPath(null);
		
		assertEquals("oMapList.node1", oMapList["node1"].getPath());
		assertEquals("oMapList.node2", oMapList["node2"].getPath());
		assertUndefined(oMapList["node3"]);
		
		pNodes["node3"] = this._createAChildNode("n3");
		oMapList.updateList(pNodes);
		
		assertEquals("oMapList.node1", oMapList["node1"].getPath());
		assertEquals("oMapList.node2", oMapList["node2"].getPath());
		assertEquals("oMapList.node3", oMapList["node3"].getPath());
		
		delete pNodes["node1"];
		oMapList.updateList(pNodes);
		
		assertUndefined(oMapList["node1"]);
		assertEquals("oMapList.node2", oMapList["node2"].getPath());
		assertEquals("oMapList.node3", oMapList["node3"].getPath());
	};
	
	MappedNodeListTest.prototype.test_addingAValueDoesntBreakExistingPathsForPrivatelyReferencedNodes = function()
	{
		var foreignNode = this._createAChildNode("foreign");
		
		var pNodes = {
			"node1": this._createAChildNode("n1"), 
			"node2": this._createAChildNode("n2")
		};
		var oMapList = new MappedNodeList(pNodes);
		
		oMapList["node2"].m_foreignNode = foreignNode;
		
		this.rootPresentationNode.oMapList = oMapList;
		this.rootPresentationNode.foreign = foreignNode;
		this.rootPresentationNode._$setPath(null);
		
		assertEquals("foreign", foreignNode.getPath());
		assertEquals("oMapList.node1", oMapList["node1"].getPath());
		assertEquals("oMapList.node2", oMapList["node2"].getPath());
		
		pNodes["node3"] = this._createAChildNode("n3");
		oMapList.updateList(pNodes);
		
		assertEquals("foreign", foreignNode.getPath());
		assertEquals("oMapList.node1", oMapList["node1"].getPath());
		assertEquals("oMapList.node2", oMapList["node2"].getPath());
		assertEquals("oMapList.node3", oMapList["node3"].getPath());
	};
    
})();
