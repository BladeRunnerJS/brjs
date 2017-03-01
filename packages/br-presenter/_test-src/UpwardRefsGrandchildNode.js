var brCore = require("br/Core");
var PresentationNode = require('br-presenter/node/PresentationNode');

var UpwardRefsGrandchildNode = function(oParent, oGrandparent)
{
	this.oParent = oParent;
	this.oGrandparent = oGrandparent;
};
brCore.extend(UpwardRefsGrandchildNode, PresentationNode);

module.exports = UpwardRefsGrandchildNode;
