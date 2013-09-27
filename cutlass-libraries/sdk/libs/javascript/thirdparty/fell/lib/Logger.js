"use strict";

var Levels = require('./Levels');

function NOOP() {};

/**
 * Creates a Logger class specific to a component.
 *
 * @private
 * @param emitter
 * @param component
 * @constructor
 */
function Logger(emitter, component) {
	this.component = component;
	this.emitter = emitter;
}

// creates a method for each of the log levels.
Levels.forEach(function(level) {
	Logger.prototype[level] = function() {
		this.emitter.trigger('log', Date.now(), this.component, level, arguments);
	};
});

/**
 * Creates instance methods pointing to the NOOP function for
 * logging methods that should have no effect.
 *
 * @param level
 * @private
 */
Logger.prototype._setLevel = function(level) {
	var dontLogThisLevel = true;
	for (var i = Levels.length - 1; i >= 0; --i ) {
		if (Levels[i] === level) {
			dontLogThisLevel = false;
		}
		if (dontLogThisLevel) {
			this[Levels[i]] = NOOP;
		} else if (this.hasOwnProperty(Levels[i])) {
			delete this[Levels[i]];
		}
	}
};

module.exports = Logger;