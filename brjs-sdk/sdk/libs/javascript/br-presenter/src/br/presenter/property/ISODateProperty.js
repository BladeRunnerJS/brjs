'use strict';

var Errors = require('br/Errors');
var ValidationResult = require('br/presenter/validator/ValidationResult');
var WritableProperty = require('br/presenter/property/WritableProperty');
var ISODateValidator = require('br/presenter/validator/ISODateValidator');
var Core = require('br/Core');

/**
 * @module br/presenter/property/ISODateProperty
 */

var momentjs = require('momentjs');

/**
 * Constructs a new <code>ISODateProperty</code> instance.
 * 
 * @class
 * @alias module:br/presenter/property/ISODateProperty
 * @extends module:br/presenter/property/WritableProperty
 * 
 * @classdesc
 * <code>ISODateProperty</code> is a {@link module:br/presenter/property/WritableProperty},
 * representing an ISO date
 * 
 * @param vValue (optional) A valid ISO Date string (YYYY-MM-DD) or a native Date object
 */
function ISODateProperty(vValue) {
	/** @private */
	this.m_oDateValidator = new ISODateValidator();

	vValue = this._validateDate(vValue);

	// super constructor
	WritableProperty.call(this, vValue);
}

Core.extend(ISODateProperty, WritableProperty);

/**
 * Gets the date object that is property represents
 * @type Date
 */
ISODateProperty.prototype.getDateValue = function() {
	var sDate = this.getValue();
	if (sDate) {
		return new Date(Number(sDate.substr(0, 4)), Number(sDate.substr(5, 2)) - 1, Number(sDate.substr(8, 2)));
	}
	return null;
};

/**
 * Sets the value of the date
 * @param {Variant} vValue The new date value (A valid ISO Date string (YYYY-MM-DD) or a native Date object)
 */
ISODateProperty.prototype.setValue = function(vValue) {
	vValue = this._validateDate(vValue);
	WritableProperty.prototype.setValue.call(this, vValue);
};

/**
 * @private
 */
ISODateProperty.prototype._validateDate = function(vDate) {
	if (vDate instanceof Date) {
		vDate = moment(vDate).format('YYYY-MM-DD');
		return vDate;
	}
	var oValidationResult = new ValidationResult();
	this.m_oDateValidator.validate(vDate, {}, oValidationResult);
	if (!oValidationResult.isValid()) {
		throw new Errors.InvalidParametersError(oValidationResult.getFailureMessage());
	} else {
		return vDate;
	}
};

module.exports = ISODateProperty;
