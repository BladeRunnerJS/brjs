var brCore = require("br/Core");
var PresentationNode = require('br-presenter/node/PresentationNode');
var WritableProperty = require('br-presenter/property/WritableProperty');

var GrandChildPresentationNode = function()
{
	this.property4 = new WritableProperty("p4");
};
brCore.extend(GrandChildPresentationNode, PresentationNode);

module.exports = GrandChildPresentationNode;
