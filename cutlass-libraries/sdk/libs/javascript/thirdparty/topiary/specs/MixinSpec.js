/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.mixin", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var Class, Mixin;

	beforeEach(function() {
		Class = function() {};
		Mixin = function() {};
	});

	it("throws an error if the target is not a constructor.", function() {
		expect( function() {
			topiary.mixin(23, Mixin);
		}).toThrow(err.NOT_CONSTRUCTOR('Target', 'mixin', 'number'));
	});

	it("throws an error if the mixin is null.", function() {
		expect( function() {
			topiary.mixin(Class, null);
		}).toThrow(err.WRONG_TYPE('Mix', 'mixin', 'non-null object or function', 'null'));
	});

	it("copies mixin functionality across to the class.", function() {
		var mixinFuncRan = false;
		Mixin.prototype.mixinFunc = function() {
			mixinFuncRan = true;
		};
		topiary.mixin(Class, Mixin);

		var instance = new Class();
		instance.mixinFunc();

		expect(mixinFuncRan).toBe(true);
	});

	it("ensures that mixin functionality cannot affect instance state.", function() {
		Mixin.prototype.mixinFunc = function() {
			this.state = "modified by mixin";
		};
		topiary.mixin(Class, Mixin);

		var instance = new Class();
		instance.mixinFunc();

		expect(instance.state).toBeUndefined();
	});

	it("ensures that functionality from a single mixin shares state.", function() {
		Mixin.prototype.mixinFunc = function() {
			this.ran = true;
		};
		Mixin.prototype.getRan = function() {
			return this.ran;
		};
		topiary.mixin(Class, Mixin);

		var instance = new Class();
		instance.mixinFunc();

		expect(instance.getRan()).toBe(true);
	});

	it("ensures that one mixin method can call another.", function() {
		Mixin.prototype.mixinFunc = function() {
			this.getRan();
			this.ran = true;
		};
		Mixin.prototype.getRan = function() {
			return this.ran;
		};
		topiary.mixin(Class, Mixin);

		var instance = new Class();
		instance.mixinFunc();

		expect(instance.getRan()).toBe(true);
	});

	it("ensures that functionality from two different mixins do not share state.", function() {
		function FirstMixin() {}
		FirstMixin.prototype.increment = function() {
			if (this.counter == null) this.counter = 0;
			return this.counter++;
		};
		function SecondMixin() {}
		SecondMixin.prototype.otherIncrement = function() {
			if (this.counter == null) this.counter = 0;
			return this.counter++;
		};

		topiary.mixin(Class, FirstMixin);
		topiary.mixin(Class, SecondMixin);

		var instance = new Class();

		instance.increment();
		var firstCount = instance.increment();

		var secondCount = instance.otherIncrement();

		expect(instance.counter).toBeUndefined();
		expect(firstCount).toBe(1);
		expect(secondCount).toBe(0);
	});

	it("throws an error if a mixin has functions that clash with the target.", function() {
		Mixin.prototype.thingy = function() {};
		Mixin.prototype.clashingThingy = function() {};

		Class.prototype.clashingThingy = function() {};

		expect(function() {
			topiary.mixin(Class, Mixin);
		}).toThrow(err.ALREADY_PRESENT('clashingThingy', 'mixin', 'target'));
	});

	it("does not mix any functionality in if it can't mix in everything.", function() {
		Mixin.prototype.thingy = function() {};
		Mixin.prototype.clashingThingy = function() {};

		Class.prototype.clashingThingy = function() {};

		try {
			topiary.mixin(Class, Mixin);
		} catch (e) {}

		expect(Class.prototype.thingy).toBeUndefined();
	});

	it("does not throw an error if we are mixing in functionality that has already been mixed in.", function() {
		function Parent() {}
		Mixin.prototype.mixinFunc = function() {};
		topiary.mixin(Parent, Mixin);
		topiary.extend(Class, Parent);

		topiary.mixin(Class, Mixin);

		var instance = new Class();
		expect(typeof instance.mixinFunc).toBe('function');
	});

	it("can mix in functionality from mixins without constructor functions.", function() {
		var mixinFuncRan = false;

		topiary.mixin(Class, {
			mixinFunc: function() {
				mixinFuncRan = true;
			}
		});

		var instance = new Class();
		instance.mixinFunc();

		expect(mixinFuncRan).toBe(true);
	});
});