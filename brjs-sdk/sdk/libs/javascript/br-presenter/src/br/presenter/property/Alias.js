'use strict';

var Core = require('br/Core');
var Errors = require('br/Errors');
var Property = require('br/presenter/property/Property');

/**
 * @module br/presenter/property/Alias
 */

/**
 * Constructs a new <code>Alias</code> instance.
 * 
 * @class
 * @alias module:br/presenter/property/Alias
 * @extends module:br/presenter/property/Property
 * 
 * @classdesc
 * <code>Alias</code> instances wrap instances of {@link module:br/presenter/property/Property} and keep the
 * values synchronised, to allow access to the same property from multiple paths (e.g. double binding).
 * 
 * @param {module:br/presenter/property/Property} oProperty The property to wrap.
 */
function Alias(oProperty) {
	if (!(oProperty instanceof Property)) {
		throw new Errors.InvalidParametersError('An Alias can only constructed with a presenter Property');
	}

	/** @private */
	this.m_oWrappedProperty = oProperty;

	oProperty.addChangeListener(this._onWrappedChanged.bind(this), false);
	var vInitial = oProperty.getValue();
	Property.call(this, vInitial);
}

Core.extend(Alias, Property);

/**
 * @private
 */
Alias.prototype._onWrappedChanged = function() {
	var vValue = this.m_oWrappedProperty.getValue();
	this._$setInternalValue(vValue);
};

Alias.prototype.getValue = function() {
	return this.m_oWrappedProperty.getValue();
};

Alias.prototype.getFormattedValue = function() {
	return this.m_oWrappedProperty.getFormattedValue();
};

Alias.prototype.getRenderedValue = function() {
	return this.m_oWrappedProperty.getRenderedValue();
};

module.exports = Alias;
