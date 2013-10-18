/* global describe, beforeEach, it, expect, topiary, err */
describe("topiary.isA", function() {
	if (typeof topiary === 'undefined') topiary = require('../lib/topiary.js');
	var err = topiary._err;

	var instance, ChildClass, ParentClass, InterfaceClass, MixinClass, OtherClass, ParentsInterface, ParentsMixin, ObjMixin;

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
		ObjMixin = {};

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
		topiary.mixin(ChildClass, ObjMixin);
		topiary.implement(ChildClass, InterfaceClass);

		instance = new ChildClass();
	});

	it('throws an error if the potential assignee is not a constructor.', function() {
		expect( function() {
			topiary.isA(instance, 34);
		}).toThrow(err.NOT_CONSTRUCTOR('Parent', 'isA', 'number'));
	});

	it('returns false for a null instance.', function() {
		expect( topiary.isA(null, ChildClass)).toBe( false );
	});

	it('returns true for an instance and its constructor.', function() {
		expect( topiary.isA(instance, ChildClass)).toBe( true );
	});

	it('returns true for an instance and an interface it implements.', function() {
		expect( topiary.isA(instance, InterfaceClass)).toBe( true );
	});

	it('returns true for an instance and a mixin its class mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiary.isA(instance, MixinClass)).toBe( true );
	});

	it('returns true for an instance and its superclass.', function() {
		expect( topiary.isA(instance, ParentClass)).toBe( true );
	});

	it('returns true for an instance and an interface that a parent extends.', function() {
		expect( topiary.isA(instance, ParentsInterface)).toBe( true );
	});

	it('returns true for an instance and something the parent mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiary.isA(instance, ParentsMixin)).toBe( true );
	});

	it('returns false for a instance and an unrelated other class, even if that class has no different properties.', function() {
		expect( topiary.isA(instance, OtherClass)).toBe( false );
	});

	it('returns true for an instance and a mixin that was defined without a constructor.', function() {
		expect( topiary.isA(instance, ObjMixin)).toBe( true );
	});
});