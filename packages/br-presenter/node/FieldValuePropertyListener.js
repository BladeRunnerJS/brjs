'use strict';

var PropertyListener = require('br-presenter/property/PropertyListener');
var Core = require('br/Core');

/**
 * @module br/presenter/node/FieldValuePropertyListener
 */

/**
 * @private
 * @class
 * @alias module:br/presenter/node/FieldValuePropertyListener
 * @implements module:br/presenter/property/PropertyListener
 *
 * @param {module:br/presenter/node/Field} oField
 *
 */
function FieldValuePropertyListener(oField) {
	this.m_oField = oField;
	oField.value.addListener(this, true);
	// TODO: we need to invoke removeListener() in our destructor
}

Core.inherit(FieldValuePropertyListener, PropertyListener);


// *********************** PropertyListener Interface ***********************

/**
 * @private
 * @see br.presenter.property.PropertyListener#onValidationComplete
 */
FieldValuePropertyListener.prototype.onValidationComplete = function() {
	if (this.m_oField.pending) {
		this.m_oField.pending.setValue(false);
	}
};

/**
 * @private
 * @see br.presenter.property.PropertyListener#onPropertyChanged
 */
FieldValuePropertyListener.prototype.onPropertyChanged = function() {
	if (this.m_oField.pending) {
		this.m_oField.pending.setValue(true);
	}
};

/**
 * @private
 * @see br.presenter.property.PropertyListener#onValidationSuccess
 */
FieldValuePropertyListener.prototype.onValidationSuccess = function(vPropertyValue, sErrorMessage) {
	this.m_oField.hasError.setValue(false);
	this.m_oField.failureMessage.setValue('');
};

/**
 * @private
 * @see br.presenter.property.PropertyListener#onValidationError
 */
FieldValuePropertyListener.prototype.onValidationError = function(vPropertyValue, sErrorMessage) {
	this.m_oField.hasError.setValue(true);
	this.m_oField.failureMessage.setValue(sErrorMessage);
};

module.exports = FieldValuePropertyListener;
