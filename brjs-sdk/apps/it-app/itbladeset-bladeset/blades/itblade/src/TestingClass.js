"use strict";
var br = require( 'br/Core' );
var TestingInterface = require('./TestingInterface');

var TestingClass = function() {
};

br.implement(TestingClass, TestingInterface);


TestingClass.prototype.implementMe = function(message) {
	return "Hello from an aliased class";
};

module.exports = TestingClass;