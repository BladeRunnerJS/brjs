'use strict';

/**
* Interpolates a string with the arguments, used for error messages.
* @private
*/
function msg(str) {
	if (str == null) {
		return null;
	}

	for (var i = 1, len = arguments.length; i < len; ++i) {
		str = str.replace('{' + (i - 1) + '}', String(arguments[i]));
	}

	return str;
}

module.exports = msg;
