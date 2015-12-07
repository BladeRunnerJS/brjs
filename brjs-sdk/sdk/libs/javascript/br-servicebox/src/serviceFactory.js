"use strict";

Object.defineProperty(exports, "__esModule", {
	value: true
});
exports.default = serviceFactory;
function serviceFactory(ServiceClass, dependencies) {
	var factory = function factory() {
		return Promise.resolve(new ServiceClass());
	};
	factory.dependencies = dependencies;

	return factory;
}