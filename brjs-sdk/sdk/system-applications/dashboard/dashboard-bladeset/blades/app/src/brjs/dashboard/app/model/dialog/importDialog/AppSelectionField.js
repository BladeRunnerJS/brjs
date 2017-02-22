'use strict';

var Core = require('br/Core');
var SelectionField = require('br/presenter/node/SelectionField');
var ConditionalChangeListener = require("brjs/dashboard/app/model/ConditionalChangeListener");

function AppSelectionField(oPresentationModel) {
	this._ = {
		currentAppName: oPresentationModel.appDetailScreen.appName,
		sourceApps: oPresentationModel.appsScreen.apps
	};

	// call super constructor
	SelectionField.call(this, this._getAppList(this._.currentAppName, this._.sourceApps));

	this._.currentAppName.addListener(new ConditionalChangeListener(
		this._onChange.bind(this), oPresentationModel.dialog.type, 'importBladesFromAppDialog'));
	this._.sourceApps.addListener(new ConditionalChangeListener(
		this._onChange.bind(this), oPresentationModel.dialog.type, 'importBladesFromAppDialog'));
}

Core.extend(AppSelectionField, SelectionField);

AppSelectionField.prototype._onChange = function() {
	this.options.setOptions(this._getAppList(this._.currentAppName, this._.sourceApps));
};

AppSelectionField.prototype._getAppList = function(oCurrentAppName, oSourceApps) {
	var pApps = oSourceApps.getPresentationNodesArray();
	var sImportApp = oCurrentAppName.getValue();
	var pAppList = [];

	for (var i = 0, l = pApps.length; i < l; ++i) {
		var oAppSummary = pApps[i];
		var sApp = oAppSummary.appName.getValue();

		if (sApp != sImportApp) {
			pAppList.push(sApp);
		}
	}

	return pAppList;
};

module.exports = AppSelectionField;
