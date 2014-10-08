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
		testRealm.define("pkg/ClassA", function(require, exports, module) {
			function LocalClass() {
			};
			require("pkg/ClassB");
			module.exports = LocalClass;
		});
		testRealm.define("pkg/ClassB", function(require, exports, module) {
			exports.X = 'X';
			require("pkg/ClassA");
			exports.Y = 'Y';
		});

		expect(function() {
			testRealm.require('pkg/ClassA');
		}).toThrow(Error("Circular dependency detected: the module 'pkg/ClassA' (requested by module 'pkg/ClassB') is still in the process of exporting."));
	});

	it("we don't consider it to be a circular reference if the module has already exported at the point the circle is formed", function() {
		testRealm.define("pkg/ClassA", function(require, exports, module) {
			function LocalClass() {
			};
			module.exports = LocalClass;
			require("pkg/ClassB");
		});
		testRealm.define("pkg/ClassB", function(require, exports, module) {
			var SuperClass = require("pkg/ClassA");
			function LocalClass() {
			};
			LocalClass.prototype = new SuperClass();
			module.exports = LocalClass;
		});

		var ClassA = testRealm.require('pkg/ClassA');
		var ClassB = testRealm.require('pkg/ClassB');
		expect((new ClassB()) instanceof ClassA).toBeTruthy();
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
