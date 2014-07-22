'use strict';

/**
 * @name br.util.StringUtility
 * This is a static class that never needs to be instantiated.
 * @constructor
 * 
 * @class
 * Utility class that provides methods for string manipulation.
 */
function StringUtility() {
}

/** @private */
StringUtility.repeat = function(unit, numOfRepeats) {
	var repeats = [];
	for (var idx = 0, n = Math.floor(numOfRepeats); idx < n; idx++) {
		repeats.push(unit);
	}

	return repeats.join('');
};

module.exports = StringUtility;
