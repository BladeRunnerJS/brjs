// This script will take the node-style code in lib and generate
//    1. a single file suitable for including from a browser script tag
//    2. a directory structure of wrapped files, that should work with RequireJS's 'simplified'
//          CommonJS wrapper.
//    3. a single file containing concatenated wrapped files, suitable to use in the browser with
//          a simple require/define framework.

var fs = require('fs-extra');
var path = require('path');
var FSVisitor = require('./FSVisitor');
var packageJson = require('../package.json');

var libraryName = packageJson.name;
var libraryDir = path.resolve(__dirname, "..");
var mainModule =  fullPathToModuleName(packageJson.main);
var srcRoot = path.resolve(libraryDir, "lib");
var amdSimpleDir = path.join(libraryDir, "target", "amd-simple", libraryName);
var moduleSystemFileName = path.join(__dirname, "ModuleSystem.js");

// functions ///////////////////////////////////////////////////////////////////////////////////////

function fullPathToModuleName(fullPath) {
	var relativePath = path.relative(libraryDir, fullPath);
	var moduleName = libraryName + "/" + relativePath.replace(/\.js$/, '').split(path.sep).join("/");
	return moduleName;
}

// script begins ///////////////////////////////////////////////////////////////////////////////////

var result = [];
var newestFileTime = fs.statSync(moduleSystemFileName).mtime.getTime();
var moduleSystemContent = fs.readFileSync(moduleSystemFileName, { encoding: 'utf8' });

console.log("Packaging " + libraryName + "...");

// clean the target directory
fs.removeSync(path.join(libraryDir, 'target'));

// visit all the files
var tree = new FSVisitor(true);
tree.on("file", function(dir, name, stat, content) {
	// keep track of the most recently changed file
	if (stat.mtime.getTime() > newestFileTime) {
		newestFileTime = stat.mtime.getTime();
	}

	var fullPath = path.join(dir, name);
	var relativePath = path.relative(srcRoot, fullPath);
	var moduleName = fullPathToModuleName(fullPath);

	var wrappedFile = [
		"// " + relativePath + " (modified " + stat.mtime.toLocaleTimeString() + ")",
		"define(function(require, exports, module) {",
		"\t" + content.replace(/\n/g, '\n\t'),
		"});"
	];

	// output the requireJS 'simplified CommonJS wrapper' file.
	fs.outputFileSync(path.join(amdSimpleDir, relativePath), wrappedFile.join("\n"));

	// we actually want the module name in this line for the other forms.
	wrappedFile[1] = "define('" + moduleName + "', function(require, exports, module) {";

	result.push("");
	result.push(wrappedFile.join("\n"));
});
tree.visit(path.dirname(srcRoot), path.basename(srcRoot));

// Output the concatenated require/define file.

result.unshift("// " + new Date(newestFileTime).toISOString());
result.unshift("// " + packageJson.name + " v" + packageJson.version + " packaged for the browser.");
fs.outputFileSync(path.join(libraryDir, 'target', 'define-single', libraryName + ".js"), result.join("\n")
		+ "\n\ndefine('" + libraryName + "', function(require, exports, module) { module.exports = require('"+  mainModule +"');});");

// Output a version of the concatenated file suitable to include in the browser.

result.unshift(fs.readFileSync(path.join(__dirname, "definitionFunction.js"), {encoding: 'utf8'}) +
		"('" + libraryName +"', function() {",
		"\t" + moduleSystemContent.replace(/\n/g, '\n\t'));
result.push("");
result.push("\treturn require('./" + fullPathToModuleName(packageJson.main) + "');");
result.push("});");
fs.outputFileSync(path.join(libraryDir, 'target', 'single', libraryName + ".js"), result.join("\n"));