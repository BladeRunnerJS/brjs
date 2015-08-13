'use strict';

var Log = require('./Log');
var LogStore = require('./destination/LogStore');
var ConsoleLog = require('./destination/ConsoleLog');

var fell = new Log();
fell.destination = {
	LogStore: LogStore,
	ConsoleLog: ConsoleLog
};

module.exports = fell;
