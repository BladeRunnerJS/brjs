"use strict";

// imports
var fs = require('fs-extra');
var path = require('path');
var webbuild = require('webbuilder');

// fields
var now = new Date();
var libraryDir = path.resolve(__dirname, "..");
var moduleName = require('../package.json').name || path.basename(libraryDir);

// clean the target directory
fs.removeSync(path.join(libraryDir, 'target'));

// Build a standalone package
var singleOut = path.join(libraryDir, "/target/single/"+moduleName+".js");
fs.mkdirpSync(path.dirname(singleOut));
webbuild(libraryDir, {
	out: singleOut,
	prefix: "// "+ moduleName + " built for browser " + now.toISOString() + "\n",
	includeSystem: true,
	withDependencies: true
});

// Build a bundle that expects there to already be a require/define
var systemOut = path.join(libraryDir, "/target/system/"+moduleName+".js");
fs.mkdirpSync(path.dirname(systemOut));
webbuild(libraryDir, {
	out: systemOut,
	prefix: "// "+ moduleName + " built for bundle module system " + now.toISOString() + "\n",
	includeSystem: false,
	withDependencies: false
});