caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField = function(oPresentationModel)
{
	this._ = {
			currentAppName: oPresentationModel.appDetailScreen.appName,
			sourceApps: oPresentationModel.appsScreen.apps
	};
	
	// call super constructor
	br.presenter.node.SelectionField.call(this, this._getAppList(this._.currentAppName, this._.sourceApps));
	
	this._.currentAppName.addListener(new caplinx.dashboard.app.model.ConditionalChangeListener(
		this, "_onChange", oPresentationModel.dialog.type, "importBladesFromAppDialog"));
	this._.sourceApps.addListener(new caplinx.dashboard.app.model.ConditionalChangeListener(
		this, "_onChange", oPresentationModel.dialog.type, "importBladesFromAppDialog"));
};
br.Core.extend(caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField, br.presenter.node.SelectionField);

caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField.prototype._onChange = function()
{
	this.options.setOptions(this._getAppList(this._.currentAppName, this._.sourceApps));
};

caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField.prototype._getAppList = function(oCurrentAppName, oSourceApps)
{
	var pApps = oSourceApps.getPresentationNodesArray();
	var sImportApp = oCurrentAppName.getValue();
	var pAppList = [];
	
	for(var i = 0, l = pApps.length; i < l; ++i)
	{
		var oAppSummary = pApps[i];
		var sApp = oAppSummary.appName.getValue();
		
		if(sApp != sImportApp)
		{
			pAppList.push(sApp);
		}
		
	}
	
	return pAppList;
};
