
(function() {
	// JsTestDriver needs to call AliasRegistry.clear and
	// ServiceRegistry.clear before the setUp. But since
	// this happens at the moment of JsTestDriver
	// Initialization, in order to ensure this it is
	// necessary to create some side-effect. For that
	// reason this test appears to do nothing, because
	// everything happens inside JsTestDriver.

	var aliasRegistryClearHasBeenCalled = false;
	var serviceRegistryClearHasBeenCalled = false;
	var AliasRegistry = require('br/AliasRegistry');
	var ServiceRegistry = require('br/ServiceRegistry');
	var originalAliasRegistryClear = AliasRegistry.clear;
	var originalServiceRegistryClear = ServiceRegistry.clear;

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
			originalServiceRegistryClear();
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
