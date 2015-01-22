'use strict';

/**
 * @module br/util/ArrayUtility
 */

var Errors = require('br/Errors');

/**
 * This is a static class that never needs to be instantiated.
 * 
 * @class
 * @alias module:br/util/ArrayUtility
 * 
 * @classdesc
 * Utility class that provides methods for array manipulation.
 */
var ArrayUtility = function() {
};

/**
 * Return <code>true</code> if the input array contains the input value, <code>false</code> otherwise.
 *
 * @param {Array} arrayToSearch The Array to test for the presence of the input variant. May not be null or undefined.
 * @param {Variant} valueToFind Variant whose presence in the input array is to be tested.  Works with null, undefined
 *  and NaN.
 * @return {Boolean} <code>true</code> if the specified value was found in the array, <code>false</code> otherwise.
 */
ArrayUtility.inArray = function(arrayToSearch, valueToFind) {
	if (!Array.isArray(arrayToSearch)) {
		throw new Errors.InvalidParametersError('ArrayUtility.inArray: ' + arrayToSearch + ' is not an array.');
	}

	// this is only true for NaN.
	if (valueToFind !== valueToFind) {
		for (var idx = 0; idx < arrayToSearch.length; idx++) {
			if (arrayToSearch[idx] !== arrayToSearch[idx]) {
				return true;
			}
		}
	}

	return arrayToSearch.indexOf(valueToFind) >= 0;
};

/**
 * Removes the first example of the specified item in the specified array, or does nothing if the item is not found.
 *
 * <p>Since this method uses the built-in <code>indexOf</code> which does work with <code>NaN</code>, it will not work
 *  if called with NaN and will throw an InvalidParametersError.  If you need to remove <code>NaN</code>, you'll have
 *  to loop over the array and then splice it out.</p>
 *
 * @param {Array} arrayToRemoveFrom The Array from which to remove the specified item. May not be null or undefined.
 * @param {Variant} valueToRemove The Variant to be removed from the specified array. May not be NaN.
 * @return {Array} The input array with the specified item removed, or the original array if the item was not found.
 */
ArrayUtility.removeItem = function(arrayToRemoveFrom, valueToRemove) {
	if (!Array.isArray(arrayToRemoveFrom)) {
		throw new Errors.InvalidParametersError('ArrayUtility.removeItem: ' + arrayToRemoveFrom + ' is not an array.');
	}

	if (valueToRemove !== valueToRemove) {
		throw new Errors.InvalidParametersError('ArrayUtility.removeItem: this method cannot remove NaN from an array.');
	}

	var itemPosition = arrayToRemoveFrom.indexOf(valueToRemove);

	if (itemPosition >= 0) {
		arrayToRemoveFrom.splice(itemPosition, 1);
	}

	return arrayToRemoveFrom;
};

module.exports = ArrayUtility;
