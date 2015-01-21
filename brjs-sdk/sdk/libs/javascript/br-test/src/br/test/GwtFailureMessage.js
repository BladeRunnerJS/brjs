'use strict';

/**
 * @module br/test/GwtFailureMessage
 */

/**
 * @private
 * @class
 * @alias module:br/test/GwtFailureMessage
 */
function GwtFailureMessage() {
	this._errorMsg = '';
	this._statck = '';
}

GwtFailureMessage.prototype.setMessage = function(message) {
	this._errorMsg = message;
};

GwtFailureMessage.prototype.getMessage = function() {
	return this._errorMsg;
};

GwtFailureMessage.prototype.setStack = function(stack) {
	this._statck = stack;
};

GwtFailureMessage.prototype.getStack = function() {
	return this._statck;
};

module.exports = GwtFailureMessage;
