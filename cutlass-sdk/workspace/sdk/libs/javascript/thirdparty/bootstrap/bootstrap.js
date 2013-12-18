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

	function realm(parentRequire) {
		var moduleDefinitions = {}, incompleteExports = {}, moduleExports = {}, modulesFromParent = {};

		function reset() {
			moduleDefinitions = {};
			modulesFromParent = {};
			incompleteExports = {};
			moduleExports = {};
		}

		function define(id, definition) {
			if (id in moduleDefinitions) {
				throw new Error('Module ' + id + ' has already been defined and cannot be defined a second time.  If you are sure that you want to redefine this module, you need to undefine this module or reset this realm first.');
			}
			if (modulesFromParent[id] === true) {
				throw new Error('Module ' + id + ' has already been loaded from a parent realm.  If you are sure that you want to override an already loaded parent module, you need to undefine this module or reset this realm first.');
			}
			// remove .js from the end of ids.
			id = id.replace(/\.js$/, "");
			moduleDefinitions[id] = definition;
		}

		function load(id, definitionString) {
			define(id, eval("(function(require, exports, module){\n" + definitionString + "\n});"));
		}

		function undefine(id) {
			delete incompleteExports[id];
			delete moduleExports[id];
			delete modulesFromParent[id];
			var definition = moduleDefinitions[id];
			delete moduleDefinitions[id];
			return definition;
		}

		function require(context, id) {
			var deRelativisedId = derelativise(context, id);
			// we ignore .js on the end of require requests.
			id = deRelativisedId.replace(/\.js$/, "");

			if (moduleExports[id] != null) { return moduleExports[id]; }

			if (incompleteExports[id] != null) {
				// There is a circular dependency; we do the best we can in the circumstances.
				// You should avoid doing module.exports= or returning something from the definition
				// function if your module is likely to be involved in a circular dependency since
				// the incompleteExports will be wrong in that case.
				return incompleteExports[id].exports;
			}

			var definition = moduleDefinitions[id];
			if (definition == null) {
				if (parentRequire != null) {
					var result = parentRequire(deRelativisedId);
					modulesFromParent[id] = true;
					return result;
				}
				throw new Error("No definition for module " + id + " has been loaded.");
			}

			// For closer spec compliance we should define id as a nonconfigurable, nonwritable
			// property, but this at least works OK in non-es5 browsers (like ie8).
			var module = { id: id, exports: {} };
			incompleteExports[id] = module;
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
					var returnValue = definition.call(module, require.bind(null, definitionContext), module.exports, module);
					moduleExports[id] = returnValue || module.exports;
				} else {
					// this lets you define things without definition functions, e.g.
					//    define('PI', 3); // Indiana House of Representatives compliant definition of PI
					// If you want to define something to be a function, you'll need to define a function
					// that sets module.exports to a function (or returns it).
					moduleExports[id] = definition;
				}
			} finally {
				// If there was an error, we want to run the definition again next time it is required
				// so we clean up whether it succeeded or failed.
				delete incompleteExports[id];
			}
			return moduleExports[id];
		}

		function setParentRequire(newParentRequire) {
			var modulesLoadedFromParent = Object.keys(modulesFromParent);
			if (modulesLoadedFromParent.length > 0) {
				throw new Error("Cannot modify the parent require when modules have already been loaded: " + modulesLoadedFromParent.join(", ") + ".  You will have to call .reset() to reinitialise this realm in order to change its parent.");
			}
			parentRequire = newParentRequire;
		}

		function subrealm() {
			return realm(realmExports.require);
		}

		function install(target) {
			target = target || global;
			target.define = realmExports.define;
			target.require = realmExports.require;
		}

		var realmExports =  {
			define: define, load: load,
			require: require.bind(null, ''),
			subrealm: subrealm, install: install,

			// the following can cause problems if used incautiously:
			undefine: undefine, reset: reset, setParentRequire: setParentRequire,

			// internal data it's sometimes useful to have access to:
			modules: moduleExports
		};

		realmExports.require.realm = realmExports;

		return realmExports;
	}

	var global = Function("return this;")();

	var defaultRealm = realm(global.require || function(moduleId) {
		// This is a 'require' that just returns a global.
		if (moduleId in global) {
			return global[moduleId];
		}

		// We'll try walking the object tree too.  This supports libraries that use objects for
		// namespacing.

		// Using a marker object allows us to distinguish between things that are explicitly set to
		// undefined and things that are not set.
		var NOT_FOUND = {};
		var result = global;
		var scopes = moduleId.split("/");
		for (var i = 0, length = scopes.length; i < length; ++i) {
			var scope = scopes[i];
			debugger;
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
	});

	if (typeof module !== 'undefined') {
		// for node.js
		module.exports = defaultRealm;
	} else {
		// for the browser
		global.realm = defaultRealm;
	}
	
	global.define = defaultRealm.define;
	global.require = defaultRealm.require;
})();
