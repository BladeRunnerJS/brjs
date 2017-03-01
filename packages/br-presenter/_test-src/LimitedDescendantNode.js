require('../_resources-test-at/html/test-form.html');
var brCore = require("br/Core");
var LimitedParentNode = require('br-presenter/_test-src/LimitedParentNode');
var WritableProperty = require('br-presenter/property/WritableProperty');

var LimitedDescendantNode = function()
{
	this.childProperty = new WritableProperty("c");
};
brCore.extend(LimitedDescendantNode, LimitedParentNode);

module.exports = LimitedDescendantNode;
