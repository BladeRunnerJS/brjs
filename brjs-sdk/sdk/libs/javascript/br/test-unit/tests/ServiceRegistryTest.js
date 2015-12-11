(function() {

	'use strict';
	require("jsmockito");
	var ServiceRegistryClass = require('br/ServiceRegistryClass');
	var ServiceRegistry;

	var ServiceBoxClass = require('br/servicebox/ServiceBoxClass')

	var ServiceRegistryTestSuite = AsyncTestCase('ServiceRegistryTest');
	var ServiceRegistryTest = ServiceRegistryTestSuite.prototype;

	var fell;
	var Errors;
	var subrealm;
	var logStore;

	ServiceRegistryTest.setUp = function() {
		subrealm = realm.subrealm();
		subrealm.install();

		fell = require("fell");
		Errors = require('br/Errors');
		ServiceRegistry = new ServiceRegistryClass(new ServiceBoxClass());

		JsHamcrest.Integration.JsTestDriver();
		JsMockito.Integration.JsTestDriver();

		logStore = mock({
			onLog: function(){}
		});
		fell.configure('debug', {}, [logStore]);
	};

	ServiceRegistryTest.tearDown = function() {
		fell.configure('warn');
		subrealm.uninstall();
	};

	ServiceRegistryTest.test_registerService_WithNoInstanceThrowsException = function() {
		try {
			ServiceRegistry.registerService('my.service', undefined);
			fail('Should throw an exception.');
		} catch (e) {
			assertSame('The service instance is undefined.', e.message);
		}
	};

	ServiceRegistryTest.test_registerService_AddingExistingServiceThrowsException = function() {
		ServiceRegistry.registerService('my.service', {});

		try {
			ServiceRegistry.registerService('my.service', {});
			fail('Should throw an exception.');
		} catch (e) {
			assertSame('Service: my.service has already been registered.', e.message);
		}
	};

	ServiceRegistryTest.test_getService_GettingNonExistantServiceThrowsException = function() {
		var getNonexistantService = function () {
			ServiceRegistry.getService('my.404-service');
		}
		assertException(getNonexistantService, Errors.INVALID_PARAMETERS);
	};

	ServiceRegistryTest.test_getService_ReturnsTheRegisteredService = function() {
		var myService = {};
		ServiceRegistry.registerService('my.service', myService);
		assertSame(ServiceRegistry.getService('my.service'), myService);
	};

	ServiceRegistryTest.test_isServiceRegistered_ReturnsTrueForRegisteredService = function() {
		ServiceRegistry.registerService('my.service', {});
		assert(ServiceRegistry.isServiceRegistered('my.service'));
	};

	ServiceRegistryTest.test_isServiceRegistered_ReturnsFalseForNotRegisteredService = function() {
		assertFalse(ServiceRegistry.isServiceRegistered('my.service'));
	};

	ServiceRegistryTest.test_deregisterService_Works = function() {
		ServiceRegistry.registerService('my.service', {});
		ServiceRegistry.deregisterService('my.service');
		assertFalse(ServiceRegistry.isServiceRegistered('my.service'));
	};

	ServiceRegistryTest.test_clear_Works = function() {
		ServiceRegistry.registerService('my.service', {});
		ServiceRegistry.legacyClear();
		assertFalse(ServiceRegistry.isServiceRegistered('my.service'));
	};

	// TODO: This test might not make sense anymore since the alias are now used with the ServicePopulator
	// ServiceRegistryTest.test_getService_WorksWithServicesRegisteredWithAliases = function() {
	// 	define('br/AliasRegistry', function(require, exports, module) {
	// 		var AliasRegistryClass = require('br/AliasRegistryClass');
	// 		module.exports = new AliasRegistryClass( {'my.service': {'class': 'MyService', 'className': 'my.Service'}} );
	// 	});
	// 	define('br/ServiceRegistry', function(require, exports, module) {
	// 		module.exports = new (require('br/ServiceRegistryClass'));
	// 	});

	// 	window.MyService = function() {};
	// 	assertTrue(require('br/ServiceRegistry').getService('my.service') instanceof MyService);
	// };

	ServiceRegistryTest.test_disposeCallsDisposeOnAllServices = function() {
		var serviceInterface = { dispose: function(){} };
		var mockService1 = mock(serviceInterface);
		var mockService2 = mock(serviceInterface);

		ServiceRegistry.registerService('mock.service.1', mockService1);
		ServiceRegistry.registerService('mock.service.2', mockService2);

		ServiceRegistry.dispose();

		verify(mockService1).dispose();
		verify(mockService2).dispose();
		verify(logStore).onLog(anything(), 'debug', [ServiceRegistryClass.LOG_MESSAGES.DISPOSE_CALLED, 'mock.service.1']);
		verify(logStore).onLog(anything(), 'debug', [ServiceRegistryClass.LOG_MESSAGES.DISPOSE_CALLED, 'mock.service.2']);
	};

	ServiceRegistryTest.test_disposeCallsDisposeOnAllServicesIfTheFirstThrowsAnError = function() {
		var serviceInterface = { dispose: function(){} };
		var mockService1 = mock(serviceInterface);
		var mockService2 = mock(serviceInterface);

		ServiceRegistry.registerService('mock.service.1', mockService1);
		ServiceRegistry.registerService('mock.service.2', mockService2);

		when(mockService1).dispose().thenThrow("ERROR!");

		ServiceRegistry.dispose();

		verify(mockService1).dispose();
		verify(mockService2).dispose();
		verify(logStore).onLog(anything(), 'error', [ServiceRegistryClass.LOG_MESSAGES.DISPOSE_ERROR, 'mock.service.1', "ERROR!"]);
		verify(logStore).onLog(anything(), 'debug', [ServiceRegistryClass.LOG_MESSAGES.DISPOSE_CALLED, 'mock.service.2']);
	};

	ServiceRegistryTest.test_disposeNotCalledOnServicesWhereItDoesntExist = function() {
		var serviceInterface = { };
		var mockService1 = mock(serviceInterface);

		ServiceRegistry.registerService('mock.service.1', mockService1);

		ServiceRegistry.dispose();

		verifyZeroInteractions(mockService1);
		verify(logStore).onLog(anything(), 'debug', [ServiceRegistryClass.LOG_MESSAGES.DISPOSE_MISSING, 'mock.service.1']);
	};

	ServiceRegistryTest.test_disposeIsOnlyCalledOnServicesThatHaveADisposeWith0Args = function() {
		var disposeCalled = false; // this has to be done with a real object rather than mocks so service.dispose.length has the correct value
		var service = {
			dispose: function(arg1) {
				disposeCalled = true;
			}
		}

		ServiceRegistry.registerService('mock.service.1', service);

		ServiceRegistry.dispose();

		assertFalse(disposeCalled);
		verify(logStore).onLog(anything(), 'info', [ServiceRegistryClass.LOG_MESSAGES.DISPOSE_0_ARG, 'mock.service.1']);
	};

})();
