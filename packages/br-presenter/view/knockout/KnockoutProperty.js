'use strict';

var Errors = require('br/Errors');
var KnockoutObservable = require('br-presenter/view/knockout/KnockoutObservable');
var Core = require('br/Core');

/**
 * @module br/presenter/view/knockout/KnockoutProperty
 */

var presenter_knockout = require('presenter-knockout');

/**
 * @private
 * @class
 * @alias module:br/presenter/view/knockout/KnockoutProperty
 */
function KnockoutProperty() {
	// call super constructor
	KnockoutObservable.call(this);
}

Core.extend(KnockoutProperty, KnockoutObservable);

/**
 * @private
 */
KnockoutProperty.prototype.getValueForKnockout = function() {
	if (arguments.length > 0) {
		throw new Errors.InvalidParametersError('getValueForKnockout cannot write as this is not an EditableProperty');
	}

	presenter_knockout.dependencyDetection.registerDependency(this); // The caller only needs to be notified of changes if they did a "read" operation
	return this.getFormattedValue();
};

KnockoutProperty.prototype.peek = function() {
	return this.getFormattedValue();
};

/**
 * @private
 * @static
 */
KnockoutProperty.createArrayMethod = function(sMethod) {
	return function() {
		var pUnderlyingArray = this.getValue();
		var pNewArray = pUnderlyingArray.splice(0, pUnderlyingArray.length);
		Array.prototype[sMethod].apply(pNewArray, arguments);
		this.setValue(pNewArray);
	};
};

// TODO: we need some presenter CTs that interact with the view while the view is connected since otherwise this code is not being tested
KnockoutProperty.prototype.pop = KnockoutProperty.createArrayMethod('pop');
KnockoutProperty.prototype.push = KnockoutProperty.createArrayMethod('push');
KnockoutProperty.prototype.reverse = KnockoutProperty.createArrayMethod('reverse');
KnockoutProperty.prototype.shift = KnockoutProperty.createArrayMethod('shift');
KnockoutProperty.prototype.sort = KnockoutProperty.createArrayMethod('sort');
KnockoutProperty.prototype.splice = KnockoutProperty.createArrayMethod('splice');
KnockoutProperty.prototype.unshift = KnockoutProperty.createArrayMethod('unshift');

module.exports = KnockoutProperty;
