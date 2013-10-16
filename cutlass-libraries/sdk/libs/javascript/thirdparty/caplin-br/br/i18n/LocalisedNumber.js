define("br/i18n/LocalisedNumber", function (require, module, exports) {
	"use strict";

	var Errors = require('br/Errors');

	/**
	 * @private
	 */
	function LocalisedNumber(number) {
		if (!isValidNumber(number)) {
			var exceptionMessage = "A LocalisedNumber object could not be instantiated from: " + number + ".";
			throw new Errors.InvalidParametersError(exceptionMessage);
		}
		this.number = number;
	}

	LocalisedNumber.prototype.format = function(thousandsSeparator, decimalRadixCharacter) {
		var numberToFormat = String(this.number);
		var unsignedNumber = getUnsignedNumber(numberToFormat);
		if (unsignedNumber === null) { return ""; }
		var formattedNumber = addSeparator(unsignedNumber, thousandsSeparator);
		numberToFormat = numberToFormat.replace(".", decimalRadixCharacter);
		return numberToFormat.replace(unsignedNumber, formattedNumber);
	};


	function addSeparator(number, thousandsSeparator) {
		var length = number.length - 3;
		for (var i = length; i > 0; i -= 3) {
			number = number.substr(0, i) + thousandsSeparator + number.substr(i);
		}
		return number;
	}

	function getUnsignedNumber(number) {
		var match = number.match(/\d+/);
		return match != null ? String(match) : null;
	}

	function isValidType(number) {
		var numberType = typeof(number);
		return (numberType === "string" || numberType === "number");
	}

	function isValidNumber(number) {
		return isValidType(number) && number !== "" && !isNaN(number);
	};

	module.exports = LocalisedNumber;
});