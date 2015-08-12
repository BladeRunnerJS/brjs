'use strict';

var Utils = require('../Utils');

// If we're outputting to a node terminal, then use ANSI color codes to make the log output prettier.
var defaultFormatter = (global.process && global.process.stdout && Boolean(global.process.stdout.isTTY))
		? Utils.ansiFormatter : Utils.templateFormatter;

// Browsers provide different visual display for different log levels.
var CONSOLE_OUTPUT = {
	'fatal': 'error',
	'error': 'error',
	'warn': 'warn',
	'info': 'info',
	'debug': 'log'
};

/**
 * Create a new ConsoleLog destination.
 *
 * @param [filter] a function that determines whether or not to log specific log events.
 * @param [formatter] a function that determines how the log event is converted into a string.
 * @constructor
 */
function ConsoleLogDestination(filter, formatter) {
	this.filter = filter || Utils.allowAll;
	this.formatter = formatter || defaultFormatter;
}

ConsoleLogDestination.prototype.onLog = function(component, level, data, time) {
	if (this.filter(time, component, level, data)) {
		this.output(level, this.formatter(time, component, level, data));
	}
};

ConsoleLogDestination.prototype.output = function(level, message) {
	console[CONSOLE_OUTPUT[level]].call(console, message);
};

module.exports = ConsoleLogDestination;
