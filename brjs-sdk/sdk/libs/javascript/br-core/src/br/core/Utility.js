"use strict";

/**
* @module br/core/Utility
*/

var global = new Function("return this")();

/**
* Navigates an object hierarchy with dotted notation.
* @param {String} path
* @param {String} root
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
* Returns true if there are no enumerable keys (as found by for-in) in the provided object, false otherwise.
* @param {Object} object
*/
function isEmpty(object) {
	for (var key in object) {
		return false;
	}
	return true;
}
exports.isEmpty = isEmpty;

/**
* Adds keys from the values array to the set object with values all set to true.
* @param {Object} set The set object
* @param {Array} values The array of values
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
