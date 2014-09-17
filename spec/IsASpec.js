/* global describe, beforeEach, it, expect, topiarist, err */
describe("topiarist.isA", function() {
	if (typeof topiarist === 'undefined') topiarist = require('../lib/topiarist.js');
	var err = topiarist._err;

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

		topiarist.hasImplemented(ParentClass, ParentsInterface);
		topiarist.mixin(ParentClass, ParentsMixin);

		ChildClass = (function() {
			return function ChildClass() {};
		})();
		topiarist.extend(ChildClass, ParentClass);
		topiarist.mixin(ChildClass, MixinClass);
		topiarist.mixin(ChildClass, ObjMixin);
		topiarist.hasImplemented(ChildClass, InterfaceClass);

		instance = new ChildClass();
	});

	it('throws an error if the potential assignee is not a constructor.', function() {
		expect( function() {
			topiarist.isA(instance, 34);
		}).toThrow(err.NOT_CONSTRUCTOR('Parent', 'isA', 'number'));
	});

	it('returns false for a null instance.', function() {
		expect( topiarist.isA(null, ChildClass)).toBe( false );
	});

	it('returns true for an instance and its constructor.', function() {
		expect( topiarist.isA(instance, ChildClass)).toBe( true );
	});

	it('returns true for an instance and an interface it implements.', function() {
		expect( topiarist.isA(instance, InterfaceClass)).toBe( true );
	});

	it('returns true for an instance and a mixin its class mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiarist.isA(instance, MixinClass)).toBe( true );
	});

	it('returns true for an instance and its superclass.', function() {
		expect( topiarist.isA(instance, ParentClass)).toBe( true );
	});

	it('returns true for an instance and an interface that a parent extends.', function() {
		expect( topiarist.isA(instance, ParentsInterface)).toBe( true );
	});

	it('returns true for an instance and something the parent mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiarist.isA(instance, ParentsMixin)).toBe( true );
	});

	it('returns false for a instance and an unrelated other class, even if that class has no different properties.', function() {
		expect( topiarist.isA(instance, OtherClass)).toBe( false );
	});

	it('returns true for an instance and a mixin that was defined without a constructor.', function() {
		expect( topiarist.isA(instance, ObjMixin)).toBe( true );
	});
});