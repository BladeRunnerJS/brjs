/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.extend", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var ChildClass, ParentClass;

	beforeEach(function() {
		// This horrible structure is to avoid a bug in IE8 where the obvious way of writing this
		// would have created *locals* ChildClass and ParentClass and not modified the values from
		// the above scope.
		ChildClass = (function() {
			return function ChildClass(){};
		})();
		ParentClass = (function() {
			return function ParentClass(){};
		})();
	});

	it("throws an error if the child class is not a function.", function() {
		ChildClass = 23;
		expect(function() {
			topiary.extend(ChildClass, ParentClass);
		}).toThrow(err.SUBCLASS_NOT_CONSTRUCTOR());
	});

	it("throws an error if the parent class is not a function.", function() {
		ParentClass = 23;
		expect(function() {
			topiary.extend(ChildClass, ParentClass);
		}).toThrow(err.SUPERCLASS_NOT_CONSTRUCTOR('ChildClass'));
	});

	it("throws an error if the child class already has something on its prototype.", function() {
		ChildClass.prototype.someThing = 23;
		expect(function() {
			topiary.extend(ChildClass, ParentClass);
		}).toThrow(err.PROTOTYPE_NOT_CLEAN('ChildClass', 'someThing')) ;
	});

	it("copies 'static' properties (of the constructor function itself) to the child from the parent class.", function() {
		ParentClass.MY_IMPORTANT_NUMBER = 23;
		topiary.extend(ChildClass, ParentClass);

		expect( ChildClass.MY_IMPORTANT_NUMBER ).toBe( 23 );
	});

	describe('causes the child class to create instances which', function() {
		var instance;

		beforeEach(function() {
			topiary.extend(ChildClass, ParentClass);
			instance = new ChildClass();
		});

		it("have .constructor properties pointing at their constructor.", function() {
			expect( instance.constructor ).toBe( ChildClass );
		});

		it("have attributes from the parent accessible via the prototype chain.", function() {
			ParentClass.prototype.anInheritedProperty = "parent property";
			expect( instance.anInheritedProperty).toBe( ParentClass.prototype.anInheritedProperty );
		});

		it("allows child classes to override parent properties without affecting the parent.", function() {
			ParentClass.prototype.anInheritedProperty = "parent property";
			ChildClass.prototype.anInheritedProperty = "child property";

			expect( instance.anInheritedProperty ).toBe( "child property" );
			var parentInstance = new ParentClass();
			expect( parentInstance.anInheritedProperty ).toBe( "parent property" );
		});
	});

});