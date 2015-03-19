/**
 * @module br/formatting/KeyValueFormatter
 */

var topiarist = require('topiarist');
var Formatter = require('br/formatting/Formatter');

/**
 * @class
 * @alias module:br/formatting/KeyValueFormatter
 * @implements module:br/formatting/Formatter
 *
 * @classdesc
 * Substitutes a value with a mapped value if the a mapped value exists othewise it returns the
 * value itself.
 *
 * The <code>mAttributes</code> argument should have the map holding the mappings in it's
 * <code>map</code> key.
 */
function KeyValueFormatter() {}

topiarist.implement(KeyValueFormatter, Formatter);

/**
 * Substitutes a value with a mapped value if the a mapped value exists otherwise it returns the
 * value itself.
 *
 * @param {Variant} vValue  the key which is expected to have mapping to a value in mAttributes.map.
 * @param {Map} mAttributes the object which holds a map of key-value pairs in its "map" element.
 * @return  the found value for the passed key or the the key if the value was not found.
 */
KeyValueFormatter.prototype.format = function(vValue, mAttributes) {
	var mKeyValues = mAttributes.map;
	return mKeyValues[vValue] || vValue;
};

/**
 * @private
 */
KeyValueFormatter.prototype.toString = function() {
	return "br.formatting.KeyValueFormatter";
};

module.exports = KeyValueFormatter;
