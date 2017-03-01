require('../_resources-test-at/html/test-form.html');
var brCore = require("br/Core");
var PresentationNode = require('br-presenter/node/PresentationNode');
var WritableProperty = require('br-presenter/property/WritableProperty');
var GrandChildPresentationNode = require('br-presenter/_test-src/GrandChildPresentationNode');

var ChildPresentationNode = function()
{
	this.m_oPrivateProperty2 = new WritableProperty();
	
	this.property2 = new WritableProperty("p2");
	this.property3 = new WritableProperty("p3");
	this.grandchild = new GrandChildPresentationNode();
};
brCore.extend(ChildPresentationNode, PresentationNode);

module.exports = ChildPresentationNode;
