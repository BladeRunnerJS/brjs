/**
 * @class
 *
 * Substitues a value with a mapped value if the a mapped value exists othewise it returns the
 * value itself.
 *
 * The <code>mAttributes</code> argument should have the map holding the mappings in it's
 * <code>map</code> key.
 * 
 * @implements br.presenter.formatter.Formatter
 * @singleton
 */
br.presenter.formatter.KeyValueFormatter = function() {
};

/**
 * Substitues a value with a mapped value if the a mapped value exists othewise it returns the
 * value itself.
 *
 * @param {Variant} vValue  the key which is expected to have mapping to a value in mAttributes.map.
 * @param {Map} mAttributes the object which holds a map of key-value pairs in its "map" element.
 * @return  the found value for the passed key or the the key if the value was not found.
 */
br.presenter.formatter.KeyValueFormatter.prototype.format = function(vValue, mAttributes) {
	var mKeyValues = mAttributes.map;
	return mKeyValues[vValue] || vValue;
};

/**
 * @private
 */
br.presenter.formatter.KeyValueFormatter.prototype.toString = function() {
	return "br.presenter.formatter.KeyValueFormatter";
};

br.Core.implement(br.presenter.formatter.KeyValueFormatter, br.presenter.formatter.Formatter);