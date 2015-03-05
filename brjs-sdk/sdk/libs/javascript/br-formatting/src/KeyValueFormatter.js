/**
 * @module br/formatting/KeyValueFormatter
 */

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
br.formatting.KeyValueFormatter = function() {
};

br.Core.implement(br.formatting.KeyValueFormatter, br.formatting.Formatter);

/**
 * Substitutes a value with a mapped value if the a mapped value exists otherwise it returns the
 * value itself.
 *
 * @param {Variant} vValue  the key which is expected to have mapping to a value in mAttributes.map.
 * @param {Map} mAttributes the object which holds a map of key-value pairs in its "map" element.
 * @return  the found value for the passed key or the the key if the value was not found.
 */
br.formatting.KeyValueFormatter.prototype.format = function(vValue, mAttributes) {
	var mKeyValues = mAttributes.map;
	return mKeyValues[vValue] || vValue;
};

/**
 * @private
 */
br.formatting.KeyValueFormatter.prototype.toString = function() {
	return "br.formatting.KeyValueFormatter";
};
