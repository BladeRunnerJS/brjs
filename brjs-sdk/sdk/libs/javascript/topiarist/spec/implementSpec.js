/* global describe, beforeEach, it, expect, topiarist, err */
describe("topiarist.implement", function() {
	if (typeof topiarist === 'undefined') topiarist = require('../lib/topiarist.js');
	var err = topiarist._err;

	var Class, Interface;

	beforeEach(function() {
		jasmine.Clock.useMock();
		
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
	});

	it("passes classIsA() and classFulfills() tests even before the methods have been implemented.", function() {
		topiarist.implement(Class, Interface);

		expect(topiarist.classIsA(Class, Interface)).toBeTruthy();
		expect(topiarist.classFulfills(Class, Interface)).toBeTruthy();
	});

	// TODO: enable this test once <https://github.com/pivotal/jasmine/issues/667> is fixed
	xit("throws an exception if the interface methods aren't subsequently implemented.", function() {
		topiarist.implement(Class, Interface);

		expect(function() {
			jasmine.Clock.tick(1);
		}).toThrow(err.DOES_NOT_IMPLEMENT('Class', 'interfaceMethod', 'Interface'));
	});

	it("doesn't throw an exception if the interface methods are subsequently implemented.", function() {
		topiarist.implement(Class, Interface);

		Class.prototype.interfaceMethod = function() {};

		jasmine.Clock.tick(1);
	});
});