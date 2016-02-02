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
		var name = serviceName;
		var name = name.substring(0, 8) === 'service!' ? name.substring(8, name.length) : name;
		if (this._serviceBox.factories[name]) {
			continue;
		}
		this.register(serviceName);
	}
}

ServicePopulatorClass.prototype.register = function(name) {
	var serviceInfo = this._serviceData[name];

	var factory = function(requirePath) {
		return function() {
			var constructorFunction = require(requirePath);
			return Promise.resolve(new constructorFunction());
		};
	}(serviceInfo.requirePath);

	factory.dependencies = serviceInfo.dependencies;

	name = name.substring(0, 8) === 'service!' ? name.substring(8, name.length) : name;
	this._serviceBox.register(name, factory);
}

module.exports = ServicePopulatorClass;
