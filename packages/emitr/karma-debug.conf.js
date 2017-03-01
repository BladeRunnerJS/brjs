var baseConfigurator = require('./karma.conf.js');

module.exports = function(config) {
	baseConfigurator(config);

	config.set({
		browsers: ['Chrome'],
		singleRun: false,
		autoWatch: true
	});
};
