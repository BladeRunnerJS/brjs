"use strict";
var br = require( 'br/Core' );
var TestingInterface = require('./TestingInterface');
var Testlib3p = require('testlib3p');

var TestingClass = function() {
};

br.implement(TestingClass, TestingInterface);

TestingClass.prototype.foo = function() {
	return "bar";
};

TestingClass.prototype.use3rdPartyLib = function() {
	return Testlib3p.helloWorldUtil();
};

TestingClass.prototype.implementMe = function() {
	return "I have been implemented";
};

module.exports = TestingClass;