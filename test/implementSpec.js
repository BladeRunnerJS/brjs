'use strict';

var topiarist = require('..');
var err = require('./errorFuncs');
var expect = require('expectations');
var sinon = require('sinon');

describe('topiarist.implement', function() {
	var Class, Interface;
	var clock;

	beforeEach(function() {
		clock = sinon.useFakeTimers();

		// This horrible structure is to avoid a bug in IE8 where the obvious way of writing this
		// would have created *locals* ChildClass and ParentClass and not modified the values from
		// the above scope.
		/*eslint no-shadow:0*/
		Class = (function() {
			return function Class() {};
		})();
		Interface = (function() {
			return function Interface() {};
		})();
		Interface.prototype.interfaceMethod = function() {};
	});

	afterEach(function () {
		clock.restore();
	});

	it('passes classIsA() and classFulfills() tests even before the methods have been implemented.', function() {
		topiarist.implement(Class, Interface);

		expect(topiarist.classIsA(Class, Interface)).toBeTruthy();
		expect(topiarist.classFulfills(Class, Interface)).toBeTruthy();
	});

	it('throws an exception if the interface methods aren\'t subsequently implemented.', function() {
		topiarist.implement(Class, Interface);

		expect(function() {
			clock.tick(1);
		}).toThrow(err._DOES_NOT_IMPLEMENT('Class', 'interfaceMethod', 'Interface'));
	});

	it('doesn\'t throw an exception if the interface methods are subsequently implemented.', function() {
		topiarist.implement(Class, Interface);

		Class.prototype.interfaceMethod = function() {};

		clock.tick(1);
	});
});
