(function(name, definition) {
	if (typeof define === "function") {
		// my own definition function.
		define(name, definition);
	} else if (typeof module !== 'undefined' && typeof module.exports !== 'undefined') {
		// node style commonJS.
		module.exports = definition();
	} else {
		// setting a global, as in e.g. a browser.
		this[name] = definition();
	}
})