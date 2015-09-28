'use strict';

var Validator = require('br/presenter/validator/Validator');
var Core = require('br/Core');
var NameValidator = require("brjs/dashboard/app/NameValidator");

function AppNamespaceValidator() {
}

Core.extend(AppNamespaceValidator, Validator);

AppNamespaceValidator.prototype.validate = function(vValue, mAttributes, oValidationResult) {
	if (!NameValidator.isValidPackageName(vValue)) {
		oValidationResult.setResult(false, NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	} else if (NameValidator.isReservedJsWord(vValue)) {
		oValidationResult.setResult(false, NameValidator.RESERVED_JS_WORD_MESSAGE);
	} else if ((vValue == 'brjs') || (vValue == 'br')) {
		oValidationResult.setResult(false, "'" + vValue + "' is a reserved namespace.");
	} else {
		oValidationResult.setResult(true);
	}
};

module.exports = AppNamespaceValidator;
