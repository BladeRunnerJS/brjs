"use strict";

var tmp = require('br/ServiceRegistry').getService('br.html-service');

var Testlib = {}

Testlib.helloWorldUtil = function() {
	console.log(tmp.getHTMLTemplate('testlib.tpl'));
}

module.exports = Testlib;
