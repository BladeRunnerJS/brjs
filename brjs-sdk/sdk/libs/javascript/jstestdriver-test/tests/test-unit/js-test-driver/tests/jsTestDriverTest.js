
(function() {
	var aliasRegistryClearHasBeenCalled = false;
	var serviceRegistryClearHasBeenCalled = false;
	var AliasRegistry = require('br/AliasRegistry');
	var ServiceRegistry = require('br/ServiceRegistry');
	var originalAliasRegistryClear = AliasRegistry.clear;

	if (AliasRegistry.clear) {
		AliasRegistry.clear = function() {
			originalAliasRegistryClear();
			aliasRegistryClearHasBeenCalled = true;
		}
	} else {
		AliasRegistry.clear = function() {
			aliasRegistryClearHasBeenCalled = true;
		}
	}

	if (ServiceRegistry.clear) {
		ServiceRegistry.clear = function() {
			ServiceRegistry.clear();
			serviceRegistryClearHasBeenCalled = true;
		}
	} else {
		ServiceRegistry.clear = function() {
			serviceRegistryClearHasBeenCalled = true;
		}
	}

	var testCaseName = 'JsTestDriverTest';
	var testCase = {
		setUp: function() {
		},
		tearDown: function() {
		},
		'test clear methods are called': function() {
			assertTrue(aliasRegistryClearHasBeenCalled);
			assertTrue(serviceRegistryClearHasBeenCalled);
		}
	};
	return new TestCase(testCaseName, testCase);
})();
