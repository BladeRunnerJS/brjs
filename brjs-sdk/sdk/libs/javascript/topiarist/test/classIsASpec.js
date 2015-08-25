'use strict';

var topiarist = require('..');
var err = require('./errorFuncs');
var expect = require('expectations');

describe('topiarist.classIsA', function() {
	var ChildClass, ChildThatInherits, ParentClass, InterfaceClass, MixinClass, OtherClass, ParentsInterface, ParentsMixin;

	beforeEach(function() {
		// This horrible structure is to avoid a bug in IE8 where the obvious way of writing this
		// would have created *locals* ChildClass and ParentClass and not modified the values from
		// the above scope.
		/*eslint no-shadow:0*/
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

		topiarist.hasImplemented(ParentClass, ParentsInterface);
		topiarist.mixin(ParentClass, ParentsMixin);

		ChildClass = (function() {
			return function ChildClass() {};
		})();
		topiarist.extend(ChildClass, ParentClass);
		topiarist.mixin(ChildClass, MixinClass);
		topiarist.hasImplemented(ChildClass, InterfaceClass);

		ChildThatInherits = (function() {
			return function ChildThatInherits() {};
		})();
		topiarist.inherit(ChildThatInherits, OtherClass);
	});

	it('throws an error if the class is not a constructor.', function() {
		expect( function() {
			topiarist.classIsA(34, ParentClass);
		}).toThrow(err._NOT_CONSTRUCTOR('Class', 'classIsA', 'number'));
	});

	it('throws an error if the potential assignee is not a constructor.', function() {
		expect( function() {
			topiarist.classIsA(ChildClass, 34);
		}).toThrow(err._NOT_CONSTRUCTOR('Parent', 'classIsA', 'number'));
	});

	it('returns true for a class and itself.', function() {
		expect( topiarist.classIsA(ChildClass, ChildClass)).toBe( true );
	});

	it('returns true for a class and an interface it implements.', function() {
		expect( topiarist.classIsA(ChildClass, InterfaceClass)).toBe( true );
	});

	it('returns true for a class and a mixin it mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiarist.classIsA(ChildClass, MixinClass)).toBe( true );
	});

	it('returns true for a class and a class it extends.', function() {
		expect( topiarist.classIsA(ChildClass, ParentClass)).toBe( true );
	});

	it('returns true for a class and an interface that a parent extends.', function() {
		expect( topiarist.classIsA(ChildClass, ParentsInterface)).toBe( true );
	});

	it('returns true for a class and something the parent mixed in (theoretically, a violation, but this is probably useful behaviour).', function() {
		expect( topiarist.classIsA(ChildClass, ParentsMixin)).toBe( true );
	});

	it('returns false for a class and an unrelated other class.', function() {
		expect( topiarist.classIsA(ChildClass, OtherClass)).toBe( false );
	});

	it('returns true for a class that inherits other class.', function() {
		expect( topiarist.classIsA(ChildThatInherits, OtherClass)).toBe( true );
	});

	it('is also available under the alias isAssignableFrom, for backwards compatibility reasons.', function() {
		expect( topiarist.isAssignableFrom(ChildClass, ParentClass)).toBe( true );
	});
});
