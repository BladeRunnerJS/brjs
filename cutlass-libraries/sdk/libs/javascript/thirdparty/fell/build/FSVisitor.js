// Visits every file in a directory tree, emitting events for them.

var fs = require('fs');
var events = require('events');

function FSVisitor(emitFileContent) {
	events.EventEmitter.call(this);
	this.emitFileContent = emitFileContent;
}

FSVisitor.prototype = Object.create(events.EventEmitter.prototype, {
	constructor: {value: FSVisitor}
});

FSVisitor.prototype.visit = function(dir, name) {
	var fullPath = fs.realpathSync(dir + "/" + name);
	var stat = fs.statSync(fullPath);

	if (stat.isDirectory() === false) {
		if (this.emitFileContent) {
			this.emit("file", dir, name, stat, fs.readFileSync(fullPath, {encoding: 'utf8'}));
		} else {
			this.emit("file", dir, name, stat);
		}
	} else {
		this.emit("directory", dir, name, stat);

		var files = fs.readdirSync(fullPath);
		for (var i = 0; i < files.length; ++i) {
			var childName = files[i];
			this.visit(fullPath, childName);
		}
	}
};

module.exports = FSVisitor;
