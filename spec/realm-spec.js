describe("a realm", function() {

	var global = Function('return this')();
	var globalRealm = global.realm || require('..');
	var Realm = globalRealm.constructor;

	var testRealm;

	beforeEach(function() {
		testRealm = new Realm();
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
		testRealm.define("ClassB", function(require, exports, module) {
			exports.parent = require("ClassA");
		});
		testRealm.define("ClassA", function(require, exports, module) {
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

	it("throws an error if there is a circular reference", function() {
		var CLASSA = function() {};
		testRealm.define("ClassA", function(require, exports, module) {
			require("ClassB");
			module.exports = CLASSA;
		});
		var CLASSB = function() {};
		testRealm.define("ClassB", function(require, exports, module) {
			require("ClassA");
			module.exports = CLASSB;
		});

		expect(function() {
			testRealm.require('ClassA');
		}).toThrow(Error("Circular dependency detected: the module 'ClassA' (requested by module 'ClassB') is still in the process of exporting."));
	});

	it("we consider it to be a circular reference even if a module has partially exported", function() {
		var CLASSA = function() {};
		testRealm.define("ClassA", function(require, exports, module) {
			require("ClassB");
			module.exports = CLASSA;
		});
		testRealm.define("ClassB", function(require, exports, module) {
			exports.X = 'X';
			require("ClassA");
			exports.Y = 'Y';
		});

		expect(function() {
			testRealm.require('ClassA');
		}).toThrow(Error("Circular dependency detected: the module 'ClassA' (requested by module 'ClassB') is still in the process of exporting."));
	});

	it("we don't consider it to be a circular reference if the module has already exported at the point the circle is formed", function() {
		testRealm.define("ClassA", function(require, exports, module) {
			function LocalClass() {
			};
			module.exports = LocalClass;
			require("ClassB");
		});
		testRealm.define("ClassB", function(require, exports, module) {
			var SuperClass = require("ClassA");
			function LocalClass() {
			};
			LocalClass.prototype = new SuperClass();
			module.exports = LocalClass;
		});

		var ClassA = testRealm.require('ClassA');
		var ClassB = testRealm.require('ClassB');
		expect((new ClassB()) instanceof ClassA).toBeTruthy();
	});

	it("a simple object required from a subrealm is unique to the subrealm.", function() {
		testRealm.define("my/classes/ClassA", function(require, exports, module) {
			function test() {}
			test.prototype.foo = function() {};
			module.exports = test;
		});

		var ClassA = testRealm.require('my/classes/ClassA');

		var subrealm = testRealm.subrealm();

		var SubrealmClassA = subrealm.require('my/classes/ClassA');
		expect(SubrealmClassA).not.toBe(ClassA);
	});

	it("a complex object (with single dependency) required from a subrealm is unique to the subrealm (including its dependency)", function() {
		testRealm.define('my/classes/Dependency', function(require, exports, module) {
			function Dependency() {}
			module.exports = Dependency;
		});

		testRealm.define('my/classes/ClassA', function(require, exports, module) {
			var Dependency = require('my/classes/Dependency');

			function test() {
				this.dependency = Dependency;
			}
			module.exports = test;
		});

		var ClassA = testRealm.require('my/classes/ClassA');

		var subrealm = testRealm.subrealm();

		var SubrealmClassA = subrealm.require('my/classes/ClassA');

		expect(SubrealmClassA).not.toBe(ClassA);

		var classA = new ClassA();
		var otherClassA = new ClassA();

		expect(classA.dependency).toEqual(otherClassA.dependency);
		var subrealmClassA = new SubrealmClassA();

		expect(classA.dependency).not.toEqual(subrealmClassA.dependency);
	});

	it("a complex object (with dependencies) required from a subrealm is unique to the subrealm (including its dependency chain)", function() {

		testRealm.define('my/classes/SubDependency', function(require, exports, module) {
			function SubDependency() {}
			SubDependency.prototype.baz = function() { return 'sub-dep'; };
			module.exports = SubDependency;
		});

		testRealm.define('my/classes/Dependency', function(require, exports, module) {
			var SubDependency = require('my/classes/SubDependency');
			function Dependency() {
				this.subDependency = new SubDependency();
			}
			Dependency.prototype.bar = function() { return 'dep'; };
			module.exports = Dependency;
		});

		testRealm.define('my/classes/ClassA', function(require, exports, module) {
			var Dependency = require('my/classes/Dependency');
			function ClassA() {
				this.dependency = new Dependency();
			}
			module.exports = ClassA;
		});

		var ClassA = testRealm.require('my/classes/ClassA');

		var classA = new ClassA();
		var otherClassA = new ClassA();

		expect(classA).toEqual(otherClassA);
		expect(classA.dependency).toEqual(otherClassA.dependency);
		expect(classA.dependency.subDependency).toEqual(otherClassA.dependency.subDependency);
		expect(classA.dependency.subDependency.baz()).toBe('sub-dep');

		// start subrealm
		var subrealm = testRealm.subrealm();

		subrealm.define('my/classes/SubDependency', function(require, exports, module) {
			function SubDependency() {
			}

			SubDependency.prototype.baz = function() { return 'sub-dep-changed'; };
			module.exports = SubDependency;
		});

		var SubrealmClassA = subrealm.require('my/classes/ClassA');

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
