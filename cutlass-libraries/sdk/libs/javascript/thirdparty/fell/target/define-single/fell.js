// fell v0.0.1 packaged for the browser.
// 2013-09-25T10:35:05.000Z

// destination\ConsoleLog.js (modified 10:10:17)
define('fell/lib/destination/ConsoleLog', function(require, exports, module) {
	"use strict";
	
	var Utils = require('../Utils');
	
	// If we're outputting to a node terminal, then use ANSI color codes to make the log output prettier.
	var global = Function("return this;")();
	var defaultFormatter = (global.process && global.process.stdout && Boolean(global.process.stdout.isTTY))
			? Utils.ansiFormatter : Utils.templateFormatter;
	
	// Browsers provide different visual display for different log levels.
	var CONSOLE_OUTPUT = {
		"fatal": console.error,
		"error": console.error,
		"warn": console.warn,
		"info": console.info,
		"debug": console.debug || console.log
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
	};
	
	ConsoleLogDestination.prototype.onLog = function(time, component, level, data) {
		if (this.filter(time, component, level, data)) {
			this.output(level, this.formatter(time, component, level, data));
		}
	};
	
	ConsoleLogDestination.prototype.output = function(level, message) {
		CONSOLE_OUTPUT[level].call(console, message);
	};
	
	module.exports = ConsoleLogDestination;
});

// destination\LogStore.js (modified 10:10:17)
define('fell/lib/destination/LogStore', function(require, exports, module) {
	"use strict";
	
	var Utils = require('../Utils');
	var RingBuffer = require('../RingBuffer');
	
	/**
	 * Records log events in an array or ring buffer.
	 *
	 * @param maxRecords the size of the ring buffer.
	 * @constructor
	 */
	function LogStore(maxRecords) {
		this.logRecords = maxRecords ? new RingBuffer(maxRecords) : [];
	}
	
	LogStore.prototype.onLog = function(time, component, level, data) {
		this.logRecords.push({
			time: time,
			component: component,
			level: level,
			data: data,
			toString: logRecordToString
		});
	};
	
	/**
	 * @returns {Array} all messages currently stored.
	 */
	LogStore.prototype.allMessages = function() {
		var result = [];
		this.logRecords.forEach(function(record) {
			result.push(record);
		});
		return result;
	};
	
	LogStore.prototype.toString = function() {
		return "Stored Log Messages:\n\t" + this.allMessages().join("\n\t");
	};
	
	function logRecordToString() {
		return Utils.templateFormatter(this.time, this.component, this.level, this.data);
	}
	
	// JSHamcrest integration. /////////////////////////////////////////////////////////////////////////
	
	var global = Function("return this")();
	if (global.both && global.hasMember && global.truth && global.allOf && global.anyOf) {
		LogStore.containsAll = function() {
			var items = [];
			for (var i = 0; i < arguments.length; i++) {
				items.push(LogStore.contains(arguments[i]));
			}
			return allOf(items);
		};
		LogStore.containsAny = function() {
			var items = [];
			for (var i = 0; i < arguments.length; i++) {
				items.push(LogStore.contains(arguments[i]));
			}
			return anyOf(items);
		};
		LogStore.contains = function(matcher) {
			var baseMatcher = truth();
			baseMatcher.matches = function(actual) {
				// Should be a LogStore
				if (!(actual instanceof LogStore)) {
					return false;
				}
	
				for (var i = 0; i < actual.logRecords.length; i++) {
					if (matcher.matches(actual.logRecords[i])) {
						return true;
					}
				}
				return false;
			};
			baseMatcher.describeTo = function(description) {
				description.append('there has been a log event ').appendDescriptionOf(matcher);
			};
			return baseMatcher;
		};
		LogStore.event = function logEvent(level, component, data, time) {
			var matcher = both(hasMember('level', level));
			if (arguments.length > 1) {
				matcher = matcher.and(hasMember('component', component));
			}
			if (arguments.length > 2) {
				matcher = matcher.and(hasMember('data', data));
			}
			if (arguments.length > 3) {
				matcher = matcher.and(hasMember('time', time));
			}
			return matcher;
		};
	}
	
	module.exports = LogStore;
});

// fell.js (modified 11:34:00)
define('fell/lib/fell', function(require, exports, module) {
	module.exports = {
		Log: require('./Log'),
		RingBuffer: require('./RingBuffer'),
		Utils: require('./Utils'),
		destination: {
			LogStore: require('./destination/LogStore')
		}
	};
	
	if (typeof console !== "undefined") {
		module.exports.destination.ConsoleLog = require('./destination/ConsoleLog');
	}
});

// Levels.js (modified 10:10:17)
define('fell/lib/Levels', function(require, exports, module) {
	module.exports = ["fatal", "error", "warn", "info", "debug"];
	
});

// Log.js (modified 11:35:05)
define('fell/lib/Log', function(require, exports, module) {
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
});

// Logger.js (modified 10:10:17)
define('fell/lib/Logger', function(require, exports, module) {
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
});

// RingBuffer.js (modified 15:30:56)
define('fell/lib/RingBuffer', function(require, exports, module) {
	"use strict";
	
	var Utils = require('./Utils');
	
	/**
	 * Creates a RingBuffer, which allows a maximum number of items to be stored.
	 *
	 * @param size {Number} The maximum size of this buffer. This must be an integer larger than 0.
	 * @constructor
	 */
	function RingBuffer(size) {
		this._checkSize(size);
		this.maxSize = size;
		this.clear();
	}
	
	var ERRORS = {
		"parameter not function": "Parameter must be a function, was a {0}.",
		"size less than 1": "RingBuffer cannot be created with a size less than 1 (was {0}).",
		"size not integer": "RingBuffer cannot be created with a non integer size (was {0})."
	};
	
	/**
	 * Clears all items from this RingBuffer and resets it.
	 */
	RingBuffer.prototype.clear = function () {
		this.buffer = new Array(this.maxSize);
		this.next = 0;
		this.isFull = false;
	};
	
	/**
	 * @return the item most recently added into this RingBuffer, or null if no items have been added.
	 */
	RingBuffer.prototype.newest = function () {
		var newest = null;
		var index = (this.next + this.maxSize - 1) % this.maxSize;
		if (this.isFull || index < this.next) {
			newest = this.buffer[index];
		}
		return newest;
	};
	
	/**
	 * @return the oldest item that is still in this RingBuffer or null if no items have been added.
	 */
	RingBuffer.prototype.oldest = function () {
		var oldest = null;
		if (this.isFull) {
			oldest = this.buffer[this.next];
		}
		else if (this.next > 0) {
			oldest = this.buffer[0];
		}
		return oldest;
	};
	
	/**
	 * @param n {Number} the index of the item to be returned.
	 * @return the nth oldest item that is stored or undefined if the index is larger than the number of
	 *          items stored.
	 */
	RingBuffer.prototype.get = function (n) {
		if (n >= this.maxSize) return undefined;
		if (this.isFull) {
			return this.buffer[(this.next + n) % this.maxSize];
		}
		return this.buffer[n];
	};
	
	/**
	 * Adds an item into the end of this RingBuffer, possibly pushing an item out of the buffer in the
	 * process.
	 *
	 * @param {Object} object an item to add into this buffer.
	 * @return the item that was pushed out of the buffer or null if the buffer is not full. Note, this
	 *          is different to what an array.push returns.
	 *
	 */
	RingBuffer.prototype.push = function(object) {
		var ousted = null;
		if (this.isFull) {
			ousted = this.oldest();
		}
	
		this._changeWindow(object);
	
		return ousted;
	};
	
	/**
	 * Changes the size of a RingBuffer.
	 *
	 * This operation should not be expected to be performant; do not do it often.
	 *
	 * If the new size is smaller than the number of items in this window, this operation may cause some
	 * objects to be pushed out of the window.
	 *
	 * @param {Number} newSize the new size the window should take up. Must be a positive integer.
	 */
	RingBuffer.prototype.setSize = function(newSize) {
		this._checkSize(newSize);
	
		if (this.maxSize == newSize) {
			return;
		}
		var tmpBuffer = new RingBuffer(newSize);
		this.forEach(tmpBuffer.push.bind(tmpBuffer));
	
		this.maxSize = tmpBuffer.maxSize;
		this.buffer = tmpBuffer.buffer;
		this.next = tmpBuffer.next;
		this.isFull = tmpBuffer.isFull;
	};
	
	/**
	 * Iterates over each of the items in this buffer from oldest to newest.
	 *
	 * @param {Function} func a function that will be called with each item.
	 */
	RingBuffer.prototype.forEach = function (func) {
		if (typeof func != 'function') {
			throw new TypeError(errorMessage("parameter not function", typeof func));
		}
	
		for (var i = 0, end = this.getSize(); i < end; ++i) {
			var bufferIndex = this.isFull ? (this.next + i) % this.maxSize : i;
			func(this.buffer[bufferIndex]);
		}
	};
	
	/**
	 * @returns the number of items in this buffer.
	 */
	RingBuffer.prototype.getSize = function () {
		return this.isFull ? this.maxSize : this.next;
	};
	
	/**
	 * @return {String} Returns a string representation of this RingBuffer.  This is intended to be
	 *          human readable for debugging but may change.
	 */
	RingBuffer.prototype.toString = function () {
		var result = [ "{sidingwindow start=" ];
		result.push(this.next);
		result.push(" values=[");
		result.push(this.buffer.join(","));
		result.push("] }");
	
		return result.join("");
	};
	
	RingBuffer.prototype._checkSize = function(size) {
		if (size !== (size|0)) {
			throw new Error(errorMessage("size not integer", size));
		}
		if (size < 1) {
			throw new Error(errorMessage("size less than 1", size));
		}
	};
	
	RingBuffer.prototype._changeWindow = function(incoming) {
		this.buffer[this.next] = incoming;
		this.next = (this.next + 1) % this.maxSize;
		if (this.next == 0) {
			this.isFull = true;
		}
	};
	
	function errorMessage() {
		var args = Array.prototype.slice.call(arguments);
		args[0] = ERRORS[args[0]];
		return Utils.interpolate.apply(Utils, args);
	}
	RingBuffer.errorMessage = errorMessage;
	
	module.exports = RingBuffer;
});

// Utils.js (modified 11:02:05)
define('fell/lib/Utils', function(require, exports, module) {
	"use strict";
	
	var DAY_NAMES = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	var MONTH_NAMES = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
	
	/**
	 * Formats a date according to a provided pattern.  The pattern is intended to be compatible with
	 * the Java SimpleDateFormatter.
	 *
	 * @param pattern
	 * @param date
	 * @returns {string}
	 */
	function format(pattern, date) {
		if (date == null) { date = new Date(); }
		var dayNo = date.getDay();
		var dateNo = date.getDate();
		var month = date.getMonth();
		var hour = date.getHours();
		var minute = date.getMinutes();
		var sec = date.getSeconds();
		var millis = date.getMilliseconds();
		var fullYear = date.getFullYear();
	
		return pattern
				.replace(/HH/g, padBefore(hour, 2, "0"))
				.replace(/H/g, hour)
				.replace(/mm/g, padBefore(minute, 2, "0"))
				.replace(/m/g, minute)
				.replace(/ss/g, padBefore(sec, 2, "0"))
				.replace(/s/g, sec)
				.replace(/SSS/g, padBefore(millis, 3, "0"))
				.replace(/S/g, millis)
				.replace(/yyyy/g, fullYear)
				.replace(/yy/g, String(fullYear).substring(2))
				.replace(/dd/g, padBefore(dateNo, 2, "0"))
				.replace(/d/g, dateNo)
				.replace(/MMMM/g, MONTH_NAMES[month])
				.replace(/MMM/g, MONTH_NAMES[month].substring(0, 3))
				.replace(/MM/g, padBefore(month + 1, 2, "0"))
				.replace(/M/g, month + 1)
				.replace(/EEEE/g, DAY_NAMES[dayNo])
				.replace(/EEE/g, DAY_NAMES[dayNo].substring(0, 3));
	}
	
	function padAfter(val, length, paddingCharacter) {
		val = String(val);
		if (val.length >= length) return val;
		var result = val + (new Array(length).join(paddingCharacter) + paddingCharacter);
		return result.substring(0, length);
	}
	
	function padBefore(val, length, paddingCharacter) {
		val = String(val);
		if (val.length >= length) return val;
		var result = (new Array(length).join(paddingCharacter) + paddingCharacter) + val;
		return result.substring(result.length - length);
	}
	
	/**
	 * Does string interpolation.  Replaces {n} in the first argument with the (n + 1)th argument to
	 * this function.
	 *
	 * @param template
	 * @returns {*}
	 */
	function interpolate(template) {
		if (template === null || template === undefined) {
			return template;
		}
		var args = arguments;
		var message = String(template);
		message = message.replace(/\{(\d+)\}/g, function(_, argNumber) {
			argNumber = Number(argNumber);
			return String(args[argNumber + 1]);
		});
		return message;
	}
	
	/**
	 * A default formatter to convert log events to strings.
	 * @param time
	 * @param component
	 * @param level
	 * @param data
	 * @returns {string}
	 */
	function templateFormatter(time, component, level, data) {
		var date = new Date(time);
		return format("yyyy-MM-dd HH:mm:ss.SSS", date)
				+ " ["
				+ padAfter(level, 5, " ")
				+ "] ["
				+ padAfter(component, 18, " ")
				+ "] : "
				+ interpolate.apply(null, data);
	}
	
	// see http://en.wikipedia.org/wiki/ANSI_escape_code
	// this is to make the output nicer in the node console.
	var colors = {
		black: 0, red: 1, green: 2, yellow: 3, blue: 4, magenta: 5, cyan: 6, white: 7
	};
	
	function style(str, style) {
		var startCodes = [];
		var endCodes = [];
		for (var key in style) {
			if (key === 'color' || key === 'background') {
				var base = (key === 'color' ? 30 : 40);
				var styleParts = style[key].split(" ");
				var color = styleParts[styleParts.length - 1];
				var isBright = false;
				if (styleParts[0] === 'bright') {
					isBright = true;
				}
				startCodes.push("\x1B[" + (base + colors[color]) + (isBright ? ";1m" : "m"));
				endCodes.push("\x1B[" + (base + 9) + (isBright ? ";22m" : "m"))
			}
			// maybe add some of the other ansi styles in future.
		}
		return startCodes.join("") + str + endCodes.reverse().join("");
	}
	
	var LEVEL_STYLES = {
		"fatal": {color: "bright white", background: "bright red"},
		"error": {color: "bright red"},
		"warn": {color: "bright yellow"},
		"info": {},
		"debug": {color: "green"}
	};

	/**
	 * A formatter that converts log events to ansi colored strings.
	 * @param time
	 * @param component
	 * @param level
	 * @param data
	 * @returns {string}
	 */
	function ansiFormatter(time, component, level, data) {
		var date = new Date(time);
		return style(format("yyyy-MM-dd HH:mm:ss.SSS", date)
					+ " [" + padAfter(level, 5, " ")	+ "] ["
					+ padAfter(component, 18, " ") + "]", LEVEL_STYLES[level])
				+ " : " + interpolate.apply(null, data);
	}
	
	/**
	 * A filter that always returns true.
	 * @param time
	 * @param component
	 * @param level
	 * @param data
	 * @returns {boolean}
	 */
	function allowAll(time, component, level, data) {
		return true;
	}
	
	module.exports = {
		format: format,
		interpolate: interpolate,
		templateFormatter: templateFormatter,
		ansiFormatter: ansiFormatter,
		allowAll: allowAll
	};
});

define('fell', function(require, exports, module) { module.exports = require('fell/lib/fell');});