'use strict';

var Core = require('br/Core');
var NodeList = require('br/presenter/node/NodeList');
var SelectableBladesetPresentationNode = require("brjs/dashboard/app/model/dialog/importDialog/SelectableBladesetPresentationNode");
var SelectableBladePresentationNode = require("brjs/dashboard/app/model/dialog/importDialog/SelectableBladePresentationNode");

function SelectableBladesetNodeList(oAppSelectionField, oPresentationModel, fNewBladesetNameValidationListener) {
	// call super constructor
	NodeList.call(this, [], SelectableBladesetPresentationNode);

	this.m_oAppSelectionField = oAppSelectionField;
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnAppInfoReceived = this._onAppInfoReceived.bind(this);
	this.m_fOnServiceError = this._onServiceError.bind(this);
	this.m_fNewBladesetNameValidationListener = fNewBladesetNameValidationListener;

	oAppSelectionField.value.addChangeListener(this, '_onSelectedAppChanged');
}

Core.extend(SelectableBladesetNodeList, NodeList);

SelectableBladesetNodeList.prototype._onSelectedAppChanged = function() {
	var sApp = this.m_oAppSelectionField.value.getValue();
	this.m_oPresentationModel.getDashboardService().getApp(sApp, this.m_fOnAppInfoReceived, this.m_fOnServiceError);
};

SelectableBladesetNodeList.prototype._onAppInfoReceived = function(mAppInfo) {
	var pBladesetPresentationModels = [];

	for (var sBladeset in mAppInfo) {
		var pBlades = mAppInfo[sBladeset];
		var pBladePresentationModels = [];

		for (var i = 0, l = pBlades.length; i < l; ++i) {
			var sBlade = pBlades[i];

			pBladePresentationModels.push(
				new SelectableBladePresentationNode(sBlade));
		}

		pBladesetPresentationModels.push(
			new SelectableBladesetPresentationNode(this.m_oPresentationModel, sBladeset, pBladePresentationModels,
				this.m_fNewBladesetNameValidationListener));
	}

	this.updateList(pBladesetPresentationModels);
};

SelectableBladesetNodeList.prototype._onServiceError = function(sFailureMessage) {
	this.updateList([]);
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};

module.exports = SelectableBladesetNodeList;
