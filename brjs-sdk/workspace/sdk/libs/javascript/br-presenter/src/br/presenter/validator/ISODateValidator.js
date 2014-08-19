/**
 * @module br/presenter/validator/ISODateValidator
 */

/**
 * @private
 * @classdesc
 * @class
 * @implements module:br/core/Validator
 */
br.presenter.validator.ISODateValidator = function() {
	/** @private */
	this.m_oSplitterRegex = /^(\d{4})-(\d{2})-(\d{2})$/;

	/** @private */
	this.m_oSplitterNoDashesRegex = /^(\d{4})(\d{2})(\d{2})$/;
};
br.Core.implement(br.presenter.validator.ISODateValidator, br.presenter.validator.Validator);

/**
 * @private
 * @param vValue
 * @param mAttributes
 * @param oValidationResult
 */
br.presenter.validator.ISODateValidator.prototype.validate = function(value, mAttributes, validationResult) {
	var isValid = false,
		i18n = require("br/I18n");
		validationMessage = i18n('ct.presenter.validator.invalidISODateFormat', { value: value });

	if (typeof value === 'undefined' || value === null || value === '') {
		isValid = true;
		validationMessage = i18n('ct.presenter.validator.valueNullUndefinedOrEmptyString');
	} else if (typeof value.match === 'function') {
		var match = value.match(this.m_oSplitterRegex);
		if (!match) {
			// Try with regex that has dashes optional
			match = value.match(this.m_oSplitterNoDashesRegex);
		}

		if (match !== null) {
			// Convert captured results to numbers for convenience:
			match[1] = Number(match[1]);
			match[2] = Number(match[2]);
			match[3] = Number(match[3]);

			// Use built-in JavaScript date object to check the validity of the value
			var dt = new Date(match[1], match[2]-1, match[3]);

			if (dt.getFullYear() === match[1] && dt.getMonth() === (match[2]-1) && dt.getDate() === match[3] ) {
				isValid = true;
				validationMessage = '';
			}
		}
	}

	validationResult.setResult(isValid, validationMessage);
};

/**
 * @private
 * @param {String} sISODate
 * @type Boolean
 */
br.presenter.validator.ISODateValidator.prototype.isValidISODate = function (ISODateString) {
	var validationResult = new br.presenter.validator.ValidationResult();
	this.validate(ISODateString, {}, validationResult);
	return validationResult.isValid();
};
