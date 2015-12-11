var ServicePopulatorClass = function(serviceBox, serviceData) {
	if (typeof serviceData == 'undefined') {
		this._serviceData = require('service!$data');
	} else {
		this._serviceData = serviceData;
	}
	if (typeof serviceBox == 'undefined') {
		this._serviceBox = require('br/servicebox/serviceBox');
	} else {
		this._serviceBox = serviceBox;
	}
}

ServicePopulatorClass.prototype.populate = function() {

	for (var serviceName in this._serviceData) {
		var serviceInfo = this._serviceData[serviceName];
		var serviceFactory = require(serviceInfo.requirePath);
		this.register(serviceName, serviceFactory);
	}
}

ServicePopulatorClass.prototype.register = function(name, factory) {
	var serviceInfo = this._serviceData[name];

	if (factory.dependencies === undefined) {
		factory = function(ConstructorFunction) {
			return function() {
				return Promise.resolve(new ConstructorFunction());
			};
		}(factory)

		factory.dependencies = serviceInfo.dependencies;
	}

	name = name.substring(0, 8) === 'service!' ? name.substring(8, name.length) : name;
	this._serviceBox.register(name, factory);
}

module.exports = ServicePopulatorClass;