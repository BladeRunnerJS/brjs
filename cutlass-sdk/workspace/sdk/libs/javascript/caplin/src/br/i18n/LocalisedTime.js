"use strict";

var Errors = require('br/Errors');
var i18n = require('br/i18n');

/**
 * @private
 */
function LocalisedTime(time) {
	if (!isValidTime(time)) {
		var exceptionMessage = "A LocalisedTime object could not be instantiated from: " + time;
		throw new Errors.InvalidParametersError(exceptionMessage);
	}
	this.time = time;
};

LocalisedTime.prototype.format = function() {
	var timeString = String(this.time);
	var timeSeparatorToken = i18n("ct.i18n.time.format.separator");
	var replacementPattern = "$1" + timeSeparatorToken;
	var regExp = /(\d{2})/g;
	var formattedTime = timeString.replace(regExp, replacementPattern);
	
	var lastChar = formattedTime.length - 1;
	if (formattedTime.charAt(lastChar) === timeSeparatorToken) {
		return formattedTime.substring(0, lastChar);
	}
	return formattedTime;
};

function isValidTime(vTime) {
	if (vTime && isCorrectLength(vTime)) {
		return (!isNaN(vTime));
	}
	return false;
}

function isCorrectLength(vTime) {
	var nLength = String(vTime).length;
	return (nLength > 0 && nLength < 7) ? true : false;
}

module.exports = LocalisedTime;
