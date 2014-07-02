/**
 * This is a static class that never needs to be instantiated.
 * @constructor
 * 
 * @class
 * Utility class that provides methods for string manipulation.
 */
br.util.StringUtility = function()
{
};

/**
 * @private
 */
br.util.StringUtility.repeat = function(sUnit, nCount)
{
	var pRepeat = [];
	for (var i = 0, n = Math.floor(nCount); i < n; ++i) {
		pRepeat.push(sUnit);
	}
	return pRepeat.join("");
};
