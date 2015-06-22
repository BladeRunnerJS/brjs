'use strict';

var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var TemplateNode = require('br/presenter/node/TemplateNode');

function BreadCrumb(sClass, sName, sUrl) {
	// call super constructor
	TemplateNode.call(this, 'brjs.dashboard.app.' + sClass);

	this.className = new WritableProperty(sClass);
	this.name = new WritableProperty(sName);
	this.url = new WritableProperty(sUrl);
}

Core.extend(BreadCrumb, TemplateNode);

module.exports = BreadCrumb;
