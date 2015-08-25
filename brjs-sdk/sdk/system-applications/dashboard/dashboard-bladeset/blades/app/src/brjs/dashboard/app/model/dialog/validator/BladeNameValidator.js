'use strict';

var Validator = require('br/presenter/validator/Validator');
var Core = require('br/Core');
var NameValidator = require("brjs/dashboard/app/NameValidator");

function BladeNameValidator(oPresentationModel) {
	this.m_oPresentationModel = oPresentationModel;
}

Core.extend(BladeNameValidator, Validator);

BladeNameValidator.prototype.setBladeset = function(sBladeset) {
	this.m_sBladeset = sBladeset;
};

BladeNameValidator.prototype.validate = function(vValue, mAttributes, oValidationResult) {
	if (!NameValidator.isValidPackageName(vValue)) {
		oValidationResult.setResult(false, NameValidator.INVALID_PACKAGE_NAME_MESSAGE);
	} else if (NameValidator.isReservedJsWord(vValue)) {
		oValidationResult.setResult(false, NameValidator.RESERVED_JS_WORD_MESSAGE);
	} else if (this.m_oPresentationModel && this.m_oPresentationModel.appDetailScreen.getBladeset(this.m_sBladeset).getBlade(vValue)) {
		// TODO: remove the need to check for this.m_oPresentationModel - this is undefined for UTs so the tests will fail without it
		oValidationResult.setResult(false, "A blade called '" + vValue + "' already exists.");
	} else {
		oValidationResult.setResult(true);
	}
};

module.exports = BladeNameValidator;
