'use strict';

var ValidationResultListener = require('br-presenter/validator/ValidationResultListener');
var Core = require('br/Core');
var ValidationResult = require('br-presenter/validator/ValidationResult');

/**
 * @module br/presenter/property/ValidationResultCollator
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/property/ValidationResultCollator
 */
function ValidationResultCollator(oValidationResultListener, nValidators) {
	/** @private */
	this.m_oValidationResultListener = oValidationResultListener;

	/** @private */
	this.m_pValidationResults = [];

	/** @private */
	this.m_bReceivedValidationError = false;

	/** @private */
	this.m_nValidators = nValidators;
}

/**
 * @private
 */
ValidationResultCollator.prototype.createValidationResult = function(nValidatorIndex) {
	var oValidationResultReceiver = new ValidationResultCollator.ValidationResultReceiver(this, nValidatorIndex);

	return new ValidationResult(oValidationResultReceiver);
};

/**
 * @private
 */
ValidationResultCollator.prototype.cancelValidationResults = function() {
	this.m_oValidationResultListener = null;
};

/**
 * @private
 */
ValidationResultCollator.prototype._onNextValidationResultReceived = function(oValidationResult, nValidatorIndex) {
	if (this.m_oValidationResultListener && !this.m_bReceivedValidationError) {
		this.m_pValidationResults[nValidatorIndex] = oValidationResult;
		for (var i = 0, max = this.m_nValidators; i < max; ++i) {
			if (!this.m_pValidationResults[i]) // when the i-th validator hasn't run yet this will be undefined
			{
				break;
			} else if (!this.m_pValidationResults[i].isValid()) {
				this.m_bReceivedValidationError = true;
				this.m_oValidationResultListener.onValidationResultReceived(this.m_pValidationResults[i]);
				this.m_pValidationResults = [];
				break;
			}
			// Only send success once, when all validators have been successful
			else if (i === (max - 1)) {
				this.m_oValidationResultListener.onValidationResultReceived(this.m_pValidationResults[i]);
				this.m_pValidationResults = [];
			}
		}
	}
};

/**
 * @private
 */
ValidationResultCollator.ValidationResultReceiver = function(oCollator, nValidatorIndex) {
	this.m_oCollator = oCollator;
	this.m_nValidatorIndex = nValidatorIndex;
};
Core.implement(ValidationResultCollator.ValidationResultReceiver, ValidationResultListener);

/**
 * @private
 */
ValidationResultCollator.ValidationResultReceiver.prototype.onValidationResultReceived = function(oValidationResult) {
	this.m_oCollator._onNextValidationResultReceived(oValidationResult, this.m_nValidatorIndex);
};

module.exports = ValidationResultCollator;
