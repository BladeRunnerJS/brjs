var mTemplateMappings = {
	"node #1": "node-list-item",
	"node #2": "alternate-node-list-item",
	"node #3": "node-list-item",
	"node #4": "alternate-node-list-item"
};

NodeListPresentationModel = function()
{
	this.nested = {
		oNode1 : new TemplateAwarePresentationNode("node #1"),
		oNode2 : new TemplateAwarePresentationNode("node #2"),
		oNode3 : new TemplateAwarePresentationNode("node #3"),
		oNode4 : new TemplateAwarePresentationNode("node #4")
	}

	this.nodeList = new br.presenter.node.NodeList([this.nested.oNode1]);
	this.anotherNodeList = new br.presenter.node.NodeList([]);
};
br.Core.extend(NodeListPresentationModel, br.presenter.PresentationModel);

TemplateAwarePresentationNode = function(sNodeText)
{
	this.itemProperty = new br.presenter.property.WritableProperty(sNodeText);
};
br.Core.extend(TemplateAwarePresentationNode, br.presenter.node.PresentationNode);
br.Core.implement(TemplateAwarePresentationNode, br.presenter.node.TemplateAware);

TemplateAwarePresentationNode.prototype.getTemplateName = function()
{
	return mTemplateMappings[this.itemProperty.getValue()];
};

NodeListPresentationModel.prototype.showZeroNodes = function()
{
	this.nodeList.updateList([]);
};

NodeListPresentationModel.prototype.showOneNode = function()
{
	this.nodeList.updateList([this.nested.oNode1]);
};

NodeListPresentationModel.prototype.showTwoNodes = function()
{
	this.nodeList.updateList([this.nested.oNode1, this.nested.oNode2]);
};

NodeListPresentationModel.prototype.showTwoNodesReverseOrder = function()
{
	this.nodeList.updateList([this.nested.oNode2, this.nested.oNode1]);
};

NodeListPresentationModel.prototype.showAnotherNodeList = function()
{
	this.anotherNodeList.updateList([this.nested.oNode3, this.nested.oNode4]);
};
