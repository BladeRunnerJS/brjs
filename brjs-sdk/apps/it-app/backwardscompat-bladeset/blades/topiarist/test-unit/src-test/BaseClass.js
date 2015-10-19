"use strict";

var BaseClass = function() {
	this.value = 54321
};

BaseClass.prototype.methodA = function(name) {
	return "Hello " + name;
};

BaseClass.prototype.methodB = function(multiplier) {
	return this.value * multiplier;
};

module.exports = BaseClass;