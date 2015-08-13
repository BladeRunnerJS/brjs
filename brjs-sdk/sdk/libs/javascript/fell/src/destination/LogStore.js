'use strict';

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

function logRecordToString() {
	return Utils.templateFormatter(this.time, this.component, this.level, this.data);
}

LogStore.prototype.onLog = function(component, level, data, time) {
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
	return 'Stored Log Messages:\n\t' + this.allMessages().join('\n\t');
};

module.exports = LogStore;
