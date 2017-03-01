'use strict';

var PresentationNode = require('br/presenter/node/PresentationNode');
var MappedNodeList = require('br/presenter/node/MappedNodeList');
var EditableProperty = require('br/presenter/property/EditableProperty');
var Property = require('br/presenter/property/Property');
var WritableProperty = require('br/presenter/property/WritableProperty');
var Core = require('br/Core');
var WolfSimpleBox = require('wolf-simple-box');

var DialogViewNode = require("brjs/dashboard/app/model/dialog/DialogViewNode");
var NewAppDialog = require("brjs/dashboard/app/model/dialog/newDialog/NewAppDialog");
var NewBladesetDialog = require("brjs/dashboard/app/model/dialog/newDialog/NewBladesetDialog");
var NewBladeDialog = require("brjs/dashboard/app/model/dialog/newDialog/NewBladeDialog");
var ImportMotifDialog = require("brjs/dashboard/app/model/dialog/importDialog/ImportMotifDialog");
var ImportBladesFromAppDialog = require("brjs/dashboard/app/model/dialog/importDialog/ImportBladesFromAppDialog");
var TestRunnerDialog = require("brjs/dashboard/app/model/dialog/TestRunnerDialog");
var NotificationDialog = require("brjs/dashboard/app/model/dialog/NotificationDialog");
var BrowserWarningDialog = require("brjs/dashboard/app/model/dialog/BrowserWarningDialog");

function Dialog(oPresentationModel) {
	this.visible = new WritableProperty(false);
	this.visible.addChangeListener(this._onVisibilityChanged.bind(this));
	this.type = new Property(null);
	this.isClosable = new EditableProperty(true);
	this.viewNode = new MappedNodeList({}, DialogViewNode);
	this.m_oModal = new WolfSimpleBox();
	this.m_oModal.callOnClose(this._onClose.bind(this));
	this.m_oPresentationModel = oPresentationModel;
	this.m_sAppNamespace = '';
}

Core.extend(Dialog, PresentationNode);

Dialog.prototype.initialize = function() {
	this.newAppDialog = new NewAppDialog(this.m_oPresentationModel);
	this.newBladesetDialog = new NewBladesetDialog(this.m_oPresentationModel);
	this.newBladeDialog = new NewBladeDialog(this.m_oPresentationModel);
	this.importMotifDialog = new ImportMotifDialog(this.m_oPresentationModel);
	this.importBladesFromAppDialog = new ImportBladesFromAppDialog(this.m_oPresentationModel);
	this.testRunnerDialog = new TestRunnerDialog(this.m_oPresentationModel);
	this.notificationDialog = new NotificationDialog(this.m_oPresentationModel);
	this.browserWarningDialog = new BrowserWarningDialog(this.m_oPresentationModel);
};

Dialog.prototype.showDialog = function(sDialog) {
	if (this.m_oModal.hasContent() === false) {
		this.m_oModal.setContent($('#modalDialog'));
	}


	var oDialog = this[sDialog];
	this.visible.setValue(true);
	oDialog.initializeForm();
	this.m_oModal.setHasBackground(oDialog.hasBackground.getValue());
	this.isClosable.setValue(oDialog.isClosable.getValue());

	this.type._$setInternalValue(sDialog);
	this.viewNode.updateList({
		current: oDialog
	});
};

Dialog.prototype.displayNotification = function(sMessage) {
	var htmlMessage = sMessage.replace(/\n/g, '<br/>');

	this.notificationDialog.message.setValue(htmlMessage);
	this.showDialog('notificationDialog');
};

Dialog.prototype.getAppNamespace = function() {
	return this.m_sAppNamespace;
};

Dialog.prototype.setAppNamespace = function(sAppNamespace) {
	this.m_sAppNamespace = sAppNamespace;
};

Dialog.prototype._onVisibilityChanged = function(sDialogClass) {
	if (this.visible.getValue() === true) {
		this._openDialog();
	} else {
		this.m_oModal.hide();
	}
};

Dialog.prototype._openDialog = function(bPreventClose) {
	this.m_oModal.setClosable(this.isClosable.getValue());
	this.m_oModal.show();
};

Dialog.prototype._onClose = function(sDialogClass) {
	if (this.m_oPresentationModel) {
		this.m_oPresentationModel.appsScreen.updateApps();
		this.visible.setValue(false);
	}
};


module.exports = Dialog;
