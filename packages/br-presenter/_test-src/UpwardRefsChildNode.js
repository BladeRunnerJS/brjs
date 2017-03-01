require('../_resources-test-at/html/test-form.html');
var brCore = require("br/Core");
var PresentationNode = require('br-presenter/node/PresentationNode');
var UpwardRefsGrandchildNode = require('br-presenter/_test-src/UpwardRefsGrandchildNode');

var UpwardRefsChildNode = function(oParent)
{
	this.oParent = oParent;
	this.oGrandChild = new UpwardRefsGrandchildNode(this, oParent)
};
brCore.extend(UpwardRefsChildNode, PresentationNode);

module.exports = UpwardRefsChildNode;
