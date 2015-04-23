(function() {
	"use strict";

	/*
	 * This provides a relatively simple define/require that can be used in the browser.  It's close
	 * to commonJS compliant, and allows the node style extension - module.exports = ...
	 *
	 * While it looks a little like the 'commonJS' wrapping described in the RequireJS documentation,
	 * it's synchronous.  Everything that is required must have been previously defined or an error
	 * will be thrown.
	 *
	 * There's a decent chance if you're looking at this that your needs might be better served by
	 * browserify.  Check it out if you haven't already.
	 *
	 * There are some extra features such as hierarchical realms, but to get started with the basics,
	 * include this file and then call realm.install().
	 *
	 * Warning: having a global 'define' method may be enough to trigger AMD definitions in some UMD
	 * modules. They should really check for define.AMD, but not all of them do. This will work some
	 * of the time, but this code is really designed to work with commonJS and node style modules. If
	 * this is likely to be a problem, you might want to avoid calling .install().
	 */

	// From https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Object/keys
	function objectKeys(obj) {

		var hasOwnProperty = Object.prototype.hasOwnProperty,
			hasDontEnumBug = !({ toString: null }).propertyIsEnumerable('toString'),
			dontEnums = [
				'toString',
				'toLocaleString',
				'valueOf',
				'hasOwnProperty',
				'isPrototypeOf',
				'propertyIsEnumerable',
				'constructor'
			],
			dontEnumsLength = dontEnums.length;

		if (typeof obj !== 'object' && (typeof obj !== 'function' || obj === null)) {
			throw new TypeError('Object.keys called on non-object');
		}

		var result = [], prop, i;

		for (prop in obj) {
			if (hasOwnProperty.call(obj, prop)) {
				result.push(prop);
			}
		}

		if (hasDontEnumBug) {
			for (i = 0; i < dontEnumsLength; i++) {
				if (hasOwnProperty.call(obj, dontEnums[i])) {
					result.push(dontEnums[i]);
				}
			}
		}
		return result;
	};

	var global = Function("return this;")();

	var create = Object.create || function(proto, attributes) {
		function object() {};
		object.prototype = proto;
		var result = new object();
		for (var key in attributes) {
			result[key] = attributes[key].value;
		}
		return result;
	};

	function derelativise(context, path) {
		var result = (context === "" || path.charAt(0) !== '.') ? [] : context.split("/");
		var working = path.split("/");
		var item;
		while (item = working.shift()) {
			if (item === "..") {
				result.pop();
			} else if (item !== ".") {
				result.push(item);
			}
		}
		return result.join("/");
	}

	// Using a marker object allows us to distinguish between things that are explicitly set to
	// undefined and things that are not set.
	var NOT_FOUND = {};

	// This is a 'require' that just returns a global.
	function globalResolve(moduleId) {
		if (moduleId in global) {
			return global[moduleId];
		}

		// We'll try walking the object tree too.  This supports libraries that use objects for
		// namespacing.
		var result = global;
		var scopes = moduleId.split("/");
		for (var i = 0, length = scopes.length; i < length; ++i) {
			var scope = scopes[i];
			if (scope in result) {
				result = result[scope];
			} else {
				result = NOT_FOUND;
				break;
			}
		}
		if (result !== NOT_FOUND) {
			return result;
		}
		throw new Error("No definition for module " + moduleId + " could be found in the global top level.");
	}

	function realmRequireFunc(realm, definitionContext, id) {
		return function(requirePath) {
			var activeRealm = global.activeRealm || realm;
			var exportVal;

			try {
				exportVal = activeRealm.require(definitionContext, requirePath);
			}
			catch(e) {
				if(e instanceof CircularDependencyError) {
					e.dependencies.unshift(id);

					if(e.dependencies[0] == e.dependencies[e.dependencies.length - 1]) {
						e = new Error("Circular dependency detected: " + e.dependencyChain());
					}
					else {
						if(activeRealm._isModuleExported(id)) {
							e.exportedModules[id] = true;
						}
					}
				}

				throw e;
			}

			return exportVal;
		};
	}

	function CircularDependencyError(requirePath) {
		this.dependencies = [requirePath];
		this.exportedModules = {};
		this.prototype = {};
	}

	CircularDependencyError.prototype.dependencyChain = function() {
		var message = [];

		for(var i = 0; i < (this.dependencies.length - 1); ++i) {
			var dependency = this.dependencies[i];
			message.push(dependency);
			message.push((dependency in this.exportedModules) ? '->' : '=>')
		}
		message.push(this.dependencies[this.dependencies.length - 1]);

		return message.join(' ');
	}

	function ModuleExports() {
	}

	function Realm(fallbackRequire) {
		this.moduleDefinitions = {};
		this.incompleteExports = {};
		this.moduleExports = {};
		this.modulesFromParent = {};
		this.fallbackRequire = fallbackRequire;
		this.installedData = null;
	}

	Realm.prototype.install = function install(target) {
		target = target || global;
		if (this.installedData === null) {
			this.installedData = {
				target: target,
				define: target.define,
				require: target.require,
				activeRealm: target.activeRealm
			};
			var self = this;
			target.define = function(id, definition) {
				Realm.prototype.define.apply(self, arguments);
			}
			target.require = function(context, id) {
				return Realm.prototype.require.apply(self, arguments);
			}
			target.activeRealm = this;
		} else {
			throw new Error("Can only install to one place at once.");
		}
	};

	Realm.prototype.uninstall = function uninstall() {
		if (this.installedData !== null) {
			this.installedData.target.define = this.installedData.define;
			this.installedData.target.require = this.installedData.require;
			this.installedData.target.activeRealm = this.installedData.activeRealm;
			this.installedData = null;
		}
	};

	Realm.prototype.define = function define(id, definition) {
		if (this.modulesFromParent[id] === true) {
			throw new Error('Module ' + id + ' has already been loaded from a parent realm.  If you are sure that you want to override an already loaded parent module, you need to undefine this module or reset this realm first.');
		}
		// remove .js from the end of ids.
		id = id.replace(/\.js$/, "");
		if (id in this.moduleDefinitions) {
			throw new Error('Module ' + id + ' has already been defined and cannot be defined a second time.  If you are sure that you want to redefine this module, you need to undefine this module or reset this realm first.');
		}

		this.moduleDefinitions[id] = definition;
	};

	Realm.prototype.load = function load(id, definitionString) {
		define(id, eval("(function(require, exports, module){\n" + definitionString + "\n});"));
	};

	Realm.prototype.require = function require(context, id) {
		if (arguments.length === 1) {
			id = arguments[0];
			context = '';
		}

		var originalId = id;
		// we ignore .js on the end of require requests.
		id = derelativise(context, id).replace(/\.js$/, "");

		if (this.moduleExports[id] != null) {
			// the module has already been exported
			return this.moduleExports[id];
		}
		else if (this.incompleteExports[id] != null) {
			// the module is in the process of being exported
			if(this._isModuleExported(id)) {
				return this.incompleteExports[id].exports;
			}
			else {
				throw new CircularDependencyError(id);
			}
		}

		var definition = this._getDefinition(id);
		if (definition == null) {
			if (this.fallbackRequire !== null) {
				var result = this.fallbackRequire(originalId);
				this.modulesFromParent[id] = true;
				return result;
			}
			throw new Error("No definition for module " + id + " has been loaded.");
		}

		// For closer spec compliance we should define id as a nonconfigurable, nonwritable
		// property, but this at least works OK in non-es5 browsers (like ie8).
		var module = { id: id, exports: new ModuleExports() };
		this.incompleteExports[id] = module;
		try {
			if (typeof definition === 'function') {
				var idx = id.lastIndexOf("/");
				// At the top level the context is the module id, at other levels, the context is the
				// path to the module. This is because we assume that everything at the top level is
				// a directory module and everything else is a file module.
				var definitionContext = id;
				if (idx >= 0) {
					definitionContext = id.substring(0, id.lastIndexOf("/"));
				}
				var requireFunc = realmRequireFunc(this, definitionContext, id);
				var returnValue = definition.call(module, requireFunc, module.exports, module);
				definition = returnValue || module.exports;
			}

			if(!module.preventCaching) {
				this.moduleExports[id] = definition;
			}
		}
		catch(e) {
			// this is here to slightly improve the dev experience when debugging exceptions that occur within this try/finally block.
			// see <http://blog.hackedbrain.com/2009/03/28/ie-javascript-debugging-near-useless-when-trycatchfinally-is-used/> for more information.
			throw e;
		}
		finally {
			// If there was an error, we want to run the definition again next time it is required
			// so we clean up whether it succeeded or failed.
			delete this.incompleteExports[id];
		}

		return definition;
	};

	Realm.prototype.subrealm = function(fallbackRequire) {
		return new SubRealm(this, fallbackRequire);
	};

	Realm.prototype._getDefinition = function(id) {
		return this.moduleDefinitions[id];
	};

	Realm.prototype._isModuleExported = function(id) {
		var moduleExports = this.incompleteExports[id].exports;
		return ((typeof(moduleExports) != 'object') || !(moduleExports instanceof ModuleExports) ||
			(objectKeys(moduleExports).length > 0));
	};

	// Subrealm ////////////////////////////////////////////////////////////////////////////

	/*
	 * Subrealms are for testing.  A subrealm is a brand new realm that will fallback to
	 * taking definitions from its parent realm if no definition is defined.
	 */
	function SubRealm(parentRealm, fallbackRequire) {
		Realm.call(this, fallbackRequire || parentRealm.fallbackRequire);
		this.parentRealm = parentRealm;
	}

	SubRealm.prototype = create(Realm.prototype, {
		constructor: {value: SubRealm, enumerable: false, configurable: true, writable: true}
	});

	SubRealm.prototype.recast = function(id) {
		var deltaId = id + '^';
		this.define(deltaId, this.parentRealm._getDefinition(id));
		return this.require(deltaId);
	};

	SubRealm.prototype._getDefinition = function(id, originalId) {
		return this.moduleDefinitions[id] || this.parentRealm._getDefinition(id, originalId);
	};

	// initialisation //////////////////////////////////////////////////////////////////////

	var defaultRealm = new Realm(global.require || globalResolve);

	if (typeof module !== 'undefined') {
		// for node.js
		module.exports = defaultRealm;
	} else {
		// for the browser
		global.realm = defaultRealm;
	}
})();
