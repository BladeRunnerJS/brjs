br.Core.thirdparty('mock4js');

MappedNodeListTest = TestCase("MappedNodeListTest");

MappedNodeListTest.prototype.setUp = function ()
{
	this.rootPresentationNode = new br.presenter.testing.node.RootPresentationNode();
	
	Mock4JS.addMockSupport(window);
	Mock4JS.clearMocksToVerify();
};

MappedNodeListTest.prototype.tearDown = function ()
{
	Mock4JS.verifyAllMocks();
};

MappedNodeListTest.prototype._createAChildNode = function(vValue)
{
	return new br.presenter.testing.node.SimplePresentationNode(vValue);
};

MappedNodeListTest.prototype.test_canConstructAMappedNodeListWithAMapOfNodes = function()
{
	var mNodes = {"node1": this._createAChildNode("n1"), "node2": this._createAChildNode("n2")};
	var oMapList = new br.presenter.node.MappedNodeList(mNodes);
	assertEquals("n1", oMapList["node1"].property.getValue());
	assertEquals("n2", oMapList["node2"].property.getValue());
};

MappedNodeListTest.prototype.test_cannotConstructAMappedNodeListWithAnArray = function()
{
	var pBadNodes = [this._createAChildNode("n1"), this._createAChildNode("n2")];
	assertException("Only arrays of key-node pairs allowed", function() {
		var oMapList = new br.presenter.node.MappedNodeList(pBadNodes);
	}, br.Errors.LEGACY);
};

MappedNodeListTest.prototype.test_constructingAMappedNodeListWithAnArrayOfNonPairsThrowsAnException = function()
{
	var pBadNodes = [[this._createAChildNode("n1")], [this._createAChildNode("n2")]];
	assertException("Only arrays of key-node pairs allowed", function() {
		var oMapList = new br.presenter.node.MappedNodeList(pBadNodes);
	}, br.Errors.LEGACY);

	pBadNodes = [["n1", this._createAChildNode("n1"), "blah"], ["n2", this._createAChildNode("n2"), "blah"]];
	assertException("Only arrays of key-node pairs allowed", function() {
		var oMapList = new br.presenter.node.MappedNodeList(pBadNodes);
	}, br.Errors.LEGACY);
};

MappedNodeListTest.prototype.test_canUpdateValuesInAMappedNodeList = function()
{
	var pNodes = {"node1": this._createAChildNode("n1"), "node2": this._createAChildNode("n2")};
	var oMapList = new br.presenter.node.MappedNodeList(pNodes);
	assertEquals("n1", oMapList["node1"].property.getValue());
	assertEquals("n2", oMapList["node2"].property.getValue());

	oMapList.updateList({"node3": this._createAChildNode("n3")});
	assertUndefined(oMapList["node1"]);
	assertUndefined(oMapList["node2"]);
	assertEquals("n3", oMapList["node3"].property.getValue());
};
