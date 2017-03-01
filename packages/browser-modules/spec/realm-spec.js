// Object.create() polyfill for IE8 (taken from <https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create>)
function objectCreate(prototype) {
	var Object = function() {};
	if (arguments.length > 1) {
		throw Error('Second argument not supported');
	}
	if (typeof prototype != 'object') {
		throw TypeError('Argument must be an object');
	}
	Object.prototype = prototype;
	var result = new Object();
	Object.prototype = null;
	return result;
};

function MockConsole() {
	this.messages = [];
	this.log = function() {
		MockConsole.log.call(this, 'info');
	};
	this.warn = function() {
		MockConsole.log.call(this, 'warn');
	}
}

MockConsole.log = function(messagePrefix, message) {
	this.messages.push(messagePrefix + ': ' + message);
};

describe('a realm', function() {
	var global = Function('return this')();
	var globalRealm = global.realm || require('..');
	var Realm = globalRealm.constructor;

	var testRealm, subrealm, mockConsole, origConsole;

	beforeEach(function() {
		testRealm = new Realm();
		subrealm = testRealm.subrealm();
		if(typeof(console) !== 'undefined') {
			origConsole = console;
		}
		mockConsole = new MockConsole();
		console = mockConsole;
	});

	afterEach(function() {
		console = origConsole;
		subrealm.uninstall();
		testRealm.uninstall();
		expect(mockConsole.messages.join(', ')).toBe('');
	});

	it('does not call definition functions on define.', function() {
		var definitionHasBeenCalled = false;
		testRealm.define('MyClass', function(require, exports, module) {
			definitionHasBeenCalled = true;
			module.exports = function MyClass() {};
		});
		expect(definitionHasBeenCalled).toBe(false);
	});

	it('allows defined objects to be required, and provides the same value each time', function() {
		var id = 0;
		testRealm.define('id', function(require, exports, module) {
			module.exports = ++id;
		});

		expect(testRealm.require('id')).toBe(1);
		expect(testRealm.require('id')).toBe(1);
	});

	it('optionally allows a fresh value to be returned each time', function() {
		var id = 0;
		testRealm.define('id', function(require, exports, module) {
			module.preventCaching = true;
			module.exports = ++id;
		});

		expect(testRealm.require('id')).toBe(1);
		expect(testRealm.require('id')).toBe(2);
	});

	it('also allows a fresh value to be returned initially, while still allowing caching at some future point', function() {
		var id = 0;
		testRealm.define('id', function(require, exports, module) {
			if(++id < 3) {
				module.preventCaching = true;
			}

			module.exports = id;
		});

		expect(testRealm.require('id')).toBe(1);
		expect(testRealm.require('id')).toBe(2);
		expect(testRealm.require('id')).toBe(3);
		expect(testRealm.require('id')).toBe(3);
	});

	it('allows one definition to require a definition defined later.', function() {
		testRealm.define('ClassB', function(require, exports, module) {
			exports.parent = require('ClassA');
		});
		testRealm.define('ClassA', function(require, exports, module) {
			module.exports = function() {};
		});

		var ClassA = testRealm.require('ClassA');
		var ClassB = testRealm.require('ClassB');
		expect(ClassB.parent).toBe(ClassA);
	});

	it('allows one definition to require another in a relative way.', function() {
		testRealm.define('derived/ClassB', function(require, exports, module) {
			exports.parent = require('../original/ClassA');
		});
		testRealm.define('original/ClassA', function(require, exports, module) {
			module.exports = function() {};
		});

		var ClassA = testRealm.require('original/ClassA');
		var ClassB = testRealm.require('derived/ClassB');
		expect(ClassB.parent).toBe(ClassA);
	});

	it('allows require() to be used globally if the realm is installed.', function() {
		var TheClass = function() {};
		testRealm.define('TheClass', function(require, exports, module) {
			module.exports = TheClass;
		});

		testRealm.install();
		expect(require('TheClass')).toBe(TheClass);
	});

	it('throws an error if there is a define-time circular dependency', function() {
		testRealm.define('pkg/ClassA', function(require, exports, module) {
			require('pkg/ClassB');
			module.exports = function() {};
		});
		testRealm.define('pkg/ClassB', function(require, exports, module) {
			require('pkg/ClassA');
			module.exports = function() {};
		});

		expect(function() {
			testRealm.require('pkg/ClassA');
		}).toThrow(Error('Circular dependency detected: pkg/ClassA => pkg/ClassB => pkg/ClassA'));
	});

	it('allows a circular define-time dependency that is partially, but not wholly, exported', function() {
		testRealm.define('pkg/A', function(require, exports, module) {
			var B = require('pkg/B');
			function ClassA() {
				this.x = B.X;
				this.y = B.Y;
			}
			module.exports = new ClassA();
		});
		testRealm.define('pkg/B', function(require, exports, module) {
			exports.X = 'X';
			exports.A = require('pkg/A');
			exports.Y = 'Y';
		});

		expect(function() {
			testRealm.require('pkg/B');
		}).not.toThrow();
	});

	it('tolerates use-time circular dependencies', function() {
		testRealm.define('pkg/ClassA', function(require, exports, module) {
			module.exports = function() {};
			require('pkg/ClassB');
		});
		testRealm.define('pkg/ClassB', function(require, exports, module) {
			module.exports = function() {};
			require('pkg/ClassA');
		});

		expect(function() {
			testRealm.require('pkg/ClassA');
		}).not.toThrow();
	});

	it('tolerates mixed circular dependencies where the required node has a use-time dependency', function() {
		testRealm.define('pkg/ClassA', function(require, exports, module) {
			var ClassB;
			function ClassA() {
				this.b = new ClassB();
			};
			module.exports = ClassA;
			ClassB = require('pkg/ClassB');
		});
		testRealm.define('pkg/ClassB', function(require, exports, module) {
			var ClassA = require('pkg/ClassA');
			function ClassB() {
			};
			ClassB.prototype = objectCreate(ClassA.prototype);
			module.exports = ClassB;
		});

		expect(function() {
			testRealm.require('pkg/ClassA');
		}).not.toThrow();

		var A = testRealm.require('pkg/ClassA');
		var B = testRealm.require('pkg/ClassB');
		expect(new B() instanceof A).toBeTruthy();
		expect((new A()).b instanceof B).toBeTruthy();
	});

	it('fails with a nice error message for mixed circular dependencies where the required node has a define-time dependency', function() {
		testRealm.define('pkg/ClassA', function(require, exports, module) {
			var ClassB;
			function ClassA() {
				this.b = new ClassB();
			};
			module.exports = ClassA;
			ClassB = require('pkg/ClassB');
		});
		testRealm.define('pkg/ClassB', function(require, exports, module) {
			var ClassA = require('pkg/ClassA');
			function ClassB() {
			};
			ClassB.prototype = Object.create(ClassA.prototype);
			module.exports = ClassB;
		});

		expect(function() {
			testRealm.require('pkg/ClassB');
		}).toThrow(new Error('Circular dependency detected: pkg/ClassB => pkg/ClassA -> pkg/ClassB'));
	});

	it('passes through exceptions that occur during definition', function() {
		testRealm.define('ClassA', function(require, exports, module) {
			require('ClassB');
			module.exports = function() {};
		});

		testRealm.define('ClassB', function(require, exports, module) {
			throw new Error('define-time error!');
		});

		expect(function() {
			testRealm.require('ClassA');
		}).toThrow(new Error('define-time error!'));
	});

	it('a simple object required from a subrealm is unique to the subrealm.', function() {
		testRealm.define('ClassA', function(require, exports, module) {
			function test() {}
			test.prototype.foo = function() {};
			module.exports = test;
		});

		var ClassA = testRealm.require('ClassA');

		var subrealm = testRealm.subrealm();

		var SubrealmClassA = subrealm.require('ClassA');
		expect(SubrealmClassA).not.toBe(ClassA);
	});

	it('a complex object (with single dependency) required from a subrealm is unique to the subrealm (including its dependency)', function() {
		testRealm.define('Dependency', function(require, exports, module) {
			function Dependency() {}
			module.exports = Dependency;
		});

		testRealm.define('ClassA', function(require, exports, module) {
			var Dependency = require('Dependency');

			function test() {
				this.dependency = Dependency;
			}
			module.exports = test;
		});

		var ClassA = testRealm.require('ClassA');

		var subrealm = testRealm.subrealm();

		var SubrealmClassA = subrealm.require('ClassA');

		expect(SubrealmClassA).not.toBe(ClassA);

		var classA = new ClassA();
		var otherClassA = new ClassA();

		expect(classA.dependency).toEqual(otherClassA.dependency);
		var subrealmClassA = new SubrealmClassA();

		expect(classA.dependency).not.toEqual(subrealmClassA.dependency);
	});

	it('a complex object (with dependencies) required from a subrealm is unique to the subrealm (including its dependency chain)', function() {

		testRealm.define('SubDependency', function(require, exports, module) {
			function SubDependency() {}
			SubDependency.prototype.baz = function() { return 'sub-dep'; };
			module.exports = SubDependency;
		});

		testRealm.define('Dependency', function(require, exports, module) {
			var SubDependency = require('SubDependency');
			function Dependency() {
				this.subDependency = new SubDependency();
			}
			Dependency.prototype.bar = function() { return 'dep'; };
			module.exports = Dependency;
		});

		testRealm.define('ClassA', function(require, exports, module) {
			var Dependency = require('Dependency');
			function ClassA() {
				this.dependency = new Dependency();
			}
			module.exports = ClassA;
		});

		var ClassA = testRealm.require('ClassA');

		var classA = new ClassA();
		var otherClassA = new ClassA();

		expect(classA).toEqual(otherClassA);
		expect(classA.dependency).toEqual(otherClassA.dependency);
		expect(classA.dependency.subDependency).toEqual(otherClassA.dependency.subDependency);
		expect(classA.dependency.subDependency.baz()).toBe('sub-dep');

		// start subrealm
		var subrealm = testRealm.subrealm();

		subrealm.define('SubDependency', function(require, exports, module) {
			function SubDependency() {
			}

			SubDependency.prototype.baz = function() { return 'sub-dep-changed'; };
			module.exports = SubDependency;
		});

		var SubrealmClassA = subrealm.require('ClassA');

		expect(SubrealmClassA).not.toBe(ClassA);

		var subrealmClassA = new SubrealmClassA();

		// verify non-equality
		expect(classA).not.toEqual(subrealmClassA);

		// test dependency
		expect(classA.dependency.bar()).toBe('dep');
		expect(classA.dependency).not.toEqual(subrealmClassA.dependency);

		// test redined sub-dependency
		expect(subrealmClassA.dependency.subDependency.baz()).toBe('sub-dep-changed');
	});

	it('allows the redefinition of a class in a subrealm.', function() {
		testRealm.define('derived/ClassB', function(require, exports, module) {
			exports.parent = require('../original/ClassA');
		});
		testRealm.define('original/ClassA', function(require, exports, module) {
			module.exports = function() {};
		});

		var ClassA = testRealm.require('original/ClassA');
		var ClassB = testRealm.require('derived/ClassB');
		expect(ClassB.parent).toBe(ClassA);

		var ReplacementClassA = function() {};
		expect(ClassA).not.toBe(ReplacementClassA);

		subrealm.define('original/ClassA', function(require, exports, module) {
			module.exports = ReplacementClassA;
		});

		var subrealmClassB = subrealm.require('derived/ClassB');
		expect(subrealmClassB).not.toBe(ClassB);
		expect(subrealmClassB.parent).toBe(ReplacementClassA);
	});

	it('allows existing class definitions to be augmented or re-cast in a safe way.', function() {
		testRealm.define('A', function(require, exports, module) {
			module.exports = 5;
		});

		subrealm.define('A', function(require, exports, module) {
			var A = subrealm.recast('A');
			module.exports = A * 10;
		});

		expect(subrealm.require('A')).toBe(50);
	});

	it('allows require() to be used globally if the sub-realm is installed.', function() {
		testRealm.define('TheClass', function(require, exports, module) {
			module.exports = function() {};
		});
		var TheClass = testRealm.require('TheClass');

		subrealm.define('TheClass', function(require, exports, module) {
			module.exports = function() {};
		});

		subrealm.install();
		expect(require('TheClass')).not.toBe(TheClass);
	});

	it('allows a sub-realm to be installed/uninstalled at the same time a realm is already installed.', function() {
		testRealm.define('TheClass', function(require, exports, module) {
			module.exports = function() {};
		});

		testRealm.install();
		var TheClass = require('TheClass');

		subrealm.define('TheClass', function(require, exports, module) {
			module.exports = function() {};
		});

		subrealm.install();
		expect(require('TheClass')).not.toBe(TheClass);

		subrealm.uninstall();
		expect(require('TheClass')).toBe(TheClass);
	});

	it('allows a use-time require defined at the realm level to make use of an overridden definition at the sub-realm level.', function() {
		testRealm.define('Class', function(require, exports, module) {
			function Class() {
			}

			Class.getDependentClass = function() {
				return require('DependentClass');
			};
			module.exports = Class;
		});
		testRealm.define('DependentClass', function(require, exports, module) {
			module.exports = function() {};
		});

		testRealm.install();
		var DependentClass = require('DependentClass');
		expect(require('Class').getDependentClass()).toBe(DependentClass);

		subrealm.define('DependentClass', function(require, exports, module) {
			module.exports = function() {};
		});
		subrealm.install();
		expect(require('Class').getDependentClass()).not.toBe(DependentClass);
	});

	it('allows a use-time require from a pre-existing object at the realm level to make use of an overridden definition at the sub-realm level.', function() {
		testRealm.define('Class', function(require, exports, module) {
			function Class() {
			}

			Class.prototype.getDependentClass = function() {
				return require('DependentClass');
			};
			module.exports = Class;
		});
		testRealm.define('DependentClass', function(require, exports, module) {
			module.exports = function() {};
		});

		testRealm.install();
		var DependentClass = require('DependentClass');
		var obj = new (require('Class'))();
		expect(obj.getDependentClass()).toBe(DependentClass);

		subrealm.define('DependentClass', function(require, exports, module) {
			module.exports = function() {};
		});
		subrealm.install();
		expect(obj.getDependentClass()).not.toBe(DependentClass);
	});

	it('does not allow you to keep references to objects created under the original realm and then not install a sub-realm you use to override definitions with.', function() {
		testRealm.define('Class', function(require, exports, module) {
			function Class() {
			}

			Class.prototype.getDependentClass = function() {
				return require('DependentClass');
			};
			module.exports = Class;
		});
		testRealm.define('DependentClass', function(require, exports, module) {
			module.exports = function() {};
		});

		var DependentClass = testRealm.require('DependentClass');
		var obj = new (testRealm.require('Class'))();
		expect(obj.getDependentClass()).toBe(DependentClass);

		subrealm.define('DependentClass', function(require, exports, module) {
			module.exports = function() {};
		});

		// Note: this should be 'not.toBe' if sub-realms could be used with pre-existing realm objects without re-installing
		expect(obj.getDependentClass()).toBe(DependentClass);
	});
});
