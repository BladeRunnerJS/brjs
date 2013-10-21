"use strict";

var Emitter = require('Emitter');
var Logger = require('./Logger');
var Levels = require('./Levels');

var DEFAULT_COMPONENT = "[default]";

function Log() {
	this.loggers = null;
	this.config = null;
	this.defaultLevel = null;
	this.rootLogger = null;
	this.clear();
}
Emitter.mixInto(Log);

Log.prototype.DEFAULT_COMPONENT = DEFAULT_COMPONENT;

Log.prototype.getLogger = function(component) {
	if (arguments.length === 0) { component = DEFAULT_COMPONENT; }
	if (this.loggers[component] !== undefined) {
		return this.loggers[component];
	}
	var logger = new Logger(this, component);
	var level = bestLevelMatch(this.config, component, this.defaultLevel);
	logger._setLevel(level);
	this.loggers[component] = logger;

	return logger;
};

Log.prototype.configure = function(defaultLevel, config, destinations) {
	if (arguments.length === 1 && typeof defaultLevel === 'object') {
		config = arguments[0];
		defaultLevel = "info";
	}
	config = config || {};
	this.config = config;
	this.defaultLevel = defaultLevel;
	setLoggerLevels(defaultLevel, config, this.loggers);
	this.rootLogger = this.getLogger();

	this.off();
	destinations = destinations || (typeof console !== 'undefined' ? [new require('./destination/ConsoleLog')()] : []);
	for (var i = 0; i < destinations.length; ++i) {
		this.addDestination(destinations[i]);
	}
};

Log.prototype.changeLevel = function(component, level) {
	if (arguments.length === 1) {
		this.defaultLevel = arguments[0];
	} else {
		this.config[component] = level;
	}
	setLoggerLevels(this.defaultLevel, this.config, this.loggers);
	this.rootLogger = this.getLogger();
};

Log.prototype.addDestination = function(logDestination, context) {
	if (context === undefined && typeof logDestination !== 'function') {
		context = logDestination;
		logDestination = context.onLog;
	}
	this.on('log', logDestination, context);
};

Log.prototype.removeDestination = function(logDestination, context) {
	if (arguments.length === 1 && typeof logDestination !== 'function') {
		context = logDestination;
		logDestination = context.onLog;
	}
	this.off('log', logDestination, context);
};

Log.prototype.clear = function() {
	this.loggers = {};
	this.configure("info");
};

Log.prototype.Levels = Levels;

Levels.forEach(function(level) {
	Log.prototype[level.toUpperCase()] = level;

	// Convenience methods to log to the root logger.
	Log.prototype[level] = function() {
		this.rootLogger[level].apply(this.rootLogger, arguments);
	};
});

function setLoggerLevels(defaultLevel, config, loggers) {
	for (var loggerId in loggers) {
		var level = bestLevelMatch(config, loggerId, defaultLevel);
		loggers[loggerId]._setLevel(level);
	}
}

function bestLevelMatch(config, key, otherwise) {
	var candidates = Object.keys(config)
			.filter(function(a) {
				return a === key || key.substring(0, a.length + 1) === (a + ".");
			}).sort(function(a, b) {
				return b.length - a.length;
			});
	return candidates[0] ? config[candidates[0]] : otherwise;
}

module.exports = new Log();