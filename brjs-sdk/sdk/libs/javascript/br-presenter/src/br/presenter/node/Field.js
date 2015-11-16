'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var FieldValuePropertyListener = require('br/presenter/node/FieldValuePropertyListener');
var WritableProperty = require('br/presenter/property/WritableProperty');
var Errors = require('br/Errors');
var EditableProperty = require('br/presenter/property/EditableProperty');
var Property = require('br/presenter/property/Property');

/**
 * @module br/presenter/node/Field
 */

/**
 * @class
 * @alias module:br/presenter/node/Field
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model an input field on screen.
 * 
 * @param {Object} vValue (optional) The initial value of the field, either using a
 * primitive type or as a {@link module:br/presenter/property/EditableProperty}.
 */
function Field(vValue) {
	if ((vValue instanceof Property) && !(vValue instanceof EditableProperty)) {
		throw new Errors.InvalidParametersError("Field constructor: can't pass non-editable property as parameter");
	}

	if (!(vValue instanceof EditableProperty)) {
		vValue = new EditableProperty(vValue);
	}

	/**
	 * The textual label associated with the input field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = new WritableProperty('');

	/**
	 * The current value displayed within the input field.
	 * @type br.presenter.property.EditableProperty
	 */
	this.value = vValue;

	/**
	 * A boolean property that is <code>true</code> if {@link #value} has any validation errors, and <code>false</code> otherwise.
	 * @type br.presenter.property.WritableProperty
	 */
	this.hasError = new WritableProperty(false);

	/**
	 * A textual description of the currently failing validation message when {@link #hasError} is <code>true</code>.
	 * @type br.presenter.property.WritableProperty
	 */
	this.failureMessage = new WritableProperty();

	/**
	 * A boolean property representing whether the input field is enabled or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.enabled = new WritableProperty(true);

	/**
	 * A boolean property representing whether the input field is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new WritableProperty(true);

	/**
	 * A boolean property representing whether the input field is currently validating
	 * @type br.presenter.property.WritableProperty
	 */
	this.isValidationInProgress = new WritableProperty(false);

	/**
	 * A boolean property representing whether the input field is currently focused.
	 * Not all fields will care about tracking whether they have focus or not. For these fields leaving hasFocus
	 * as undefined seems the most appropriate choice.
	 * @type br.presenter.property.WritableProperty
	 */
	this.hasFocus = new WritableProperty(undefined);

	/**
	 * The logical control-name the field is being bound to &mdash; this
	 * value will appear within the <code>name</code> attribute if being bound
	 * to a native HTML control.
	 * @type br.presenter.property.WritableProperty
	 */
	this.controlName = new WritableProperty('');

	/** @private */
	this.m_oValueListener = new FieldValuePropertyListener(this);
}

Core.extend(Field, PresentationNode);

module.exports = Field;
