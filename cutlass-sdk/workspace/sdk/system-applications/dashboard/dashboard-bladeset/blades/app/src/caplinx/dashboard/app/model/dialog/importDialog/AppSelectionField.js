caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField = function(oPresentationModel)
{
	this.m_oCurrentAppName = oPresentationModel.appDetailScreen.appName;
	this.m_oSourceApps = oPresentationModel.appsScreen.apps;
	
	// call super constructor
	caplin.presenter.node.SelectionField.call(this, this._getAppList(this.m_oCurrentAppName, this.m_oSourceApps));
	
	this.m_oCurrentAppName.addListener(new caplinx.dashboard.app.model.ConditionalChangeListener(
		this, "_onChange", oPresentationModel.dialog.type, "importBladesFromAppDialog"));
	this.m_oSourceApps.addListener(new caplinx.dashboard.app.model.ConditionalChangeListener(
		this, "_onChange", oPresentationModel.dialog.type, "importBladesFromAppDialog"));
};
caplin.extend(caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField, caplin.presenter.node.SelectionField);

caplinx.dashboard.app.model.dialog.importDialog.AppSelectionField.prototype._onChange = function()
{
	this.options.setOptions(this._getAppList(this.m_oCurrentAppName, this.m_oSourceApps));
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
