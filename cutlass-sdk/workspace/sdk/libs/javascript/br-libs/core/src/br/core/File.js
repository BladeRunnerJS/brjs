var Errors = require('br/Errors');

var HTTP_OK = 200;

exports.readFileSync = function(url) {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', url, false);
	xhr.send();
	if (xhr.status === HTTP_OK) {
		return xhr.responseText;
	} else {
		throw new Errors.RequestFailedError("Unable to load file " + url + " (status " + xhr.status + ").");
	}
};
