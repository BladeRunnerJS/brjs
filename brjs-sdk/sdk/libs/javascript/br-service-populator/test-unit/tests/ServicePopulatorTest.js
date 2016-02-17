
require('jasmine');

describe('service populator', function() {

	var flag;
	var ServiceA = require('br/servicepopulator/ServiceA');
	var ServiceB = require('br/servicepopulator/ServiceB');

	var ServicePopulatorClass = require('br/servicepopulator/ServicePopulatorClass');
	var servicePopulator;

	var ServiceBoxClass = require('br/servicebox/ServiceBoxClass');
	var serviceBox = new ServiceBoxClass();

	var serviceData = {
		'serviceA': {
			'dependencies': [],
			'requirePath': 'br/servicepopulator/ServiceA'
		},
		'serviceB': {
			'dependencies': ['serviceA', 'serviceC'],
			'requirePath': 'br/servicepopulator/ServiceB'
		},
		'serviceC': {
			'dependencies': [],
			'requirePath': 'br/servicepopulator/ServiceC'
		}
	};

	beforeEach(function() {
		flag = false;
		serviceBox = new ServiceBoxClass();
		servicePopulator = new ServicePopulatorClass(serviceBox, serviceData);
	});

	it('populates the services with no dependencies correctly', function() {
		servicePopulator.populate();

		var serviceA;

		runs(function() {
			serviceBox.resolve(['serviceA']).then(function() {
				serviceA = serviceBox.get('serviceA');
				flag = true;
			});
		});

		waitsFor(function() {
			return flag;
		}, 'The flag should be true', 500);

		runs(function() {
			expect(serviceA instanceof ServiceA).toBe(true);
		});
	});

	it('populates the services with dependencies correctly', function() {
		servicePopulator.populate();

		var serviceB;

		runs(function() {
			serviceBox.resolve(['serviceB']).then(function() {
				serviceB = serviceBox.get('serviceB');
				flag = true;
			});
		});

		waitsFor(function() {
			return flag;
		}, 'The flag should be true', 500);

		runs(function() {
			expect(serviceB instanceof ServiceB).toBe(true);
		});
	});

	it('fails to return unregistered services', function() {
		servicePopulator.populate();

		var unregisteredService;

		runs(function() {
			serviceBox.resolveAll().then(function() {
				try {
					unregisteredService = serviceBox.get('unregisteredService');
				} catch (e) {
					flag = true;
				}
			});
		});

		waitsFor(function() {
			return flag;
		}, 'The flag should be true', 500);

		runs(function() {
			expect(unregisteredService).toBe(undefined);
		});
	});

	it('fails to return services when the dependencies have not been resolved', function() {
		servicePopulator.populate();

		var getServiceA = function() {
			serviceBox.get('serviceA');
			flag = true;
		}

		expect(getServiceA).toThrow();
	});

	it ('fails to return services when the populate method has not been called', function() {

		var getServiceA = function() {
			serviceBox.resolve(['serviceA']).then(function() {
				serviceA = serviceBox.get('serviceA');
			});
		};

		expect(getServiceA).toThrow();
	});

});
