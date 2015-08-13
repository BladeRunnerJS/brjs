'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var Property = require('br/presenter/property/Property');

/**
 * @module br/presenter/node/Button
 */

/**
 * @class
 * @alias module:br/presenter/node/Button
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @classdesc
 * A <code>PresentationNode</code> containing all of the attributes necessary to
 * model a button on screen.
 * 
 * @param vLabel (optional) The text that will be displayed within the button &mdash; can be a <code>String</code> or a {@link module:br/presenter/property/Property}.
 */
function Button(vLabel) {
	if (!(vLabel instanceof Property)) {
		vLabel = new WritableProperty(vLabel || '');
	}

	/**
	 * The text currently displayed within the button.
	 * @type br.presenter.property.WritableProperty
	 */
	this.label = vLabel;

	/**
	 * A boolean property representing whether the button is enabled or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.enabled = new WritableProperty(true);

	/**
	 * A boolean property representing whether the button is visible or not.
	 * @type br.presenter.property.WritableProperty
	 */
	this.visible = new WritableProperty(true);
}

Core.extend(Button, PresentationNode);

module.exports = Button;
