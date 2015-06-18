var brCore = require("br/Core");
var PresentationNode = require("br/presenter/node/PresentationNode");

var ChildPresentationNode = function()
{
	this.m_oPrivateProperty2 = new WritableProperty();
	
	this.property2 = new WritableProperty("p2");
	this.property3 = new WritableProperty("p3");
	this.grandchild = new GrandChildPresentationNode();
};
br.Core.extend(ChildPresentationNode, PresentationNode);

module.exports = ChildPresentationNode;

var WritableProperty = require("br/presenter/property/WritableProperty");
var GrandChildPresentationNode = require("br/presenter/testing/node/GrandChildPresentationNode");
