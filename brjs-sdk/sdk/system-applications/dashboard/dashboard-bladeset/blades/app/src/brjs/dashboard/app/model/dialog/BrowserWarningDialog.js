'use strict';

var Core = require('br/Core');
var EditableProperty = require('br/presenter/property/EditableProperty');
var DialogViewNode = require("brjs/dashboard/app/model/dialog/DialogViewNode");

function BrowserWarningDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'browser-warning-dialog');

	this.isClosable.setValue(false);
	this.hasBackground.setValue(false);
	this.browserVersionsHtml = new EditableProperty('');
	this.m_pMinimumBrowserVersions = null;
}

Core.extend(BrowserWarningDialog, DialogViewNode);

BrowserWarningDialog.prototype.initializeForm = function() {
	// do nothing
};

BrowserWarningDialog.prototype.setMinimumBrowserVersions = function(pMinimumBrowserVersions) {
	this.m_pMinimumBrowserVersions = pMinimumBrowserVersions;
	var sVersionsHtml = '<ul>';
	for (var browser in pMinimumBrowserVersions) {
		var browserVersion = pMinimumBrowserVersions[browser];
		sVersionsHtml += "<li><span class='browser'>" + browser + "</span><span class='browserVersion'>" + browserVersion + '</span></li>';
	}
	sVersionsHtml += '</ul>';
	this.browserVersionsHtml.setValue(sVersionsHtml);
};

module.exports = BrowserWarningDialog;
