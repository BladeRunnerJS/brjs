"use strict";

var TestDefinitionRegistry = function() {
	this.overriddenDefinitions = {};
	this.origRequire = window.require;
};

TestDefinitionRegistry.install = function() {
	var testDefinitionRegistry = new TestDefinitionRegistry();
	window.require = testDefinitionRegistry._require.bind(testDefinitionRegistry);
	
	return testDefinitionRegistry;
};

TestDefinitionRegistry.prototype.uninstall = function() {
	window.require = this.origRequire;
};

TestDefinitionRegistry.prototype.define = function(requirePath, vExportedObject) {
	this.overriddenDefinitions[requirePath] = vExportedObject;
};

TestDefinitionRegistry.prototype._require = function(requirePath) {
	var exportedObject = this.overriddenDefinitions[requirePath];
	
	if(exportedObject === undefined) {
		exportedObject = this.origRequire(requirePath);
	}
	
	return exportedObject;
};

module.exports = TestDefinitionRegistry;
