"use strict";
var br = require( 'br/Core' );
var TestingInterface = require('./TestingInterface');

var TestingClass = function() {
};

br.implement(TestingClass, TestingInterface);

TestingClass.prototype.foo = function() {
	return "bar";
};

TestingClass.prototype.implementMe = function() {
	return "I have been implemented";
};

module.exports = TestingClass;