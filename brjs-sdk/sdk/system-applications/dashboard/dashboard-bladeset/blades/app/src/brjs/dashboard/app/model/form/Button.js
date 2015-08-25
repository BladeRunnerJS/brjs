'use strict';

var Field = require('br/presenter/node/Field');
var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var NodeButton = require('br/presenter/node/Button');

function Button(vValue, oActionObject, sActionMethod) {
	// call super constructor
	NodeButton.call(this, vValue);

	// all of our form elements are permanently visible, so we don't need this property
	delete this.visible;

	this.tooltipLabel = new WritableProperty();
	this.tooltipVisible = new WritableProperty(false);

	this.m_oActionObject = oActionObject;
	this.m_sActionMethod = sActionMethod;
}

Core.extend(Button, Field);

Button.prototype.action = function() {
	this.m_oActionObject[this.m_sActionMethod]();
};

module.exports = Button;
