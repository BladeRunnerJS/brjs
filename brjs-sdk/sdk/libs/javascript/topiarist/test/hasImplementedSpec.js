'use strict';

var topiarist = require('..');
var err = require('./errorFuncs');
var expect = require('expectations');

describe('topiarist.hasImplemented', function() {
	var Class, Interface;

	beforeEach(function() {
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
		Interface.prototype.anotherInterfaceMethod = function() {};
	});

	it('throws an error if the class is not a function.', function() {
		Class = 23;
		expect(function() {
			topiarist.hasImplemented(Class, Interface);
		}).toThrow(err._NOT_CONSTRUCTOR('Class', 'hasImplemented', 'number'));
	});

	it('throws an error if the interface is not a function.', function() {
		Interface = 23;
		expect(function() {
			topiarist.hasImplemented(Class, Interface);
		}).toThrow(err._NOT_CONSTRUCTOR('Protocol', 'hasImplemented', 'number'));
	});

	it('throws an error if the class doesn\'t implement all the methods specified by the interface.', function() {
		expect(function() {
			topiarist.hasImplemented(Class, Interface);
		}).toThrow( err._DOES_NOT_IMPLEMENT('Class', ['interfaceMethod', 'anotherInterfaceMethod'].join('\', \''), 'Interface') );
	});

	it('throws an error if the class doesn\'t implement all the \'class\' methods specified by the interface.', function() {
		Class.prototype.interfaceMethod = function() {};
		Class.prototype.anotherInterfaceMethod = function() {};

		Interface.staticMethod = function() {};

		expect(function() {
			topiarist.hasImplemented(Class, Interface);
		}).toThrow(err._DOES_NOT_IMPLEMENT('Class', 'staticMethod (class method)', 'Interface'));
	});

	it('does not throw an error if the class implements all the methods specified by the interface.', function() {
		Class.prototype.interfaceMethod = function() {};
		Class.prototype.anotherInterfaceMethod = function() {};

		topiarist.hasImplemented(Class, Interface);
		expect(true).toBe(true);
	});

	it('does not throw an error if the class doesn\'t define static \'class\' properties specified by the interface.', function() {
		Class.prototype.interfaceMethod = function() {};
		Class.prototype.anotherInterfaceMethod = function() {};

		[1, NaN, 'a', null, true].forEach(function(prop, idx) {
			Interface['staticProperty_' + idx] = prop;
		});

		topiarist.hasImplemented(Class, Interface);
		expect(true).toBe(true);
	});

	it('does not throw an error if the class inherits (using topiarist.extend) methods required by the interface.', function() {
		Class.prototype.interfaceMethod = function() {};
		Class.prototype.anotherInterfaceMethod = function() {};

		function ChildClass() {
		}
		topiarist.extend(ChildClass, Class);

		topiarist.hasImplemented(ChildClass, Interface);
		expect(true).toBe(true);
	});

});
