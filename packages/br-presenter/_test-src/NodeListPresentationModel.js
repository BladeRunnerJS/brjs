require('../_resources-test-at/html/node-list-item.html');
require('../_resources-test-at/html/node-list.html');
require('../_resources-test-at/html/alternate-node-list-item.html');
var TemplateAware = require('br-presenter/node/TemplateAware');
var PresentationNode = require('br-presenter/node/PresentationNode');
var WritableProperty = require('br-presenter/property/WritableProperty');
var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var NodeList = require('br-presenter/node/NodeList');
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

    this.nodeList = new NodeList([this.nested.oNode1]);
    this.anotherNodeList = new NodeList([]);
};
Core.extend(NodeListPresentationModel, PresentationModel);

TemplateAwarePresentationNode = function(sNodeText)
{
    this.itemProperty = new WritableProperty(sNodeText);
};
Core.extend(TemplateAwarePresentationNode, PresentationNode);
Core.implement(TemplateAwarePresentationNode, TemplateAware);

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

module.exports = NodeListPresentationModel;
