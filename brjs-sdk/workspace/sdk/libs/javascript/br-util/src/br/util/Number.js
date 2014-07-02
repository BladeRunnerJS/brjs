/**
 * @class
 * Utility methods for numbers
 */
br.util.Number = function() {
};

/**
 * Returns a numeric representation of the sign on the number. 
 * 
 * @param {Number} n  The number (or a number as a string)
 * @type int
 * @return 1 for positive values, -1 for negative values, or the original value for zero and non-numeric values
 */
br.util.Number.sgn = function(n) {
	return n > 0 ? 1 : n < 0 ? -1 : n;
};

/**
 * @param {Object} n
 * @return  true for numbers and their string representations and false for other values including non-numeric strings, null, Infinity, NaN.
 * @type boolean
 */
br.util.Number.isNumber = function(n) {
	if(typeof n === "string")
	{
		n = (n.trim());
	}
	return n != null && n !== "" && n - n == 0;
};

/**
 * Formats the number to the specified number of decimal places.
 * 
 * @param {Number} n  The number (or a number as a string)
 * @param {Number} nDp  The number of decimal places
 * @return  The formatted number
 * @type String
 */
br.util.Number.toFixed = function(n, dp) {
	//return this.isNumber(n) && dp != null ? Number(n).toFixed(dp) : n;
	//Workaround for IE8/7/6 where toFixed returns 0 for (0.5).toFixed(0) and 0.0 for (0.05).toFixed(1)
	if (this.isNumber(n) && dp != null)
	{
		var sgn = br.util.Number.sgn(n);
		n = sgn * n;
		var nFixed = (Math.round(Math.pow(10, dp)*n)/Math.pow(10, dp)).toFixed(dp);
		return (sgn * nFixed).toFixed(dp);
	}
	return n;
};

/**
 * Formats the number to the specified number of significant figures.
 * 
 * This fixes the bugs in the native Number function of the same name that are prevalent in various browsers.
 * 
 * If the number of significant figures is less than one, then the function has no effect.
 * 
 * @param {Number} n  The number (or a number as a string)
 * @param {Number} nSf  The number of significant figures
 * @return  The formatted number
 * @type String
 */
br.util.Number.toPrecision = function(n, nSf) {
	return this.isNumber(n) && nSf > 0 ? Number(n).toPrecision(nSf) : n;
};

/**
 * Formats the number to the specified number of decimal places, omitting any trailing zeros.
 * 
 * @param {Number} n  The number (or a number as a string)
 * @param {Number} nRounding  The number of decimal places to round
 * @return  The rounded number.
 * @type String
 */
br.util.Number.toRounded = function(n, nRounding) {
	//return this.isNumber(n) && nRounding != null ? String(Number(Number(n).toFixed(nRounding))) : n;
	//Workaround for IE8/7/6 where toFixed returns 0 for (0.5).toFixed(0) and 0.0 for (0.05).toFixed(1)
	if(this.isNumber(n) && nRounding != null)
	{
		var sgn = br.util.Number.sgn(n);
		n = sgn * n;
		var nRounded = (Math.round(Math.pow(10, nRounding)*n)/Math.pow(10, nRounding)).toFixed(nRounding);
		return sgn * nRounded;
	}
	return  n;
};

/**
 * Logarithm to base 10.
 * 
 * @param {Number} n  The number (or a number as a string)
 * @return  The logarithm to base 10.
 * @type Number
 */
br.util.Number.log10 = function(n) {
	return Math.log(n) / Math.LN10;
};

/**
 * Rounds a floating point number 
 * 
 * @param {Number} n  The number (or a number as a string)
 * @return  The formatted number
 * @type Number
 */
br.util.Number.round = function(n) {
	dp = 13 - (n ? Math.ceil(this.log10(Math.abs(n))) : 0);
	return this.isNumber(n) ? Number(Number(n).toFixed(dp)) : n;
};

/**
 * Pads the integer part of a number with zeros to reach the specified length.
 * 
 * @param {Number} nValue  The number (or a number as a string)
 * @param {Number} nLength  The required length of the number
 * @return  The formatted number
 * @type String
 */
br.util.Number.pad = function(sValue, nLength) {
	if (this.isNumber(sValue)) {
		var nAbsolute = Math.abs(sValue);
		var sInteger = new String(parseInt(nAbsolute));
		var nSize = nLength || 0;
		var sSgn = sValue < 0 ? "-" : "";
		sValue = sSgn + br.util.StringUtility.repeat("0", nSize - sInteger.length) + nAbsolute;
	}
	return sValue;
};
