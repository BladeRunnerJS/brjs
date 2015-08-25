var brCore = require("br/Core");
var ControlAdaptor = require("br/presenter/control/ControlAdaptor");

var TestControlAdaptor = function()
{
};
brCore.inherit(TestControlAdaptor, ControlAdaptor);

TestControlAdaptor.prototype.setOptions = function()
{
	// do nothing
};

TestControlAdaptor.prototype.setPresentationNode = function()
{
	// do nothing
};

TestControlAdaptor.prototype.onViewReady = function()
{
	// do nothing
};

module.exports = TestControlAdaptor;
