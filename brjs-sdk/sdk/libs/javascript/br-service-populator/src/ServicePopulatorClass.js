'use strict';

function ServicePopulatorClass(serviceBox, serviceData) {
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

function normalizeName(name) {
	return name.substring(0, 8) === 'service!' ? name.substring(8, name.length) : name;
}

ServicePopulatorClass.prototype.populate = function() {
	var factories = this._serviceBox.factories;
	Object.keys(this._serviceData).forEach(
		function(_name) {
			var name = normalizeName(_name);
			if (typeof factories[name] !== 'undefined') {
				return;
			}
			this.register(_name)
		},
		this
	);
}

ServicePopulatorClass.prototype.register = function(name) {
	var serviceInfo = this._serviceData[name];

	var factory = function(requirePath) {
		return function() {
			var ConstructorFn = require(requirePath);
			return Promise.resolve(new ConstructorFn());
		};
	}(serviceInfo.requirePath);

	factory.dependencies = serviceInfo.dependencies;

	this._serviceBox.register(normalizeName(name), factory);
}

module.exports = ServicePopulatorClass;
