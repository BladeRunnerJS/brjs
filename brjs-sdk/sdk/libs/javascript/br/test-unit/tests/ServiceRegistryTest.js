(function() {
	'use strict';
	
	require("jsmockito");

	var ServiceRegistryTest = TestCase('ServiceRegistryTest').prototype;
	var Errors;
	var ServiceRegistry;
	var subrealm;
	var oldConsole;

	ServiceRegistryTest.setUp = function() {
		subrealm = realm.subrealm();
		subrealm.install();

		Errors = require('br/Errors');
		ServiceRegistry = require('br/ServiceRegistry');
		
		JsHamcrest.Integration.JsTestDriver();
		JsMockito.Integration.JsTestDriver();
		
		oldConsole = console;
		console = mock(console);
	};

	ServiceRegistryTest.tearDown = function() {
		console = oldConsole;
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
		assertException(function() {
			ServiceRegistry.getService('my.404-service');
		}, Errors.INVALID_PARAMETERS);
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

	ServiceRegistryTest.test_getService_WorksWithServicesRegisteredWithAliases = function() {
		window.MyService = function() {};
		var aliasRegistry = require('br/AliasRegistry');
		aliasRegistry._aliasData = {'my.service': {'class': 'MyService', 'className': 'my.Service'}};

		assertTrue(ServiceRegistry.getService('my.service') instanceof MyService);
	};
	
	ServiceRegistryTest.test_disposeCallsDisposeOnAllServices = function() {
		var serviceInterface = { dispose: function(){} };
		var mockService1 = mock(serviceInterface);
		var mockService2 = mock(serviceInterface);
		
		ServiceRegistry.registerService('mock.service.1', mockService1);
		ServiceRegistry.registerService('mock.service.2', mockService2);
		
		ServiceRegistry.dispose();

		verify(mockService1).dispose();
		verify(mockService2).dispose();
		verify(console).info("dispose() called on service registered for 'mock.service.1'");
		verify(console).info("dispose() called on service registered for 'mock.service.2'");
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
		verify(console).error("error thrown when calling dispose() on service registered for 'mock.service.1'. The error was: ERROR!");
		verify(console).info("dispose() called on service registered for 'mock.service.2'");
	};
	
	ServiceRegistryTest.test_disposeNotCalledOnServicesWhereItDoesntExist = function() {
		var serviceInterface = { };
		var mockService1 = mock(serviceInterface);
		
		ServiceRegistry.registerService('mock.service.1', mockService1);
		
		ServiceRegistry.dispose();
		
		verifyZeroInteractions(mockService1);
		verify(console).info("dispose() not called on service registered for 'mock.service.1' since no dispose() method was defined");
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
		verify(console).info("dispose() not called on service registered for 'mock.service.1' since it's dispose() method requires more than 0 arguments");
	};
	
})();
