function defineTestCase(name, setUp, tearDown) {

	var testCase = TestCase(name);
	testCase.prototype.setUp = setUp;
	testCase.prototype.tearDown = tearDown;
	
	var definitionErrors = [];

	var aliases = {};

	function addDefinitionError(message) {
		definitionErrors.push(message);
		if (testCase.prototype["test definition errors"] == null) {
			testCase.prototype["test definition errors"] = function() {
				fail(definitionErrors.join("\n"));
			}
		}
	}
	
	testCase.prototype.continuesFrom = function(testName) {
		var realTestName = aliases[testName] || testName;
		if ( ! testCase.prototype[realTestName]) {
			throw new Error("Test not found "+testName + " ("+realTestName+").");
		}
		try {
			testCase.prototype[realTestName].call(this);
		} catch (e) {
			if (e.name != "DependedTestFailed") {
				e = new Error("Unable to continue because depended on test "+testName+" failed.");
				e.name = "DependedTestFailed";
				e.stack = "";
			}
			throw e;
		}
		if (window["JsMockito"] && window["JsMockito"].clearAllMocks) {
			JsMockito.clearAllMocks();
		}
	};

	function testThat(mainTestName, testCode) {
		var realTestName = "test that "+mainTestName;
		if (testCase.prototype[realTestName] != null) {
			addDefinitionError("Can't name test '"+mainTestName+"' as a test with that name is already defined.");
		} else {
			testCase.prototype[realTestName] = testCode;
		}

		function aliasFunc(aliasName) {
			if (aliases[aliasName] != null) {
				addDefinitionError("Can't alias test '"+mainTestName+"' to '"+aliasName+"' as that alias is already defined.");
			} else {
				aliases[aliasName] = realTestName;
			}
			return aliasFunc;
		}
		
		return {
			alsoKnownAs: aliasFunc
		};
	}

	return {
		setup: function(setupCode) {
			testCase.prototype.setUp = setupCode;
		},
		tearDown: function(tearDownCode) {
			testCase.prototype.tearDown = tearDownCode;
		},
		set: function(name, value) {
			testCase.prototype[name] = value;
		},
		testThat: testThat
	};
}
