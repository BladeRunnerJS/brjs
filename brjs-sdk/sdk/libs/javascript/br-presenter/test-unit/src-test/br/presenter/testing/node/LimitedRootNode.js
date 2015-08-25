var brCore = require("br/Core");
var WritableProperty = require("br/presenter/property/WritableProperty");
var LimitedParentNode = require("br/presenter/testing/node/LimitedParentNode");
var LimitedDescendantNode = require("br/presenter/testing/node/LimitedDescendantNode");
var PresentationModel = require("br/presenter/PresentationModel");

var LimitedRootNode = function()
{
	this.rootProperty = new WritableProperty("r");
	this.parentNode1 = new LimitedParentNode();
	this.parentNode2 = new LimitedParentNode();
	this.descendantNode1 = new LimitedDescendantNode();
	this.descendantNode2 = new LimitedDescendantNode();
};
brCore.extend(LimitedRootNode, PresentationModel);

module.exports = LimitedRootNode;
