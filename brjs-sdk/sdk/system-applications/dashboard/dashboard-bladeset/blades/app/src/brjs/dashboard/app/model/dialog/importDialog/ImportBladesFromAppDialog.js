'use strict';

var Core = require('br/Core');
var Button = require('br/presenter/node/Button');
var DialogViewNode = require("brjs/dashboard/app/model/dialog/DialogViewNode");
var AppSelectionField = require("brjs/dashboard/app/model/dialog/importDialog/AppSelectionField");
var SelectableBladesetNodeList = require("brjs/dashboard/app/model/dialog/importDialog/SelectableBladesetNodeList");

function ImportBladesFromAppDialog(oPresentationModel) {
	// call super constructor
	DialogViewNode.call(this, 'import-blades-from-app-dialog');

	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnSuccess = this._onSuccess.bind(this);
	this.m_fOnFailure = this._onFailure.bind(this);
	this.m_fNewBladesetNameValidationListener = this._updateDialog.bind(this);

	this.selectedApp = new AppSelectionField(oPresentationModel);
	this.bladesets = new SelectableBladesetNodeList(this.selectedApp, oPresentationModel, this.m_fNewBladesetNameValidationListener);
	this.importBladesButton = new Button('Import');

	this.bladesets.addChangeListener(this, '_onBladesetsChanged', true);
}

Core.extend(ImportBladesFromAppDialog, DialogViewNode);

ImportBladesFromAppDialog.prototype.initializeForm = function() {
	// do nothing
};

ImportBladesFromAppDialog.prototype.importBlades = function() {
	var sSourceApp = this.selectedApp.value.getValue();
	var mBlades = this._getBladesMap();
	var sTargetApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();

	this.m_oPresentationModel.getDashboardService().importBlades(
		sSourceApp, mBlades, sTargetApp, this.m_fOnSuccess, this.m_fOnFailure);
	this.m_oPresentationModel.dialog.visible.setValue(false);
};

ImportBladesFromAppDialog.prototype._onBladesetsChanged = function() {
	this.bladesets.properties('isSelected').addChangeListener(this, '_onSelectionChanged', true);
};

ImportBladesFromAppDialog.prototype._onSelectionChanged = function() {
	this._updateDialog();
};

ImportBladesFromAppDialog.prototype._getBladesMap = function() {
	var pBladesets = this.bladesets.getPresentationNodesArray();
	var mBlades = {};

	for (var si = 0, sl = pBladesets.length; si < sl; ++si) {
		var oBladesetPresentationNode = pBladesets[si];

		if (oBladesetPresentationNode.isSelected.getValue() || oBladesetPresentationNode.isIndeterminate.getValue()) {
			var sBladesetName = oBladesetPresentationNode.bladesetName.getValue();
			var sNewBladesetName = oBladesetPresentationNode.newBladesetName.value.getValue();
			var pBladePresentationNodes = oBladesetPresentationNode.blades.getPresentationNodesArray();
			var pBlades = [];

			for (var bi = 0, bl = pBladePresentationNodes.length; bi < bl; ++bi) {
				var oBladePresentationNode = pBladePresentationNodes[bi];

				if (oBladePresentationNode.isSelected.getValue()) {
					pBlades.push(oBladePresentationNode.bladeName.getValue());
				}
			}

			mBlades[sBladesetName] = {
				newBladesetName: sNewBladesetName,
				blades: pBlades
			};
		}
	}

	return mBlades;
};

ImportBladesFromAppDialog.prototype._onSuccess = function() {
	var sCurrentApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();

	this.m_oPresentationModel.appDetailScreen.displayApp(sCurrentApp);
};

ImportBladesFromAppDialog.prototype._onFailure = function(sFailureMessage) {
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};

ImportBladesFromAppDialog.prototype._updateDialog = function() {
	var pBladesetNodes = this.bladesets.getPresentationNodesArray();
	for (var i = 0; i < pBladesetNodes.length; i++) {
		var oBladesetNode = pBladesetNodes[i];
		if (oBladesetNode.newBladesetName.hasError.getValue() && (oBladesetNode.isSelected.getValue() || oBladesetNode.isIndeterminate.getValue())) {
			this.importBladesButton.enabled.setValue(false);
			return;
		}
	}

	if (this.bladesets.properties('isSelected', true).getSize() == 0) {
		this.importBladesButton.enabled.setValue(false);
	} else {
		this.importBladesButton.enabled.setValue(true);
	}
};

module.exports = ImportBladesFromAppDialog;
