(function() {
	"use strict";

	var global = (new Function("return this;"))();

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
				throw new Error('Module ' + id + ' has already been defined.');
			}
			if (modulesFromParent[id] === true) {
				throw new Error('Module ' + id + ' has already been loaded from a parent realm.');
			}
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
			// we ignore .js on the end of require requests.
			id = derelativise(context, id).replace(/\.js$/, "");

			if (moduleExports[id] != null) { return moduleExports[id]; }

			if (incompleteExports[id] != null) {
				// there is a circular dependency, we do the best we can in the circumstances.
				return incompleteExports[id].exports;
			}

			var definition = moduleDefinitions[id];
			if (definition == null) {
				if (parentRequire != null) {
					var result = parentRequire(id);
					modulesFromParent[id] = true;
					return result;
				}
				throw new Error("No definition for module " + id + " has been loaded.");
			}

			var module = { id: id, exports: {} };
			incompleteExports[id] = module;
			try {
				if (typeof definition === 'function') {
					var idx = id.lastIndexOf("/");
					var definitionContext = id;
					if (idx >= 0) {
						definitionContext = id.substring(0, id.lastIndexOf("/"));
					}
					var returnValue = definition.call(module, require.bind(null, definitionContext), module.exports, module);
					moduleExports[id] = returnValue || module.exports;
				} else {
					moduleExports[id] = definition;
				}
			} finally {
				delete incompleteExports[id];
			}
			return moduleExports[id];
		}

		function setParentRequire(newParentRequire) {
			var modulesLoadedFromParent = Object.keys(modulesFromParent);
			if (modulesLoadedFromParent.length > 0) {
				throw new Error("Cannot modify the parent require when modules have already been loaded: " + modulesLoadedFromParent.join(", "));
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

	var defaultRealm = realm(global.require || function(moduleId) {
		if (moduleId in global) {
			return global[moduleId];
		}
		throw new Error("No definition for module " + moduleId + " could be found in the global top level.");
	});

	if (typeof module !== 'undefined') {
		module.exports = defaultRealm;
	} else {
		global.realm = defaultRealm;
	}
})();