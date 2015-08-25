var brCore = require("br/Core");
var WritableProperty = require("br/presenter/property/WritableProperty");
var ChildPresentationNode = require("br/presenter/testing/node/ChildPresentationNode");
var NodeList = require("br/presenter/node/NodeList");
var PresentationModel = require("br/presenter/PresentationModel");

var RootPresentationNodeContainingList = function()
{
	this.m_oPrivateProperty1 = new WritableProperty();
	this.m_oOnlyChild = new ChildPresentationNode();
	
	this.property1 = new WritableProperty();
	this.children = new NodeList([this.m_oOnlyChild]);
};
brCore.extend(RootPresentationNodeContainingList, PresentationModel);

module.exports = RootPresentationNodeContainingList;
