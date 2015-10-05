var brCore = require("br/Core");
var LimitedParentNode = require("br/presenter/testing/node/LimitedParentNode");
var WritableProperty = require("br/presenter/property/WritableProperty");

var LimitedDescendantNode = function()
{
	this.childProperty = new WritableProperty("c");
};
brCore.extend(LimitedDescendantNode, LimitedParentNode);

module.exports = LimitedDescendantNode;
