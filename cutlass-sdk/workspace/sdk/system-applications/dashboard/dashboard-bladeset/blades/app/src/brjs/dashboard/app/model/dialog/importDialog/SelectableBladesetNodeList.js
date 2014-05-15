brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetNodeList = function(oAppSelectionField, oPresentationModel, fNewBladesetNameValidationListener)
{
	// call super constructor
	br.presenter.node.NodeList.call(this, [], brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode);
	
	this.m_oAppSelectionField = oAppSelectionField;
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnAppInfoReceived = this._onAppInfoReceived.bind(this);
	this.m_fOnServiceError = this._onServiceError.bind(this);
	this.m_fNewBladesetNameValidationListener = fNewBladesetNameValidationListener;
	
	oAppSelectionField.value.addChangeListener(this, "_onSelectedAppChanged");
};
br.Core.extend(brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetNodeList, br.presenter.node.NodeList);

brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetNodeList.prototype._onSelectedAppChanged = function()
{
	var sApp = this.m_oAppSelectionField.value.getValue();
	this.m_oPresentationModel.getDashboardService().getApp(sApp, this.m_fOnAppInfoReceived, this.m_fOnServiceError);
};

brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetNodeList.prototype._onAppInfoReceived = function(mAppInfo)
{
	var pBladesetPresentationModels = [];
	
	for(var sBladeset in mAppInfo)
	{
		var pBlades = mAppInfo[sBladeset];
		var pBladePresentationModels = [];
		
		for(var i = 0, l = pBlades.length; i < l; ++i)
		{
			var sBlade = pBlades[i];
			
			pBladePresentationModels.push(
				new brjs.dashboard.app.model.dialog.importDialog.SelectableBladePresentationNode(sBlade));
		}
		
		pBladesetPresentationModels.push(
			new brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetPresentationNode(this.m_oPresentationModel, sBladeset, pBladePresentationModels, 
					this.m_fNewBladesetNameValidationListener));
	}
	
	this.updateList(pBladesetPresentationModels);
};

brjs.dashboard.app.model.dialog.importDialog.SelectableBladesetNodeList.prototype._onServiceError = function(sFailureMessage)
{
	this.updateList([]);
	this.m_oPresentationModel.dialog.displayNotification(sFailureMessage);
};
