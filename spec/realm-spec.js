// Object.create() polyfill for IE8 (taken from <https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/create>)
if (typeof Object.create != 'function') {
	Object.create = (function() {
		var Object = function() {};
		return function (prototype) {
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
	})();
}

// Object.create() polyfill for IE8 (taken from <https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Function/bind>)
if (!Function.prototype.bind) {
  Function.prototype.bind = function(oThis) {
    if (typeof this !== 'function') {
      // closest thing possible to the ECMAScript 5
      // internal IsCallable function
      throw new TypeError('Function.prototype.bind - what is trying to be bound is not callable');
    }

    var aArgs   = Array.prototype.slice.call(arguments, 1),
        fToBind = this,
        fNOP    = function() {},
        fBound  = function() {
          return fToBind.apply(this instanceof fNOP && oThis
                 ? this
                 : oThis,
                 aArgs.concat(Array.prototype.slice.call(arguments)));
        };

    fNOP.prototype = this.prototype;
    fBound.prototype = new fNOP();

    return fBound;
  };
}

function MockConsole() {
	this.messages = [];
	this.log = MockConsole.log.bind(this, 'info');
	this.warn = MockConsole.log.bind(this, 'warn');
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
		mockConsole = new MockConsole();
		origConsole = console;
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
		}).toThrow(Error('Circular dependency detected: pkg/ClassA -> pkg/ClassB -> pkg/ClassA'));
	});

	it('throws an error even when a define-time dependency is partially, but not wholly, exported', function() {
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
			testRealm.require('pkg/A');
		}).toThrow('Circular dependency detected: pkg/A -> pkg/B -> pkg/A');
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
			ClassB.prototype = Object.create(ClassA.prototype);
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

	it('tolerates mixed circular dependencies where the required node has a define-time dependency', function() {
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
		}).not.toThrow();

		var A = testRealm.require('pkg/ClassA');
		var B = testRealm.require('pkg/ClassB');
		expect(new B() instanceof A).toBeTruthy();
		expect((new A()).b instanceof B).toBeTruthy();

		expect(mockConsole.messages.shift()).toBe('warn: Circular dependency detected: pkg/ClassB -> pkg/ClassA -> pkg/ClassB');
		expect(mockConsole.messages.shift()).toBe("info: requiring 'pkg/ClassA' early to solve the circular dependency problem");
	});

	it('tolerates large mixed circular dependencies where the required node has a define-time dependency', function() {
		testRealm.define('pkg/ClassA', function(require, exports, module) {
			var ClassB = require('pkg/ClassB');
			function ClassA() {
			};
			ClassA.prototype = Object.create(ClassB.prototype);
			module.exports = ClassA;
		});
		testRealm.define('pkg/ClassB', function(require, exports, module) {
			var ClassC;
			function ClassB() {
				this.c = new ClassC();
			};
			module.exports = ClassB;
			ClassC = require('pkg/ClassC');
		});
		testRealm.define('pkg/ClassC', function(require, exports, module) {
			var ClassA = require('pkg/ClassA');
			function ClassC() {
			};
			ClassC.prototype = Object.create(ClassA.prototype);
			module.exports = ClassC;
		});

		expect(function() {
			testRealm.require('pkg/ClassC');
		}).not.toThrow();

		var A = testRealm.require('pkg/ClassA');
		var B = testRealm.require('pkg/ClassB');
		var C = testRealm.require('pkg/ClassC');
		expect(new A() instanceof B).toBeTruthy();
		expect((new B()).c instanceof C).toBeTruthy();
		expect(new C() instanceof A).toBeTruthy();

		expect(mockConsole.messages.shift()).toBe('warn: Circular dependency detected: pkg/ClassC -> pkg/ClassA -> pkg/ClassB -> pkg/ClassC');
		expect(mockConsole.messages.shift()).toBe("info: requiring 'pkg/ClassB' early to solve the circular dependency problem");
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

	it('can recover from a second circular dependency error that occurs while we are still recovering from an earlier circular dependency error', function() {
		testRealm.define('A', function(require, exports, module) {
			var B = require('B');
			function A() {
			}
			A.prototype = Object.create(B.prototype);
			module.exports = A;
		});

		testRealm.define('B', function(require, exports, module) {
			var C = require('C');
			function B() {
				this.x = new X();
			}
			B.prototype = Object.create(C.prototype);
			module.exports = B;
			var X = require('X');
		});

		testRealm.define('C', function(require, exports, module) {
			function C() {
				this.a = new A();
			}
			module.exports = C;
			var A = require('A');
		});

		testRealm.define('X', function(require, exports, module) {
			function X() {
				this.a = new A();
			}
			module.exports = X;
			var A = require('A');
		});

		var A = testRealm.require('A');
		var B = testRealm.require('B');
		var C = testRealm.require('C');
		var X = testRealm.require('X');

		expect(mockConsole.messages.shift()).toBe('warn: Circular dependency detected: A -> B -> C -> A');
		expect(mockConsole.messages.shift()).toBe("info: requiring 'C' early to solve the circular dependency problem");
		expect(mockConsole.messages.shift()).toBe('warn: Circular dependency detected: A -> B -> X -> A');
		expect(mockConsole.messages.shift()).toBe("info: requiring 'X' early to solve the circular dependency problem");

		expect(new A() instanceof B).toBe(true);
		expect(new A() instanceof C).toBe(true);

		expect(new B() instanceof C).toBe(true);
		expect(new B().x instanceof X).toBe(true);

		expect(new C().a instanceof A).toBe(true);

		expect(new X().a instanceof A).toBe(true);
	});

	it('can recover from a second circular dependency error that engulfs the first', function() {
		testRealm.define('A', function(require, exports, module) {
			var B = require('B');
			function A() {
			}
			A.prototype = Object.create(B.prototype);
			module.exports = A;
		});

		testRealm.define('B', function(require, exports, module) {
			var C = require('C');
			function B() {
				this.x = new X();
			}
			B.prototype = Object.create(C.prototype);
			module.exports = B;
			var X = require('X');
		});

		testRealm.define('C', function(require, exports, module) {
			function C() {
				this.a = new A();
			}
			module.exports = C;
			var A = require('A');
		});

		testRealm.define('X', function(require, exports, module) {
			function X() {
				this.s = new S();
			}
			module.exports = X;
			var S = require('S');
		});

		testRealm.define('S', function(require, exports, module) {
			function S() {
				this.a = new A();
			}
			module.exports = S;
			var A = require('A');
		});

		var A = testRealm.require('A');
		var B = testRealm.require('B');
		var C = testRealm.require('C');
		var X = testRealm.require('X');
		var S = testRealm.require('S');

		expect(mockConsole.messages.shift()).toBe('warn: Circular dependency detected: A -> B -> C -> A');
		expect(mockConsole.messages.shift()).toBe("info: requiring 'C' early to solve the circular dependency problem");
		expect(mockConsole.messages.shift()).toBe('warn: Circular dependency detected: A -> B -> X -> S -> A');
		expect(mockConsole.messages.shift()).toBe("info: requiring 'S' early to solve the circular dependency problem");

		expect(new A() instanceof B).toBe(true);
		expect(new A() instanceof C).toBe(true);

		expect(new B() instanceof C).toBe(true);
		expect(new B().x instanceof X).toBe(true);

		expect(new C().a instanceof A).toBe(true);

		expect(new X().s instanceof S).toBe(true);

		expect(new S().a instanceof A).toBe(true);
	});
});
