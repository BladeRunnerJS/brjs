/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.inherit", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var Class, Parent, MultiParent;

	beforeEach(function() {
		Class = function() {};
		Parent = function() {};
		topiary.extend(Class, Parent);
		MultiParent = function() {};
	});

	it("throws an error if the target is not a constructor.", function() {
		expect( function() {
			topiary.inherit(23, MultiParent);
		}).toThrow(err.NOT_CONSTRUCTOR('Target', 'inherit', 'number'));
	});

	it("throws an error if the inherited parent is null.", function() {
		expect( function() {
			topiary.inherit(Class, null);
		}).toThrow(err.WRONG_TYPE('Parent', 'inherit', 'non-null object or function', 'null'));
	});

	it("copies inherited functionality across to the class.", function() {
		var inheritedFuncRan = false;
		MultiParent.prototype.inheritedFunc = function() {
			inheritedFuncRan = true;
		};
		topiary.inherit(Class, MultiParent);

		var instance = new Class();
		instance.inheritedFunc();

		expect(inheritedFuncRan).toBe(true);
	});

	it("allows inherited functionality to affect instance state.", function() {
		MultiParent.prototype.inheritedFunc = function() {
			this.state = "modified by inherited function";
		};
		topiary.inherit(Class, MultiParent);

		var instance = new Class();
		instance.inheritedFunc();

		expect(instance.state).toBe("modified by inherited function");
	});

	it("throws an error if an inherited parent has functions that clash with the target class.", function() {
		MultiParent.prototype.thingy = function() {};
		MultiParent.prototype.clashingThingy = function() {};

		Class.prototype.clashingThingy = function() {};

		expect(function() {
			topiary.inherit(Class, MultiParent);
		}).toThrow(err.ALREADY_PRESENT('clashingThingy', 'parent', 'target'));
	});

	it("does not copy any functionality if it can't copy everything.", function() {
		MultiParent.prototype.thingy = function() {};
		MultiParent.prototype.clashingThingy = function() {};

		Class.prototype.clashingThingy = function() {};

		try {
			topiary.inherit(Class, MultiParent);
		} catch (e) {}

		expect(Class.prototype.thingy).toBeUndefined();
	});

	it("copies functionality from an inherited classes parents.", function() {
		function MultiOverMixin() {}
		MultiOverMixin.prototype.mixedIn = function() {};

		function MultiOverParent() {}
		MultiOverParent.prototype.uber = function() {};
		topiary.extend(MultiParent, MultiOverParent);
		topiary.mixin(MultiParent, MultiOverMixin);

		topiary.inherit(Class, MultiParent);

		var instance = new Class();

		expect(typeof instance.mixedIn).toBe('function');
		expect(typeof instance.uber).toBe('function');
	});

	it("does not throw an error if we attempt to inherit something that has already been inherited higher up the tree, even if that has then been modified.", function() {
		function MultiOverMixin() {}
		MultiOverMixin.prototype.mixedIn = function() {};

		function MultiOverParent() {}
		MultiOverParent.prototype.uber = function() {};
		topiary.extend(MultiParent, MultiOverParent);
		topiary.mixin(MultiParent, MultiOverMixin);

		// override uber
		MultiParent.prototype.uber = function() {};

		topiary.inherit(Class, MultiParent);

		topiary.inherit(Class, MultiOverParent);

		var instance = new Class();

		expect(instance.uber).toBe(MultiParent.prototype.uber );
	});

	describe("supports the a->b->c->target multiple inheritance scenario.", function() {

		function A() {}
		A.prototype.x = function x1() {};

		function B() {}
		topiary.extend(B, A);
		B.prototype.x = function x2() {};

		function C() {}
		topiary.extend(C, B);
		C.prototype.x = function x3() {};

		it('gets the latest implementation when they are applied in order.', function() {
			function Target() {};

			topiary.extend(Target, A);
			topiary.inherit(Target, B);
			topiary.inherit(Target, C);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});

		it('gets the latest implementation when they are applied out of order.', function() {
			function Target() {};

			topiary.extend(Target, A);
			topiary.inherit(Target, C);
			topiary.inherit(Target, B);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});

		it('gets the latest implementation when we inherit from a parent.', function() {
			function Target() {};

			topiary.extend(Target, B);
			topiary.inherit(Target, A);
			topiary.inherit(Target, C);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});

		it('throws an error when the inheritance tree is incompatible.', function() {
			function Incompatible() {};
			topiary.extend(Incompatible, A);
			Incompatible.prototype.x = function() {};

			function Target() {};

			topiary.extend(Target, A);
			topiary.inherit(Target, B);
			topiary.inherit(Target, C);
			expect(function() {
				topiary.inherit(Target, Incompatible);
			}).toThrow(err.ALREADY_PRESENT('x', 'Incompatible', 'Target'));
		});

		it('does not throw an error when the inheritance tree doesn\'t clash.', function() {
			function NotIncompatible() {};
			topiary.extend(NotIncompatible, A);

			function Target() {};

			topiary.extend(Target, A);
			topiary.inherit(Target, B);
			topiary.inherit(Target, C);
			topiary.inherit(Target, NotIncompatible);
			expect(Target.prototype.x).toBe(C.prototype.x);
		});
	});

	it('checks multiple-inherited parents when checking compatibility.', function() {
		function A() {};
		A.prototype.x = function X1() {};

		function B() {};
		B.prototype = {};
		topiary.inherit(B, A);
		B.prototype.x = function X2() {};

		function Target1() {};
		topiary.inherit(Target1, A);
		topiary.inherit(Target1, B);

		expect(Target1.prototype.x).toBe(B.prototype.x);
	});

});