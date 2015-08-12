'use strict';

var ERROR_MESSAGES = require('../src/messages');
var msg = require('../src/msg');
var slice = Array.prototype.slice;

function errorFuncs() {
	var err = {};
	var getErr = function(key) {
		return function() {
			var message = ERROR_MESSAGES[key];
			var args = slice.call(arguments);
			args.unshift(message);
			var result = msg.apply(null, args);
			if (result === null) {
				throw new Error('No such error message ' + key);
			}
			return result;
		};
	};
	for (var key in ERROR_MESSAGES) {
		err['_' + key] = getErr(key);
	}

	return err;
}

module.exports = errorFuncs();
