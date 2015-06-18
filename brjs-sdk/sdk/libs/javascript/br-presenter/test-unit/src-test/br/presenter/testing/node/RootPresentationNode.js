var brCore = require("br/Core");
var WritableProperty = require("br/presenter/property/WritableProperty");
var ChildPresentationNode = require("br/presenter/testing/node/ChildPresentationNode");
var PresentationModel = require("br/presenter/PresentationModel");

var RootPresentationNode = function()
{
	this.m_oPrivateProperty1 = new WritableProperty();
	
	this.property1 = new WritableProperty("p1");
	this.child = new ChildPresentationNode();
};
brCore.extend(RootPresentationNode, PresentationModel);

module.exports = RootPresentationNode;
