var Errors = require("br/Errors");

/**
 * @class
 * Represents an object with the capability to format values.
 * @interface 
 */
br.presenter.formatter.Formatter = function() {};

/**
 * Formats a value according to some provided attributes.
 * 
 * @param {Variant} vValue the value to format. May be null or undefined.
 * @param {Map} mAttributes A map of attributes to control the way this formatter works.  May not be null or undefined.
 * 
 * @type String
 * @return the formatted value. May not be null or undefined.
 */
br.presenter.formatter.Formatter.prototype.format = function(vValue, mAttributes)
{
	throw new Errors.UnimplementedInterfaceError("Formatter.format() has not been implemented.");
};
