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

describe("a realm", function() {
	var global = Function('return this')();
	var globalRealm = global.realm || require('..');
	var Realm = globalRealm.constructor;

	var testRealm, mockConsole, origConsole;

	beforeEach(function() {
		testRealm = new Realm();
		mockConsole = new MockConsole();
		origConsole = console;
		console = mockConsole;
	});

	afterEach(function() {
		console = origConsole;
		expect(mockConsole.messages.join(', ')).toBe('');
	});

	it("does not call definition functions on define.", function() {
		var definitionHasBeenCalled = false;
		testRealm.define('MyClass', function(require, exports, module) {
			definitionHasBeenCalled = true;
			module.exports = function MyClass() {};
		});
		expect(definitionHasBeenCalled).toBe(false);
	});

	it("allows one definition to require a definition defined later.", function() {
		var CLASSA = function() {};
		testRealm.define('ClassB', function(require, exports, module) {
			exports.parent = require('ClassA');
		});
		testRealm.define('ClassA', function(require, exports, module) {
			module.exports = CLASSA;
		});

		var classB = testRealm.require('ClassB');
		expect(classB.parent).toBe(CLASSA);
	});

	it("allows one definition to require another in a relative way.", function() {
		var CLASSA = function() {};
		testRealm.define("my/classes/derived/ClassB", function(require, exports, module) {
			exports.parent = require("../original/ClassA");
		});
		testRealm.define("my/classes/original/ClassA", function(require, exports, module) {
			module.exports = CLASSA;
		});

		var classB = testRealm.require('my/classes/derived/ClassB');
		expect(classB.parent).toBe(CLASSA);
	});

	it("throws an error if there is a define-time circular dependency", function() {
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
		}).toThrow(Error("Circular dependency detected: pkg/ClassA -> pkg/ClassB -> pkg/ClassA"));
	});

	it("throws an error even when a define-time dependency is partially, but not wholly, exported", function() {
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
		}).toThrow("Circular dependency detected: pkg/A -> pkg/B -> pkg/A");
	});

	it("tolerates use-time circular dependencies", function() {
		testRealm.define('pkg/ClassA', function(require, exports, module) {
			module.exports = function() {};
			require('pkg/ClassB');
		});
		testRealm.define("pkg/ClassB", function(require, exports, module) {
			module.exports = function() {};
			require('pkg/ClassA');
		});

		expect(function() {
			testRealm.require('pkg/ClassA');
		}).not.toThrow();
	});

	it("tolerates mixed circular dependencies where the required node has a use-time dependency", function() {
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

	it("tolerates mixed circular dependencies where the required node has a define-time dependency", function() {
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

		expect(mockConsole.messages.shift()).toBe("warn: Circular dependency detected: pkg/ClassB -> pkg/ClassA -> pkg/ClassB");
		expect(mockConsole.messages.shift()).toBe("info: requiring 'pkg/ClassA' early to solve the circular dependency problem");
	});

	it("tolerates large mixed circular dependencies where the required node has a define-time dependency", function() {
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

		expect(mockConsole.messages.shift()).toBe("warn: Circular dependency detected: pkg/ClassC -> pkg/ClassA -> pkg/ClassB -> pkg/ClassC");
		expect(mockConsole.messages.shift()).toBe("info: requiring 'pkg/ClassB' early to solve the circular dependency problem");
	});

	it("passes through exceptions that occur during definition", function() {
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

	it("allows the redefinition of a class in a subrealm.", function() {
		var CLASSA = function() {};
		testRealm.define("my/classes/derived/ClassB", function(require, exports, module) {
			exports.parent = require("../original/ClassA");
		});
		testRealm.define("my/classes/original/ClassA", function(require, exports, module) {
			module.exports = CLASSA;
		});

		var classB = testRealm.require('my/classes/derived/ClassB');
		expect(classB.parent).toBe(CLASSA);

		var REPLACEMENT_CLASSA = function() {};
		expect(CLASSA).not.toBe(REPLACEMENT_CLASSA);
		var subrealm = testRealm.subrealm();

		subrealm.define("my/classes/original/ClassA", function(require, exports, module) {
			module.exports = REPLACEMENT_CLASSA;
		});

		var subrealmClassB = subrealm.require('my/classes/derived/ClassB');
		expect(subrealmClassB).not.toBe(classB);
		expect(subrealmClassB.parent).toBe(REPLACEMENT_CLASSA);
	});
});
