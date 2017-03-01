var brCore = require("br/Core");
var WritableProperty = require('br-presenter/property/WritableProperty');
var PresentationNode = require('br-presenter/node/PresentationNode');

var SimplePresentationNode = function(vPropValue)
{
	this.property = new WritableProperty(vPropValue);
};
brCore.extend(SimplePresentationNode, PresentationNode);

module.exports = SimplePresentationNode;
