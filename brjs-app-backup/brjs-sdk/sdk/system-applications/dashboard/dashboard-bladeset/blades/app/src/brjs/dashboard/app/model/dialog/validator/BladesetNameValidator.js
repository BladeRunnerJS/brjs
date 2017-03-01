'use strict';

var Validator = require('br/presenter/validator/Validator');
var Core = require('br/Core');
var NameValidator = require("brjs/dashboard/app/NameValidator");

function BladesetNameValidator(oPresentationModel) {
	this.m_oPresentationModel = oPresentationModel;
}

Core.extend(BladesetNameValidator, Validator);

BladesetNameValidator.prototype.validate = function(vValue, mAttributes, oValidationResult) {
	if (!NameValidator.isValidPackageName(vValue)) {
		oValidationResult.setResult(false, NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	} else if (NameValidator.isReservedJsWord(vValue)) {
		oValidationResult.setResult(false, NameValidator.RESERVED_JS_WORD_MESSAGE);
	} else if (this.m_oPresentationModel && this.m_oPresentationModel.appDetailScreen.getBladeset(vValue)) {
		oValidationResult.setResult(false, "A bladeset called '" + vValue + "' already exists.");
	} else {
		oValidationResult.setResult(true);
	}
};

module.exports = BladesetNameValidator;
