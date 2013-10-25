define("br", function(require, module, exports) {	

	var topiary = require('topiary');
	topiary.exportTo(exports);

	exports.implement = function(implementor, interface) {
		// We do this on a timeout so you can implement the methods later.
		var br = topiary;
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

	exports.provide = exports.inherit;

	exports.thirdparty = function(library){};
	require("br/i18n").initialise(window.pUnprocessedI18NMessages || []);
});