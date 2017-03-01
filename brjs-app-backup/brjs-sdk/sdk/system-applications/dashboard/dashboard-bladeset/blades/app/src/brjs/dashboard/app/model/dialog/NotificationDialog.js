'use strict';

var Core = require('br/Core');
var WritableProperty = require('br/presenter/property/WritableProperty');
var DialogViewNode = require("brjs/dashboard/app/model/dialog/DialogViewNode");

function NotificationDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'notification-dialog');

	this.m_oPresentationModel = oPresentationModel;

	this.message = new WritableProperty();
}

Core.extend(NotificationDialog, DialogViewNode);

NotificationDialog.prototype.initializeForm = function() {
	// do nothing
};

module.exports = NotificationDialog;
