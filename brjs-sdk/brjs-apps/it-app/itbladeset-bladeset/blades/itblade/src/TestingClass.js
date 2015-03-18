"use strict";
var br = require( 'br/Core' );
var TestingInterface = require('./TestingInterface');

var TestingClass = function() {
};

br.implement(TestingClass, TestingInterface);


TestingClass.prototype.implementMe = function(message) {
	return "TestingClass: I have implemented TestingInterface";
};

module.exports = TestingClass;