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
		var CLASSA = {};
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
		var CLASSA = {};
		testRealm.define("my/classes/derived/ClassB", function(require, exports, module) {
			exports.parent = require("../original/ClassA");
		});
		testRealm.define("my/classes/original/ClassA", function(require, exports, module) {
			module.exports = CLASSA;
		});

		var classB = testRealm.require('my/classes/derived/ClassB');
		expect(classB.parent).toBe(CLASSA);
	});

	it("allows the redefinition of a class in a subrealm.", function() {
		var CLASSA = {};
		testRealm.define("my/classes/derived/ClassB", function(require, exports, module) {
			exports.parent = require("../original/ClassA");
		});
		testRealm.define("my/classes/original/ClassA", function(require, exports, module) {
			module.exports = CLASSA;
		});

		var classB = testRealm.require('my/classes/derived/ClassB');
		expect(classB.parent).toBe(CLASSA);

		var REPLACEMENT_CLASSA = {};
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