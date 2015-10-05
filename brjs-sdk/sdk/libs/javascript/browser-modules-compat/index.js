(function(window) {
	window.require = function(requirePath) {
		return System.syncImport(requirePath);
	};

	// TODO: add support for sub-realms, which depends on:
	// sub-realms compatibility layer <https://github.com/BladeRunnerJS/brjs/issues/1469>
	// MockableSystemJS Wrapper Library <https://github.com/BladeRunnerJS/brjs/issues/1468>
	// MockableSystemJS Library <https://github.com/BladeRunnerJS/brjs/issues/1467>
})(window);
