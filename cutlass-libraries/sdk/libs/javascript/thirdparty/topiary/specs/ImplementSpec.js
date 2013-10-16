/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.implement", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var Class, Interface;

	beforeEach(function() {
		// This horrible structure is to avoid a bug in IE8 where the obvious way of writing this
		// would have created *locals* ChildClass and ParentClass and not modified the values from
		// the above scope.
		Class = (function() {
			return function Class() {};
		})();
		Interface = (function() {
			return function Interface() {};
		})();
		Interface.prototype.interfaceMethod = function() {};
		Interface.prototype.anotherInterfaceMethod = function() {};
	});

	it("throws an error if the class is not a function.", function() {
		Class = 23;
		expect(function() {
			topiary.implement(Class, Interface);
		}).toThrow(err.NOT_CONSTRUCTOR("Class", "implement", "number"));
	});

	it("throws an error if the interface is not a function.", function() {
		Interface = 23;
		expect(function() {
			topiary.implement(Class, Interface);
		}).toThrow(err.NOT_CONSTRUCTOR('Protocol', 'implement', "number"));
	});

	it("throws an error if the class doesn't implement all the methods specified by the interface.", function() {
		expect(function() {
			topiary.implement(Class, Interface);
		}).toThrow( err.DOES_NOT_IMPLEMENT('Class', ['interfaceMethod', 'anotherInterfaceMethod'].join("', '"), 'Interface') );
	});

	it("does not throw an error if the class implements all the methods specified by the interface.", function() {
		Class.prototype.interfaceMethod = function() {};
		Class.prototype.anotherInterfaceMethod = function() {};

		topiary.implement(Class, Interface);
		expect(true).toBe(true);
	});

	it("does not throw an error if the class inherits (using topiary.extend) methods required by the interface.", function() {
		Class.prototype.interfaceMethod = function() {};
		Class.prototype.anotherInterfaceMethod = function() {};

		function ChildClass() {}
		topiary.extend(ChildClass, Class);

		topiary.implement(ChildClass, Interface);
		expect(true).toBe(true);
	});

});