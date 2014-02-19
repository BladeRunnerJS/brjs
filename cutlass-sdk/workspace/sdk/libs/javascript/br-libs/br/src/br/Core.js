var topiarist = require('topiarist');
topiarist.exportTo(exports);

//TODO: find a better solution for this
exports.implementImmediately = exports.implement; 

exports.implement = function(implementor, interface) {
	// We do this on a timeout so you can implement the methods later.
	var br = topiarist;
	var error = new Error();
	setTimeout(function() {
		try {
			br.implement(implementor, interface);
		} catch (e) {
			error.message = e.message;
			error.name = e.name;
			throw error;
		}
	}, 0);
};

exports.thirdparty = function(library){};

require("br/I18n").initialise(window._brjsI18nProperties || []);
