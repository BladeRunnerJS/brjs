var Formatter = require('br-presenter/formatter/Formatter');
var Core = require("br/Core");

UpperCaseFormatter = function()
{
    // nothing
};
Core.implement(UpperCaseFormatter, Formatter);

UpperCaseFormatter.prototype.format = function(vValue, mAttributes)
{
    return vValue.toUpperCase();
};

module.exports = new UpperCaseFormatter();
