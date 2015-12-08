require('jasmine');

describe('service populator', function() {

	var flag;
	var ServiceA = require('br/servicepopulator/ServiceA');
	var ServiceB = require('br/servicepopulator/ServiceB');
	var servicePopulator;

	var serviceData = {
		"serviceA": {
			"dependencies": [],
			"requirePath": "br/servicepopulator/ServiceA"
		},
		"serviceB": {
			"dependencies": ["serviceA", "serviceC"],
			"requirePath": "br/servicepopulator/ServiceB"
		},
		"serviceC": {
			"dependencies": [],
			"requirePath": "br/servicepopulator/ServiceC"
		}
	};

	beforeEach(function() {
		flag = false;
		var ServicePopulator = require('br/servicepopulator/ServicePopulator');
		servicePopulator = new ServicePopulator(serviceData);
		servicePopulator.populate();
	});

	afterEach(function() {
		serviceBox.factories = {};
		serviceBox.services = {};
	});

	it('can retrieve services with no dependencies', function() {
		var serviceA;

		runs(function() {
			serviceBox.resolve(["serviceA"]).then(function() {
				serviceA = serviceBox.get("serviceA");
				flag = true;
			});
		});

		waitsFor(function() {
			return flag;
		}, "The Value should be incremented", 500);

		runs(function() {
			expect(serviceA instanceof ServiceA).toBe(true);
		});
	});

	it('can retrieve services with dependencies', function() {
		var serviceB;

		runs(function() {
			serviceBox.resolve(["serviceB"]).then(function() {
				serviceB = serviceBox.get("serviceB");
				flag = true;
			});
		});

		waitsFor(function() {
			return flag;
		}, "The Value should be incremented", 500);

		runs(function() {
			expect(serviceB instanceof ServiceB).toBe(true);
		});
	});

	it('fails to return services not registered', function() {
		var unregisteredService;

		runs(function() {
			serviceBox.resolveAll().then(function() {
				try {
					unregisteredService = serviceBox.get("unregisteredService");
				} catch (e) {
					flag = true;
				}
			});
		});

		waitsFor(function() {
			return flag;
		}, "The Value should be incremented", 500);

		runs(function() {
			expect(unregisteredService).toBe(undefined);
		});
	});

});