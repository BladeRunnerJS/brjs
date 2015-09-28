var brCore = require("br/Core");
var PresentationNode = require("br/presenter/node/PresentationNode");
var WritableProperty = require("br/presenter/property/WritableProperty");

var LimitedParentNode = function()
{
	// require LimitedDescendantNode to break a circular dependency
	var LimitedDescendantNode = require("br/presenter/testing/node/LimitedDescendantNode");
	
	this.parentProperty = new WritableProperty("p");
	this.nestedChild = new LimitedDescendantNode();
};
brCore.extend(LimitedParentNode, PresentationNode);

module.exports = LimitedParentNode;
