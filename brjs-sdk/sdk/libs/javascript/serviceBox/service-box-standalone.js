(function(f) {
	if (typeof exports === "object" && typeof module !== "undefined") {
		module.exports = new (f().default)()
	} else if (typeof define === "function" && define.amd) {
		define([], f)
	} else {
		var g;
		if (typeof window !== "undefined") {
			g = window
		} else if (typeof global !== "undefined") {
			g = global
		} else if (typeof self !== "undefined") {
			g = self
		} else {
			g = this
		}
		g.serviceBox = new (f().default)()
	}
})(function() {
	var define, module, exports;
	return (function e(t, n, r) {
		function s(o, u) {
			if (!n[o]) {
				if (!t[o]) {
					var a = typeof require == "function" && require;
					if (!u && a) return a(o, !0);
					if (i) return i(o, !0);
					var f = new Error("Cannot find module '" + o + "'");
					throw f.code = "MODULE_NOT_FOUND", f
				}
				var l = n[o] = {
					exports: {}
				};
				t[o][0].call(l.exports, function(e) {
					var n = t[o][1][e];
					return s(n ? n : e)
				}, l, l.exports, e, t, n, r)
			}
			return n[o].exports
		}
		var i = typeof require == "function" && require;
		for (var o = 0; o < r.length; o++) s(r[o]);
		return s
	})({
		1: [function(require, module, exports) {
			"use strict";

			var _createClass = (function() {
				function defineProperties(target, props) {
					for (var i = 0; i < props.length; i++) {
						var descriptor = props[i];
						descriptor.enumerable = descriptor.enumerable || false;
						descriptor.configurable = true;
						if ("value" in descriptor) descriptor.writable = true;
						Object.defineProperty(target, descriptor.key, descriptor);
					}
				}
				return function(Constructor, protoProps, staticProps) {
					if (protoProps) defineProperties(Constructor.prototype, protoProps);
					if (staticProps) defineProperties(Constructor, staticProps);
					return Constructor;
				};
			})();

			Object.defineProperty(exports, "__esModule", {
				value: true
			});

			function _classCallCheck(instance, Constructor) {
				if (!(instance instanceof Constructor)) {
					throw new TypeError("Cannot call a class as a function");
				}
			}

			/*eslint no-loop-func: 0*/

			function resolveNextService(servicesToResolve, factories, services) {
				var promises = [];
				var i = 0;

				var _loop = function _loop() {
					var name = servicesToResolve[i++];
					var factory = factories[name];

					if (!services[name]) {
						var unmetDependencies = factory.dependencies.filter(function(dep) {
							return !services[dep];
						});
						var dependenciesMet = unmetDependencies.length === 0;

						if (!dependenciesMet) {
							var newDependencies = unmetDependencies.filter(function(dep) {
								return servicesToResolve.indexOf(dep) === -1;
							});
							servicesToResolve = servicesToResolve.concat(newDependencies);
						} else {
							promises.push(factory().then(function(service) {
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

			var ServiceBox = (function() {
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
							return promise.then(function() {
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

		}, {}],
		2: [function(require, module, exports) {
			'use strict';

			Object.defineProperty(exports, "__esModule", {
				value: true
			});

			var _ServiceBox = require('./ServiceBox.js');

			Object.defineProperty(exports, 'default', {
				enumerable: true,
				get: function get() {
					return _ServiceBox.default;
				}
			});

			var _serviceFactory = require('./serviceFactory.js');

			Object.defineProperty(exports, 'serviceFactory', {
				enumerable: true,
				get: function get() {
					return _serviceFactory.default;
				}
			});

		}, {
			"./ServiceBox.js": 1,
			"./serviceFactory.js": 3
		}],
		3: [function(require, module, exports) {
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

		}, {}]
	}, {}, [2])(2)
});
