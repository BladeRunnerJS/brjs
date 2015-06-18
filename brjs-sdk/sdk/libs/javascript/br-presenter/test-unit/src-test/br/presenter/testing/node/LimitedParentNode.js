var brCore = require("br/Core");
var PresentationNode = require("br/presenter/node/PresentationNode");

var LimitedParentNode = function()
{
	var LimitedDescendantNode = require("br/presenter/testing/node/LimitedDescendantNode");
	var WritableProperty = require("br/presenter/property/WritableProperty");
	
	this.parentProperty = new WritableProperty("p");
	this.nestedChild = new LimitedDescendantNode();
};
brCore.extend(LimitedParentNode, PresentationNode);

module.exports = LimitedParentNode;
