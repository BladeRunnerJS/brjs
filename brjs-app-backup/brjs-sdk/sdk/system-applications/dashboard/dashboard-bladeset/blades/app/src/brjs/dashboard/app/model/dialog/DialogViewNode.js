'use strict';

var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');
var TemplateNode = require('br/presenter/node/TemplateNode');

function DialogViewNode(sTemplateId) {
	// call super constructor
	TemplateNode.call(this, 'brjs.dashboard.app.' + sTemplateId);

	this.isClosable = new EditableProperty(true);
	this.hasBackground = new EditableProperty(true);
}

Core.extend(DialogViewNode, TemplateNode);

DialogViewNode.prototype.initializeForm = function() {
	throw new Error('DialogViewNode.initializeForm not implemented.');
};

module.exports = DialogViewNode;

