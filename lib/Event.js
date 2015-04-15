"use strict";

var shams = require('./shams');
// Event ///////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates a base Event object.
 * @constructor
 * @memberOf Emitter
 * @class Event
 * @classdesc
 * Event provides a convenient base class for events.
 */
var Event = function() {};

/**
 * Extend provides a shorthand for creating subclasses of the class
 * whose constructor it is attached to.
 *
 * You can pass in an object that represents the things that
 * should be added to the prototype (in which case, the special
 * member 'constructor' if present will become the constructor),
 * or a function that represents the constructor whose prototype
 * should be modified, or nothing at all, in which case a new
 * constructor will be created that calls the superclass constructor.
 *
 * @memberOf Emitter.Event
 * @param {object|function} [properties] an object containing methods to be added to the prototype, or the constructor function, or nothing at all.
 * @returns {function} a constructor function for the newly created subclass.
 */
Event.extend = function inlineExtend(properties) {
	var superclass = this, subclassConstructor;
	if (typeof superclass !== 'function') { throw new TypeError("extend: Superclass must be a constructor function, was a " + typeof superclass); }

	if (typeof properties === 'function') {
		subclassConstructor = properties;
	} else if (properties != null && properties.hasOwnProperty('constructor')) {
		subclassConstructor = properties.constructor;
	} else {
		subclassConstructor = function() {
			superclass.apply(this, arguments);
		};
	}
	subclassConstructor.superclass = superclass;
	subclassConstructor.prototype = shams.create(superclass.prototype, {
		constructor: {
			enumerable: false, value: subclassConstructor
		}
	});
	
	//IE8 bug. https://developer.mozilla.org/en-US/docs/ECMAScript_DontEnum_attribute
	if (subclassConstructor.prototype.constructor !== subclassConstructor) {
		subclassConstructor.prototype.constructor = subclassConstructor;
	}

	if (typeof properties === 'object') {
		if (shams.getPrototypeOf(properties) !== Object.prototype) {
			throw new Error("extend: Can't extend something that already has a prototype chain.");
		}
		for (var instanceProperty in properties) {
			if (instanceProperty !== 'constructor' && properties.hasOwnProperty(instanceProperty)) {
				subclassConstructor.prototype[instanceProperty] = properties[instanceProperty];
			}
		}
	}
	for (var staticProperty in superclass) {
		if (superclass.hasOwnProperty(staticProperty)) {
			subclassConstructor[staticProperty] = superclass[staticProperty];
		}
	}

	return subclassConstructor;
};
/**
 * A simple toString is provided to aid in debugging.
 * @returns {string} a representation of all the fields on the object.
 */
Event.prototype.toString = function() {
	var result = [];
	for (var key in this) {
		// toString should show inherited properties too.
		//noinspection JSUnfilteredForInLoop
		if (typeof result[key] !== 'function') {
			//noinspection JSUnfilteredForInLoop
			result.push(key + ": " + this[key] + ",");
		}
	}
	return result.join(" ");
};

module.exports = Event;