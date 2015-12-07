"use strict";

var _createClass = (function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; })();

Object.defineProperty(exports, "__esModule", {
	value: true
});

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/*eslint no-loop-func: 0*/

function resolveNextService(servicesToResolve, factories, services) {
	var promises = [];
	var i = 0;

	var _loop = function _loop() {
		var name = servicesToResolve[i++];
		var factory = factories[name];

		if (!services[name]) {
			var unmetDependencies = factory.dependencies.filter(function (dep) {
				return !services[dep];
			});
			var dependenciesMet = unmetDependencies.length === 0;

			if (!dependenciesMet) {
				var newDependencies = unmetDependencies.filter(function (dep) {
					return servicesToResolve.indexOf(dep) === -1;
				});
				servicesToResolve = servicesToResolve.concat(newDependencies);
			} else {
				promises.push(factory().then(function (service) {
					services[name] = service;
				}));
			}
		}
	};

	while (i < servicesToResolve.length) {
		_loop();
	}

	return promises.length === 0 ? false : Promise.all(promises);
}

var ServiceBox = (function () {
	function ServiceBox() {
		_classCallCheck(this, ServiceBox);

		this.factories = {};
		this.services = {};
	}

	_createClass(ServiceBox, [{
		key: "register",
		value: function register(name, factory) {
			if (this.factories[name]) {
				throw new Error("A factory with the name '" + name + "' has already been registered.");
			}

			if (!factory.dependencies) {
				factory.dependencies = [];
			}

			this.factories[name] = factory;
		}
	}, {
		key: "resolve",
		value: function resolve(names) {
			var _this = this;

			var promise = resolveNextService(names, this.factories, this.services);

			if (promise) {
				return promise.then(function () {
					return _this.resolve(names);
				});
			} else {
				return Promise.resolve();
			}
		}
	}, {
		key: "resolveAll",
		value: function resolveAll() {
			return this.resolve(Object.keys(this.factories));
		}
	}, {
		key: "get",
		value: function get(name) {
			if (!this.services[name]) {
				if (this.factories[name]) {
					throw new Error("The '" + name + "' service needs to be resolved before you can retrieve it.");
				} else {
					throw new Error("No service called '" + name + "' has been registered.");
				}
			}

			return this.services[name];
		}
	}]);

	return ServiceBox;
})();

exports.default = ServiceBox;