'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');

/**
 * @module br/presenter/node/Option
 */

/**
 * A single option held within an {@link module:br/presenter/node/OptionsNodeList} instance.
 * 
 * @class
 * @alias module:br/presenter/node/Option
 * @extends module:br/presenter/node/PresentationNode
 * 
 * @param {String} sValue The (logical) value of the option.
 * @param {String} sLabel The label that is displayed on the screen.
 * @param {Boolean} bEnabled Is the option enabled or disabled (enabled by default).
 */
function Option(sValue, sLabel, bEnabled) {
	/**
	 * The value of the option.
	 * @type String
	 */
	this.value = new WritableProperty(sValue);

	/**
	 * The textual label associated with the option.
	 * @type String
	 */
	this.label = new WritableProperty(sLabel);

	/**
	 * If option is enabled
	 * @type String
	 */
	this.enabled = new WritableProperty(bEnabled === undefined ? true : bEnabled);
}

Core.extend(Option, PresentationNode);

/**
 * Returns the option label.
 * @type String
 */
Option.prototype.toString = function() {
	return this.label.getValue();
};

module.exports = Option;
