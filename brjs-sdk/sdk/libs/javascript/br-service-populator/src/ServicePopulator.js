
var serviceBox = require('serviceBox');

var ServicePopulator = function(serviceData){
	if (typeof serviceData === 'undefined') {
		this._serviceData = require('service!$data');
	}
	else {
		this._serviceData = serviceData;
	}
}

ServicePopulator.prototype.populate = function() {

	for (var serviceName in this._serviceData) {
		var serviceInfo = this._serviceData[serviceName];
		var serviceFactory = require(serviceInfo.requirePath);

		if (serviceFactory.dependencies === undefined) {
			serviceFactory  = function(Service) {
				return function() {
					return Promise.resolve(new Service());
				} ;
			}(serviceFactory)

			serviceFactory.dependencies = serviceInfo.dependencies; // === [] ? [] : serviceInfo.dependencies;
		}

		serviceBox.register(serviceName, serviceFactory);
	}
}

module.exports = ServicePopulator;
