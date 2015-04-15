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