'use strict';

/**
 * Utility methods for numbers
 * @module br/util/Number
 */
var StringUtility = require('br/util/StringUtility');

/**
 * @alias module:br/util/Number
 * @classdesc
 */
function NumberUtil() {
}

/**
 * Returns a numeric representation of the sign on the number.
 *
 * @param {Number} n  The number (or a number as a string)
 * @return {int} 1 for positive values, -1 for negative values, or the original value for zero and non-numeric values.
 */
NumberUtil.sgn = function(n) {
	return n > 0 ? 1 : n < 0 ? -1 : n;
};

/**
 * @param {Object} n
 * @return {boolean} true for numbers and their string representations and false for other values including non-numeric
 *  strings, null, Infinity, NaN.
 */
NumberUtil.isNumber = function(n) {
	if (typeof n === 'string') {
		n = n.trim();
	}

	return n != null && n !== '' && n - n === 0;
};

/**
 * Formats the number to the specified number of decimal places.
 *
 * @param {Number} n The number (or a number as a string).
 * @param {Number} dp The number of decimal places.
 * @return {String} The formatted number.
 */
NumberUtil.toFixed = function(n, dp) {
	//return this.isNumber(n) && dp != null ? Number(n).toFixed(dp) : n;
	//Workaround for IE8/7/6 where toFixed returns 0 for (0.5).toFixed(0) and 0.0 for (0.05).toFixed(1)
	if (this.isNumber(n) && dp != null) {
		var sgn = NumberUtil.sgn(n);
		n = sgn * n;
		var nFixed = (Math.round(Math.pow(10, dp)*n)/Math.pow(10, dp)).toFixed(dp);
		return (sgn * nFixed).toFixed(dp);
	}

	return n;
};

/**
 * Formats the number to the specified number of significant figures. This fixes the bugs in the native Number function
 *  of the same name that are prevalent in various browsers. If the number of significant figures is less than one,
 *  then the function has no effect.
 *
 * @param {Number} n The number (or a number as a string).
 * @param {Number} sf The number of significant figures.
 * @return {String} The formatted number.
 */
NumberUtil.toPrecision = function(n, sf) {
	return this.isNumber(n) && sf > 0 ? Number(n).toPrecision(sf) : n;
};

/**
 * Formats the number to the specified number of decimal places, omitting any trailing zeros.
 *
 * @param {Number} n The number (or a number as a string).
 * @param {Number} rounding  The number of decimal places to round.
 * @return {String} The rounded number.
 */
NumberUtil.toRounded = function(n, rounding) {
	//return this.isNumber(n) && rounding != null ? String(Number(Number(n).toFixed(rounding))) : n;
	//Workaround for IE8/7/6 where toFixed returns 0 for (0.5).toFixed(0) and 0.0 for (0.05).toFixed(1)
	if (this.isNumber(n) && rounding != null) {
		var sgn = NumberUtil.sgn(n);
		n = sgn * n;
		var nRounded = (Math.round(Math.pow(10, rounding)*n)/Math.pow(10, rounding)).toFixed(rounding);
		return sgn * nRounded;
	}

	return  n;
};

/**
 * Logarithm to base 10.
 *
 * @param {Number} n The number (or a number as a string).
 * @return {Number} The logarithm to base 10.
 */
NumberUtil.log10 = function(n) {
	return Math.log(n) / Math.LN10;
};

/**
 * Rounds a floating point number
 *
 * @param {Number} n The number (or a number as a string).
 * @return {Number} The formatted number.
 */
NumberUtil.round = function(n) {
	var dp = 13 - (n ? Math.ceil(this.log10(Math.abs(n))) : 0);
	return this.isNumber(n) ? Number(Number(n).toFixed(dp)) : n;
};

/**
 * Pads the integer part of a number with zeros to reach the specified length.
 *
 * @param {Number} value  The number (or a number as a string).
 * @param {Number} numLength  The required length of the number.
 * @return {String} The formatted number.
 */
NumberUtil.pad = function(value, numLength) {
	if (this.isNumber(value)) {
		var nAbsolute = Math.abs(value);
		var sInteger = new String(parseInt(nAbsolute));
		var nSize = numLength || 0;
		var sSgn = value < 0 ? "-" : "";
		value = sSgn + StringUtility.repeat("0", nSize - sInteger.length) + nAbsolute;
	}

	return value;
};

/**
 * Counts the amount of decimal places within a number.
 * Also supports scientific notations
 *
 * @param {Number} n The number (or a number as a string).
 * @return {Number} The number of decimal places
 */
NumberUtil.decimalPlaces = function(n) {
	var match = (''+n).match(/(?:\.(\d+))?(?:[eE]([+-]?\d+))?$/);
	if (!match) {
		return 0;
	}
	return Math.max(
		0,
		// Number of digits right of decimal point.
		(match[1] ? match[1].length : 0)
		// Adjust for scientific notation.
		- (match[2] ? +match[2] : 0));
}

module.exports = NumberUtil;
