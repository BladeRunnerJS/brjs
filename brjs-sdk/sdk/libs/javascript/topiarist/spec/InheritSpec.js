/* global describe, beforeEach, it, expect, topiarist, err */
describe("topiarist.inherit", function() {
	if (typeof topiarist === 'undefined') topiarist = require('../lib/topiarist.js');
	var err = topiarist._err;

	var Class, Parent, MultiParent;

	beforeEach(function() {
		Class = function() {};
		Parent = function() {};
		topiarist.extend(Class, Parent);
		MultiParent = function() {};
	});

	it("throws an error if the target is not a constructor.", function() {
		expect( function() {
			topiarist.inherit(23, MultiParent);
		}).toThrow(err.NOT_CONSTRUCTOR('Target', 'inherit', 'number'));
	});

	it("throws an error if the inherited parent is null.", function() {
		expect( function() {
			topiarist.inherit(Class, null);
		}).toThrow(err.WRONG_TYPE('Parent', 'inherit', 'non-null object or function', 'null'));
	});

	it("copies inherited functionality across to the class.", function() {
		var inheritedFuncRan = false;
		MultiParent.prototype.inheritedFunc = function() {
			inheritedFuncRan = true;
		};
		topiarist.inherit(Class, MultiParent);

		var instance = new Class();
		instance.inheritedFunc();

		expect(inheritedFuncRan).toBe(true);
	});

	it("allows inherited functionality to affect instance state.", function() {
		MultiParent.prototype.inheritedFunc = function() {
			this.state = "modified by inherited function";
		};
		topiarist.inherit(Class, MultiParent);

		var instance = new Class();
		instance.inheritedFunc();

		expect(instance.state).toBe("modified by inherited function");
	});

	it("throws an error if an inherited parent has functions that clash with the target class.", function() {
		MultiParent.prototype.thingy = function() {};
		MultiParent.prototype.clashingThingy = function() {};

		Class.prototype.clashingThingy = function() {};

		expect(function() {
			topiarist.inherit(Class, MultiParent);
		}).toThrow(err.ALREADY_PRESENT('clashingThingy', 'parent', 'target'));
	});

	it("does not copy any functionality if it can't copy everything.", function() {
		MultiParent.prototype.thingy = function() {};
		MultiParent.prototype.clashingThingy = function() {};

		Class.prototype.clashingThingy = function() {};

		try {
			topiarist.inherit(Class, MultiParent);
		} catch (e) {}

		expect(Class.prototype.thingy).toBeUndefined();
	});

	it("copies functionality from an inherited classes parents.", function() {
		function MultiOverMixin() {}
		MultiOverMixin.prototype.mixedIn = function() {};

		function MultiOverParent() {}
		MultiOverParent.prototype.uber = function() {};
		topiarist.extend(MultiParent, MultiOverParent);
		topiarist.mixin(MultiParent, MultiOverMixin);

		topiarist.inherit(Class, MultiParent);

		var instance = new Class();

		expect(typeof instance.mixedIn).toBe('function');
		expect(typeof instance.uber).toBe('function');
	});

	it("does not throw an error if we attempt to inherit something that has already been inherited higher up the tree, even if that has then been modified.", function() {
		function MultiOverMixin() {}
		MultiOverMixin.prototype.mixedIn = function() {};

		function MultiOverParent() {}
		MultiOverParent.prototype.uber = function() {};
		topiarist.extend(MultiParent, MultiOverParent);
		topiarist.mixin(MultiParent, MultiOverMixin);

		// override uber
		MultiParent.prototype.uber = function() {};

		topiarist.inherit(Class, MultiParent);

		topiarist.inherit(Class, MultiOverParent);

		var instance = new Class();

		expect(instance.uber).toBe(MultiParent.prototype.uber );
	});

	describe("supports the a->b->c->target multiple inheritance scenario.", function() {

		function A() {}
		A.prototype.x = function x1() {};

		function B() {}
		topiarist.extend(B, A);
		B.prototype.x = function x2() {};

		function C() {}
		topiarist.extend(C, B);
		C.prototype.x = function x3() {};

		it('gets the latest implementation when they are applied in order.', function() {
			function Target() {};

			topiarist.extend(Target, A);
			topiarist.inherit(Target, B);
			topiarist.inherit(Target, C);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});

		it('gets the latest implementation when they are applied out of order.', function() {
			function Target() {};

			topiarist.extend(Target, A);
			topiarist.inherit(Target, C);
			topiarist.inherit(Target, B);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});

		it('gets the latest implementation when we inherit from a parent.', function() {
			function Target() {};

			topiarist.extend(Target, B);
			topiarist.inherit(Target, A);
			topiarist.inherit(Target, C);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});

		it('throws an error when the inheritance tree is incompatible.', function() {
			function Incompatible() {};
			topiarist.extend(Incompatible, A);
			Incompatible.prototype.x = function() {};

			function Target() {};

			topiarist.extend(Target, A);
			topiarist.inherit(Target, B);
			topiarist.inherit(Target, C);
			expect(function() {
				topiarist.inherit(Target, Incompatible);
			}).toThrow(err.ALREADY_PRESENT('x', 'Incompatible', 'Target'));
		});

		it('does not throw an error when the inheritance tree doesn\'t clash.', function() {
			function NotIncompatible() {};
			topiarist.extend(NotIncompatible, A);

			function Target() {};

			topiarist.extend(Target, A);
			topiarist.inherit(Target, B);
			topiarist.inherit(Target, C);
			topiarist.inherit(Target, NotIncompatible);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});
	});

	it('checks multiple-inherited parents when checking compatibility.', function() {
		function A() {};
		A.prototype.x = function X1() {};

		function B() {};
		B.prototype = {};
		topiarist.inherit(B, A);
		B.prototype.x = function X2() {};

		function Target1() {};
		topiarist.inherit(Target1, A);
		topiarist.inherit(Target1, B);

		expect(Target1.prototype.x).toBe(B.prototype.x);
	});

});