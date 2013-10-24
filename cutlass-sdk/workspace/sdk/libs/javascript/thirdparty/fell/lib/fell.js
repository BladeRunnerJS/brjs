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