require('../_resources-test-at/html/test-form.html');
var brCore = require("br/Core");
var PresentationModel = require('br-presenter/PresentationModel');
var UpwardRefsChildNode = require('br-presenter/_test-src/UpwardRefsChildNode');

var UpwardRefsPModel = function()
{
	this.child = new UpwardRefsChildNode(this);
};
brCore.extend(UpwardRefsPModel, PresentationModel);

module.exports = UpwardRefsPModel;
