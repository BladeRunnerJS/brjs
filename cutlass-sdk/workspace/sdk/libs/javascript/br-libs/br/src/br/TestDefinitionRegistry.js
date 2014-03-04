"use strict";

var TestDefinitionRegistry = function() {
	this.overriddenDefinitions = {};
	this.origRequire = window.require;
};

TestDefinitionRegistry.install = function() {
	var testDefinitionRegistry = new TestDefinitionRegistry();
	realm.moduleExports = {};
	window.require = testDefinitionRegistry._require.bind(testDefinitionRegistry);
	
	return testDefinitionRegistry;
};

TestDefinitionRegistry.prototype.uninstall = function() {
	window.require = this.origRequire;
};

TestDefinitionRegistry.prototype.define = function(requirePath, vExportedObject) {
	this.overriddenDefinitions[requirePath] = vExportedObject;
};

TestDefinitionRegistry.prototype._require = function(context, requirePath) {
	if (arguments.length === 1) {
		requirePath = arguments[0];
		context = '';
	}
	
	var exportedObject = this.overriddenDefinitions[requirePath];
	
	if(exportedObject === undefined) {
		exportedObject = this.origRequire(context, requirePath);
	}
	
	return exportedObject;
};

module.exports = TestDefinitionRegistry;
