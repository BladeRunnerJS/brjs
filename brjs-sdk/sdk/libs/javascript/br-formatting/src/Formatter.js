/**
 * @module br/formatting/Formatter
 */

var Errors = require("br/Errors");

/**
 * @class
 * @interface
 * @alias module:br/formatting/Formatter
 * 
 * @classdesc
 * Represents an object with the capability to format values.
 */
function Formatter() {
}

/**
 * Formats a value according to some provided attributes.
 * 
 * @param {Variant} vValue the value to format. May be null or undefined.
 * @param {Map} mAttributes A map of attributes to control the way this formatter works.  May not be null or undefined.
 * 
 * @type String
 * @return the formatted value. May not be null or undefined.
 */
Formatter.prototype.format = function(vValue, mAttributes) {
	throw new Errors.UnimplementedInterfaceError("Formatter.format() has not been implemented.");
};

module.exports = Formatter;
