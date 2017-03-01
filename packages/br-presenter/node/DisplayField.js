'use strict';

var PresentationNode = require('br-presenter/node/PresentationNode');
var Core = require('br/Core');
var WritableProperty = require('br-presenter/property/WritableProperty');
var Property = require('br-presenter/property/Property');

/**
 * @module br/presenter/node/DisplayField
 */

/**
 * @class
 * @alias module:br/presenter/node/DisplayField
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model a non-input field on screen.
 * 
 * @param {Object} vValue (optional) The initial value of the field, either using a
 * primitive type or as a {@link module:br/presenter/property/Property}.
 */
function DisplayField(vValue) {
	if (!(vValue instanceof Property)) {
		vValue = new WritableProperty(vValue);
	}

	/**
	 * The textual label associated with the field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = new WritableProperty('');

	/**
	 * A boolean property representing whether the field is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new WritableProperty(true);

	/**
	 * The current value displayed within the field.
	 * @type br.presenter.property.WritableProperty
	 */
	this.value = vValue;
}

Core.extend(DisplayField, PresentationNode);

module.exports = DisplayField;
