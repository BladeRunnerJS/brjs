var baseConfigurator = require('./karma.conf.js');

module.exports = function(config) {
	baseConfigurator(config);

	var customLaunchers = {
		SauceLabs_Chrome: {
			base: 'SauceLabs',
			browserName: 'chrome',
			platform: 'Windows 7',
			version: '43'
		},
		SauceLabs_IE8_XP: {
			base: 'SauceLabs',
			browserName: 'internet explorer',
			platform: 'Windows xp',
			version: '8'
		},
		SauceLabs_IE8_Win7: {
			base: 'SauceLabs',
			browserName: 'internet explorer',
			platform: 'Windows 7',
			version: '8'
		},
		SauceLabs_IE9: {
			base: 'SauceLabs',
			browserName: 'internet explorer',
			platform: 'Windows 7',
			version: '9'
		},
		SauceLabs_IE10: {
			base: 'SauceLabs',
			browserName: 'internet explorer',
			platform: 'Windows 8',
			version: '10'
		},
		SauceLabs_IE11: {
			base: 'SauceLabs',
			browserName: 'internet explorer',
			platform: 'Windows 8.1',
			version: '11'
		}
	};

	config.set({
		sauceLabs: {
			testName: 'fell tests',
			username: 'bladerunnerjs',
			accessKey: 'c4d7ffe8-6b82-4f8c-9dbf-550bdfe18126',
			connectOptions: {
        logfile: 'sauce_connect.log'
      }
		},
		reporters: ['progress', 'saucelabs'],
		customLaunchers: customLaunchers,
		browsers: Object.keys(customLaunchers)
	});
};
