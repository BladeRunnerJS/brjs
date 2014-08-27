(function() {
	'use strict';

	var ServiceRegistryTest = TestCase('ServiceRegistryTest').prototype;

	var Errors = require('br/Errors');
	var ServiceRegistry = require('br/ServiceRegistry');

	ServiceRegistryTest.tearDown = function() {
		ServiceRegistry.clear();
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
		ServiceRegistry.clear();

		assertFalse(ServiceRegistry.isServiceRegistered('my.service'));
	};

	ServiceRegistryTest.test_getService_WorksWithServicesRegisteredWithAliases = function() {
		var AliasRegistry = require('br/AliasRegistry');
		var MyService = function() {};

		AliasRegistry._aliasData = {'my.service': {'class': MyService, 'className': 'my.Service'}};

		assertTrue(ServiceRegistry.getService('my.service') instanceof MyService);
	};
})();
