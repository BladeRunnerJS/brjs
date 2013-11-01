/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.isAssignableFrom", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var ChildClass, ParentClass, InterfaceClass, MixinClass, OtherClass, ParentsInterface, ParentsMixin;

	beforeEach(function() {
		// This horrible structure is to avoid a bug in IE8 where the obvious way of writing this
		// would have created *locals* ChildClass and ParentClass and not modified the values from
		// the above scope.
		InterfaceClass = (function() {
			return function InterfaceClass() {};
		})();
		MixinClass = (function() {
			return function MixinClass() {};
		})();
		OtherClass = (function() {
			return function OtherClass() {};
		})();

		ParentsInterface = (function() {
			return function ParentsInterface() {};
		})();
		ParentsMixin = (function() {
			return function ParentsMixin() {};
		})();
		ParentClass = (function() {
			return function ParentClass() {};
		})();

		topiary.implement(ParentClass, ParentsInterface);
		topiary.mixin(ParentClass, ParentsMixin);

		ChildClass = (function() {
			return function ChildClass() {};
		})();
		topiary.extend(ChildClass, ParentClass);
		topiary.mixin(ChildClass, MixinClass);
		topiary.implement(ChildClass, InterfaceClass);
	});

	it('throws an error if the class is not a constructor.', function() {
		expect( function() {
			topiary.isAssignableFrom(34, ParentClass);
		}).toThrow(err.NOT_CONSTRUCTOR('Class', 'isAssignableFrom', 'number'));
	});

	it('throws an error if the potential assignee is not a constructor.', function() {
		expect( function() {
			topiary.isAssignableFrom(ChildClass, 34);
		}).toThrow(err.NOT_CONSTRUCTOR('Parent', 'isAssignableFrom', 'number'));
	});

	it('returns true for a class and itself.', function() {
		expect( topiary.isAssignableFrom(ChildClass, ChildClass)).toBe( true );
	});

	it('returns true for a class and an interface it implements.', function() {
		expect( topiary.isAssignableFrom(ChildClass, InterfaceClass)).toBe( true );
	});

	it('returns true for a class and a mixin it mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiary.isAssignableFrom(ChildClass, MixinClass)).toBe( true );
	});

	it('returns true for a class and a class it extends.', function() {
		expect( topiary.isAssignableFrom(ChildClass, ParentClass)).toBe( true );
	});

	it('returns true for a class and an interface that a parent extends.', function() {
		expect( topiary.isAssignableFrom(ChildClass, ParentsInterface)).toBe( true );
	});

	it('returns true for a class and something the parent mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiary.isAssignableFrom(ChildClass, ParentsMixin)).toBe( true );
	});

	it('returns false for a class and an unrelated other class.', function() {
		expect( topiary.isAssignableFrom(ChildClass, OtherClass)).toBe( false );
	});
});
