'use strict';

var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');
var WritableProperty = require('br/presenter/property/WritableProperty');
var NodeField = require('br/presenter/node/Field');

function Field(sPlaceholder, vValue) {
	// call super constructor
	NodeField.call(this, vValue);

	// all of our form elements are permanently visible, so we don't need this property
	delete this.visible;

	this.placeholder = new WritableProperty(sPlaceholder);
	this.hasFocus = new EditableProperty(false);
}

Core.extend(Field, NodeField);

module.exports = Field;
