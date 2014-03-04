ControlPluginPresentationModel = function()
{
	this.field = new br.presenter.node.DisplayField("control #1");
	
	this.nested = {
			oNode1 : this._createNodeListItem("control #2"),
			oNode2 : this._createNodeListItem("control #3")
	}
	this.nodeList = new br.presenter.node.NodeList([this.nested.oNode1]);
};
br.Core.extend(ControlPluginPresentationModel, br.presenter.PresentationModel);

ControlPluginPresentationModel.prototype.showZeroNodeListItems = function()
{
	this.nodeList.updateList([]);
};

ControlPluginPresentationModel.prototype.showTwoNodeListItems = function()
{
	this.nodeList.updateList([this.nested.oNode1, this.nested.oNode2]);
};

ControlPluginPresentationModel.prototype.showTwoNodeListItemsInReverseOrder = function()
{
	this.nodeList.updateList([this.nested.oNode2, this.nested.oNode1]);
};

ControlPluginPresentationModel.prototype._createNodeListItem = function(sFieldText)
{
	var oNode = new br.presenter.node.PresentationNode();
	oNode.field = new br.presenter.node.DisplayField(sFieldText);
	
	return oNode;
};
