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

	function Realm(fallbackRequire) {
		this.moduleDefinitions = {};
		this.incompleteExports = {};
		this.moduleExports = {};
		this.modulesFromParent = {};
		this.fallbackRequire = fallbackRequire;
		this.installedData = null;
		
		var realm = this;
		this.require = function() {
			return realm._require.apply(realm, arguments);
		};
		this.define = function() {
			realm._define.apply(realm, arguments);
		};
	}

	Realm.prototype.install = function install(target) {
		target = target || global;
		if (this.installedData === null) {
			this.installedData = {
				target: target,
				define: target.define,
				require: target.require
			};
			target.define = this.define;
			target.require = this.require;
		} else {
			throw new Error("Can only install to one place at once.");
		}
	};

	Realm.prototype.uninstall = function uninstall() {
		if (this.installedData !== null) {
			this.installedData.target.define = this.installedData.define;
			this.installedData.target.require = this.installedData.require;
			this.installedData = null;
		}
	};

	Realm.prototype._define = function define(id, definition) {
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

	Realm.prototype._require = function require(context, id) {
		if (arguments.length === 1) {
			id = arguments[0];
			context = '';
		}

		var originalId = id;
		// we ignore .js on the end of require requests.
		id = derelativise(context, id).replace(/\.js$/, "");

		// Has already been instantiated
		if (this.moduleExports[id] != null) {
			return this.moduleExports[id];
		}

		if (this.incompleteExports[id] != null) {
			// There is a circular dependency; we do the best we can in the circumstances.
			// You should avoid doing module.exports= or returning something from the definition
			// function if your module is likely to be involved in a circular dependency since
			// the incompleteExports will be wrong in that case.
			return this.incompleteExports[id].exports;
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
		var module = { id: id, exports: {} };
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
				// this is set to the module inside the definition code.
				var returnValue = definition.call(module, function(requirePath) {
					return window.require(definitionContext, requirePath);
				}, module.exports, module);
				this.moduleExports[id] = returnValue || module.exports;
			} else {
				// this lets you define things without definition functions, e.g.
				//    define('PI', 3); // Indiana House of Representatives compliant definition of PI
				// If you want to define something to be a function, you'll need to define a function
				// that sets module.exports to a function (or returns it).
				this.moduleExports[id] = definition;
			}
		} finally {
			// If there was an error, we want to run the definition again next time it is required
			// so we clean up whether it succeeded or failed.
			delete this.incompleteExports[id];
		}
		return this.moduleExports[id];
	};

	Realm.prototype.subrealm = function(fallbackRequire) {
		return new SubRealm(this, fallbackRequire);
	};

	Realm.prototype._getDefinition = function(id) {
		return this.moduleDefinitions[id];
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