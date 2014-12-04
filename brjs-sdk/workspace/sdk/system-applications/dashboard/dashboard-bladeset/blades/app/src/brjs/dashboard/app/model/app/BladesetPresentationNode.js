brjs.dashboard.app.model.app.BladesetPresentationNode = function(sBladesetName, pBlades, oPresentationModel)
{
	this.m_oPresentationModel = oPresentationModel;
	this.m_fOnTestProgress = this._onTestProgress.bind(this);
	this.m_fOnTestError = this._onTestError.bind(this);
	this.m_oHoverTimeout = null;
	this.workbenchUrl = new br.presenter.property.WritableProperty();
	this.isBladeset = new br.presenter.property.WritableProperty(false);

	this.bladesetName = new br.presenter.property.Property(sBladesetName);
	this.bladeSetClasses = new br.presenter.property.EditableProperty("bladeset");
	this.blades = new br.presenter.node.NodeList(this._getBladePresentationModels(pBlades || []),
		brjs.dashboard.app.model.app.BladePresentationNode);
	if(this.m_oPresentationModel && sBladesetName != "default")
	{
		this.isBladeset.setValue(true);
		var appName = this.m_oPresentationModel.appDetailScreen.appName.getValue();
		var sWorkbenchPopoutUrl = "/" + appName + "/" + sBladesetName + "/workbench/";
		this.workbenchUrl.setValue(sWorkbenchPopoutUrl);
	}
};
br.Core.extend(brjs.dashboard.app.model.app.BladesetPresentationNode, br.presenter.node.PresentationNode);

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype.getBlade = function(sBlade)
{
	var pNodes = this.blades.nodes("*", {bladeName:sBlade}).getNodesArray();
	
	return (pNodes.length == 1) ? pNodes[0] : null;
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype.newBlade = function()
{
	this.m_oPresentationModel.dialog.newBladeDialog.setBladeset(this.bladesetName.getValue());
	this.m_oPresentationModel.dialog.showDialog("newBladeDialog");
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype.runAllTests = function()
{
	if(this.m_oPresentationModel.getDashboardService().getTestRunInProgress())
	{
		this.m_oPresentationModel.dialog.displayNotification("Can't run tests. The test run is in progress.");
		return;
	}
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(true);
	
	var sApp = this.m_oPresentationModel.appDetailScreen.appName.getValue();
	var sBladeset = this.bladesetName.getValue();
	this.m_oPresentationModel.dialog.displayNotification("Running tests... (this may take a few minutes). Please wait for test results.");
	this.m_oPresentationModel.getDashboardService().runBladesetTests(sApp, sBladeset, this.m_fOnTestProgress, this.m_fOnTestError);
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype._onTestProgress = function(sTestOutput)
{	
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(false);
	this.m_oPresentationModel.dialog.showDialog("testRunnerDialog");
	this.m_oPresentationModel.dialog.testRunnerDialog.displayTestResults();
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype._onTestError = function(sErrorMessage)
{
	this.m_oPresentationModel.getDashboardService().setTestRunInProgress(false);
	this.m_oPresentationModel.dialog.showDialog("testRunnerDialog");
	this.m_oPresentationModel.dialog.testRunnerDialog.displayTestError(sErrorMessage);
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype.popoutWorkbench = function()
{
	this.m_oPresentationModel.getWindowOpenerService().openWindow(this.workbenchUrl.getValue());
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype._getBladePresentationModels = function(pBlades)
{
	var pBladePresentationModels = [];
	
	for(var i = 0, l = pBlades.length; i < l; ++i)
	{
		var sBladeName = pBlades[i];
		var oBladePresentationModel = new brjs.dashboard.app.model.app.BladePresentationNode(sBladeName, this,
			this.m_oPresentationModel);
		
		pBladePresentationModels.push(oBladePresentationModel);
	}
	
	return pBladePresentationModels;
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype.onBladesetMouseOver = function()
{
	clearTimeout( this.m_oHoverTimeout );
	this.bladeSetClasses.setValue("bladeset hover");
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype.onBladesetMouseOut = function()
{
	this.m_oHoverTimeout = setTimeout( this._removeHoverClass.bind( this ), 150 );
};

brjs.dashboard.app.model.app.BladesetPresentationNode.prototype._removeHoverClass = function()
{
	this.bladeSetClasses.setValue("bladeset");
};