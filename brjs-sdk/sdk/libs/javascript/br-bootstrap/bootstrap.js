(function(window) {
	"use strict";

	/** @private */
	(function(){
		var mergePackageBlock = function(context, packageBlock) {
			for (var packageName in packageBlock) {
				if(!context[packageName]) {
					context[packageName] = packageBlock[packageName];
				}
				else {
					mergePackageBlock(context[packageName], packageBlock[packageName]);
				}
			}
		}
		window.mergePackageBlock = mergePackageBlock;
	})();

	// TODO: put this into a separate 'browser-modules-compat' library
	// TODO: stop using syncImport() once tests automatically sync import on our behalf
	/** @private */
	(function(){
		var require = function(requirePath) {
			return System.syncImport(requirePath);
		}
		window.require = require;
	})();

	/** @private */
	(function(){
		var requireAll = function(require, requirePaths) {
			for (var i = 0; i < requirePaths.length; i++) {
				var requirePath = requirePaths[i];
				var namespacePath = requirePath.replace(/\//gi, ".");
				globaliseRequirePath(namespacePath, require(requirePath));
			}
		}
		window.requireAll = requireAll;
	})();

	/* private stuff */

	var globaliseRequirePath = function(namespacePath, exportedObject) {
		var namespacePathContext = getContextForNamespacePath(namespacePath);
		var namespaceKeyName = namespacePath.split(".").pop();
		namespacePathContext[namespaceKeyName] = exportedObject;
	}

	var convertToPackageBlock = function(namespacePath) {
		var namespacePathParts = namespacePath.split(".");
		namespacePathParts.pop();
		var rootContext = {};
		var currentContext = rootContext;
		for (var i = 0; i < namespacePathParts.length; i++) {
			var namespacePathPart = namespacePathParts[i];
			currentContext[namespacePathPart] = {};
			currentContext = currentContext[namespacePathPart];
		}
		return rootContext;
	}

	var getContextForNamespacePath = function(namespacePath) {
		var namespacePathParts = namespacePath.split(".");
		namespacePathParts.pop();
		var currentContext = window;
		for (var i = 0; i < namespacePathParts.length; i++) {
			var namespacePathPart = namespacePathParts[i];
			currentContext = currentContext[namespacePathPart];
		}
		return currentContext;
	}

})(window);
