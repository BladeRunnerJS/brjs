(function() {
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

	// having multiple realms would allow you to load
	// two different versions of the same module.
	function realm(parentRequire) {
		var moduleDefinitions = {};
		var incompleteExports = {};
		var moduleExports = {};

		function define(id, definition) {
			if (id in moduleDefinitions) {
				throw new Error('Module ' + id + ' has already been defined.');
			}
			moduleDefinitions[id] = definition;
		}

		function require(context, id) {
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

			var module = { exports: {}, id: id };
			incompleteExports[id] = module;
			try {
				if (typeof definition === 'function') {
					var definitionContext = id;
					var idx = id.lastIndexOf("/");
					if (idx >= 0) {
						definitionContext = id.substring(0, idx);
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

		require.modules = moduleExports;
		
		return {
			define: define,
			require: require
		};
	}

	var global = (new Function("return this;"))();
	var defaultRealm = realm();

	global.define = defaultRealm.define;
	global.require = defaultRealm.require.bind(null, "");
})();