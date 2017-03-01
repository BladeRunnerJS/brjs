
var PresentationNode = require('br-presenter/node/PresentationNode');
var PresentationModel = require('br-presenter/PresentationModel');
var Core = require("br/Core");
var NodeList = require('br-presenter/node/NodeList');
var DisplayField = require('br-presenter/node/DisplayField');
ControlPluginPresentationModel = function()
{
    this.field = new DisplayField("control #1");
    
    this.nested = {
            oNode1 : this._createNodeListItem("control #2"),
            oNode2 : this._createNodeListItem("control #3")
    }
    this.nodeList = new NodeList([this.nested.oNode1]);
};
Core.extend(ControlPluginPresentationModel, PresentationModel);

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
    var oNode = new PresentationNode();
    oNode.field = new DisplayField(sFieldText);
    
    return oNode;
};

module.exports = ControlPluginPresentationModel;
