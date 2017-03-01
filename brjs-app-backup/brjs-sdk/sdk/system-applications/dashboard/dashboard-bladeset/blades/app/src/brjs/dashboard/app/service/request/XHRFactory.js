'use strict';

function XHRFactory() {
}

XHRFactory.prototype.getRequestObject = function() {
	return new XMLHttpRequest();
};

module.exports = XHRFactory;
