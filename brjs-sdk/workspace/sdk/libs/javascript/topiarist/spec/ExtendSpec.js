/* global describe, beforeEach, it, expect, topiarist, err */
describe("topiarist.extend", function() {
	if (typeof topiarist === 'undefined') topiarist = require('../lib/topiarist.js');
	var err = topiarist._err;

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
			topiarist.extend(ChildClass, ParentClass);
		}).toThrow(err.SUBCLASS_NOT_CONSTRUCTOR());
	});

	it("throws an error if the parent class is not a function.", function() {
		ParentClass = 23;
		expect(function() {
			topiarist.extend(ChildClass, ParentClass);
		}).toThrow(err.SUPERCLASS_NOT_CONSTRUCTOR('ChildClass'));
	});

	it("throws an error if the child class already has something on its prototype.", function() {
		ChildClass.prototype.someThing = 23;
		expect(function() {
			topiarist.extend(ChildClass, ParentClass);
		}).toThrow(err.PROTOTYPE_NOT_CLEAN('ChildClass', 'someThing')) ;
	});

	it("copies 'static' properties (of the constructor function itself) to the child from the parent class.", function() {
		ParentClass.MY_IMPORTANT_NUMBER = 23;
		topiarist.extend(ChildClass, ParentClass);

		expect( ChildClass.MY_IMPORTANT_NUMBER ).toBe( 23 );
	});

	describe('causes the child class to create instances which', function() {
		var instance;

		beforeEach(function() {
			topiarist.extend(ChildClass, ParentClass);
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

	it('allows extra properties to be specified which are added to the child prototype', function() {
		function extraFunction() {};
		topiarist.extend(ChildClass, ParentClass, {
			"anExtraProperty": extraFunction
		});

		var instance = new ChildClass();

		expect( instance.anExtraProperty ).toBe(extraFunction);
	});

	it('allows the constructor to be specified in extra properties.', function() {
		function extraFunction() {};
		var MyClass = topiarist.extend(null, ParentClass, {
			"constructor": ChildClass,
			"anExtraProperty": extraFunction
		});

		var instance = new MyClass();

		expect(instance instanceof ChildClass).toBe(true);
		expect( instance.anExtraProperty ).toBe(extraFunction);
	});

	it('throws an exception if the constructor specified in extra properties is different to the provided constructor.', function() {
		function MyClass() {};
		expect(function() {
			topiarist.extend(ChildClass, ParentClass, {
				"constructor": MyClass
			});
		}).toThrow(err.TWO_CONSTRUCTORS("ChildClass"));
	});
});