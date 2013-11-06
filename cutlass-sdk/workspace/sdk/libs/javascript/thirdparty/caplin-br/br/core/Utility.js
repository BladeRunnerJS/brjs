define('br/core/Utility', function(require, exports, module) {
	"use strict";

	var global = new Function("return this")();

	/**
	 * Locate navigates an object hierarchy with dotted notation.
	 */
	function locate(path, root) {
		if (typeof path !== 'string') {
			throw new TypeError('Utility.locate: Path must be a string, was ' + typeof path + '.');
		}
		
		if (arguments.length < 2) {
			root = global;
		}
		
		return path.split(".").reduce(function(accumulator, value) {
			return accumulator != null ? accumulator[value] : undefined;
		}, root);
	};
	exports.locate = locate;

	/**
	 * isEmpty returns true if there are no enumerable keys (as found by for-in) in the provided object, false otherwise.
	 */
	function isEmpty(object) {
		for (var key in object) {
			return false;
		}
		return true;
	}
	exports.isEmpty = isEmpty;
	
	/**
	 * addValuesToSet adds keys from the values array to the set object with values all set to true.
	 */
	function addValuesToSet(set, values) {
		if (set == null) {
			throw new TypeError('Utility.addValuesToSet: Set must be an object, was ' + typeof set + '.');
		}
		if (Array.isArray(values) === false) {
			throw new TypeError('Utility.addValuesToSet: Values must be an array, was ' + typeof values + '.');
		}

		for (var i = 0; i < values.length; ++i) {
			var value = values[i];
			if (value == null) {
				throw new TypeError("Utility.addValuesToSet: Values to add to a set cannot be "+value+".");
			}
			set[value] = true;
		}
		
		return set;
	}
	exports.addValuesToSet = addValuesToSet;	
	
});