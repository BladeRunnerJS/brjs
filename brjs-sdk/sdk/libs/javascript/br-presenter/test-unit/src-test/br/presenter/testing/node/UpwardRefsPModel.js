var brCore = require("br/Core");
var PresentationModel = require("br/presenter/PresentationModel");
var UpwardRefsChildNode = require("br/presenter/testing/node/UpwardRefsChildNode");

var UpwardRefsPModel = function()
{
	this.child = new UpwardRefsChildNode(this);
};
brCore.extend(UpwardRefsPModel, PresentationModel);

module.exports = UpwardRefsPModel;
