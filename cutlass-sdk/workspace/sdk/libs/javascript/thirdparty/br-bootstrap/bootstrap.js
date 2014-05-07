(function(window) {
	"use strict";

	window.mergePackageBlock = function(context, packageBlock) {
		for (var packageName in packageBlock) {
			if(!context[packageName]) {
				context[packageName] = packageBlock[packageName];
			}
			else {
				mergePackageBlock(context[packageName], packageBlock[packageName]);
			}
		}
	}

	window.requireAll = function(requirePaths) {
		for (var i = 0; i < requirePaths.length; i++) {
			var requirePath = requirePaths[i];
			var namespacePath = requirePath.replace(/\//gi, ".");
			mergePackageBlock( window, convertToPackageBlock(namespacePath) );
			globaliseRequirePath(requirePath, namespacePath);
		}
	}



	/* private stuff */

	var globaliseRequirePath = function(requirePath, namespacePath) {
		var namespacePathContext = getContextForNamespacePath(namespacePath);
		var namespaceKeyName = namespacePath.split(".").pop();
		namespacePathContext[namespaceKeyName] = require(requirePath);
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
