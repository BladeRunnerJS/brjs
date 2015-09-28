var brCore = require("br/Core");
var PresentationNode = require("br/presenter/node/PresentationNode");
var UpwardRefsGrandchildNode = require("br/presenter/testing/node/UpwardRefsGrandchildNode");

var UpwardRefsChildNode = function(oParent)
{
	this.oParent = oParent;
	this.oGrandChild = new UpwardRefsGrandchildNode(this, oParent)
};
brCore.extend(UpwardRefsChildNode, PresentationNode);

module.exports = UpwardRefsChildNode;
